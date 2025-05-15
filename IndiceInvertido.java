import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IndiceInvertido {

    private final ConcurrentMap<String, Set<Integer>> indice = new ConcurrentHashMap<>();

    private void procesarArchivo(Path archivo, int docId, AtomicInteger archivosRestantes) {
        try (BufferedReader reader = Files.newBufferedReader(archivo)) {
            List<String> batch = new ArrayList<>();
            String palabra;
            final int batchSize = 10_000;

            while ((palabra = reader.readLine()) != null) {
                palabra = palabra.toLowerCase().trim(); //tratamiento
                if (!palabra.isEmpty()) {
                    batch.add(palabra);
                }

                if (batch.size() >= batchSize) {
                    procesarBatch(batch, docId);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                procesarBatch(batch, docId);
            }

            int restantes = archivosRestantes.decrementAndGet();
            System.out.println(" Procesado: " + archivo.getFileName() + " | Quedan: " + restantes + " archivos");

        } catch (IOException e) {
            System.err.println(" Error al leer archivo " + archivo + ": " + e.getMessage());
        }
    }

    private void procesarBatch(List<String> palabras, int docId) {
        palabras.parallelStream().forEach(palabra ->
            indice.computeIfAbsent(palabra, k -> ConcurrentHashMap.newKeySet()).add(docId)
        );
    }

    public void construirIndice(List<Path> archivos) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        AtomicInteger docId = new AtomicInteger(0);
        AtomicInteger archivosRestantes = new AtomicInteger(archivos.size());
        List<Future<?>> tareas = new ArrayList<>();

        for (Path archivo : archivos) {
            int id = docId.getAndIncrement();
            tareas.add(executor.submit(() -> procesarArchivo(archivo, id, archivosRestantes)));
        }

        for (Future<?> tarea : tareas) {
            try {
                tarea.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    public void imprimirIndice() {
        int limite = 10;
        int contador = 0;

        for (Map.Entry<String, Set<Integer>> entrada : indice.entrySet()) {
            System.out.println(entrada.getKey() + " -> " + entrada.getValue());
            contador++;
            if (contador >= limite) break;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Path> archivos = List.of(
            Path.of("salida_5gbTexto1.txt"),
            Path.of("salida_5gbTexto2.txt"),
            Path.of("salida_5gbTexto3.txt")
        );

        IndiceInvertido ii = new IndiceInvertido();

        long inicio = System.currentTimeMillis();
        ii.construirIndice(archivos);
        long finIndexado = System.currentTimeMillis();

        ii.imprimirIndice();
        long finTotal = System.currentTimeMillis();

        System.out.printf(" Tiempo de indexado: %.2f s\n", (finIndexado - inicio) / 1000.0);
        System.out.printf(" Tiempo total (incluye impresión): %.2f s\n", (finTotal - inicio) / 1000.0);
    }
}

