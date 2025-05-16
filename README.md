# Índice Invertido en Java , laboratorio 2

Este proyecto implementa un **índice invertido** utilizando Java y programación concurrente con **virtual threads** para procesar grandes volúmenes de texto de forma eficiente.
El índice invertido permite mapear cada palabra a los documentos en los que aparece.

## Características

- Procesamiento concurrente de archivos de texto mediante `ExecutorService` con hilos virtuales.
- Uso de `ConcurrentHashMap` para manejar el índice de forma segura en entornos multihilo.
- Lectura en lotes (`batch`) de palabras para mejorar el rendimiento.
- Imprime una muestra del índice generado.
- Mide el tiempo de ejecución del proceso.

## Estructura del Índice

```java
ConcurrentMap<String, Set<Integer>> indice;
```

Cada palabra (`String`) se mapea a un conjunto de identificadores de documentos (`Set<Integer>`).

## Ejemplo de salida

```
hola -> [0, 2]
mundo -> [1]
java -> [0, 1, 2]
...
```

## Archivos de entrada

El índice se construye a partir de archivos de texto. En el `main`, se especifican 3 archivos de ejemplo:

```java
List<Path> archivos = List.of(
    Path.of("salida_5gbTexto1.txt"),
    Path.of("salida_5gbTexto2.txt"),
    Path.of("salida_5gbTexto3.txt")
);
```

Puedes modificar esta lista para incluir tus propios archivos de texto.

## Cómo ejecutar

### Requisitos

- Java 21 o superior (para usar hilos virtuales).
- Archivos de texto con una palabra por línea.

### Compilación y ejecución

```bash
javac IndiceInvertido.java
java IndiceInvertido
```

### Ejemplo de salida

```
Procesado: salida_5gbTexto1.txt | Quedan: 2 archivos
Procesado: salida_5gbTexto2.txt | Quedan: 1 archivos
Procesado: salida_5gbTexto3.txt | Quedan: 0 archivos
hola -> [0]
mundo -> [0, 1]
java -> [2]
Tiempo de indexado: 4.73 s
Tiempo total (incluye impresión): 4.74 s
```

## Notas

- Este proyecto está optimizado para trabajar con archivos grandes (GBs de texto).
- Se usa `parallelStream` para paralelizar el procesamiento de palabras en cada lote.
- El método `imprimirIndice()` imprime solo las primeras 10 entradas del índice como muestra.

## Licencia

Este proyecto está disponible bajo la licencia MIT. Puedes usarlo, modificarlo y distribuirlo libremente.

