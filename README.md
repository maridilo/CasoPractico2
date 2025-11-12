https://github.com/maridilo/CasoPractico2.git
# Benchmarking de Estrategias de Programación Multihilo con Spring Boot

## Integrantes
- María Díaz - Heredero López - maridilo@myuax.com
- Cintia Santillan Garcia - csantill@myuax.com
- Suren Hashemi Alam - sjoorhas@myuax.com

## Objetivo
Comparar el rendimiento de tres estrategias de ejecución para un conjunto de tareas CPU‑bound en Java con Spring Boot:
1) Secuencial (un solo hilo)
2) Concurrente con `ExecutorService` (pool configurable)
3) Asíncrono con `@Async` de Spring

Se miden: tiempo total, promedio por tarea, *speedup* y *eficiencia*.

## Requisitos
-Spring Boot  
-Controlador REST  
-Servicio (@service) encargado de ejecutar las pruebas  
-Activación de ejecución asíncrona con @EnableAsync  

### Estrategias de ejecución  

## Cómo ejecutar
-Desde Postman con la siguiente URL:
http://localhost:8080/benchmark/start?tasks=50&threads=8

-Desde el siguiente link al ejecutar el código: 
http://localhost:8080/

## Endpoints
### 1) Iniciar benchmark
`POST /benchmark/start?tasks={N}&threads={M}`

Parámetros:
- `tasks` (int, por defecto 50): número de tareas simuladas.
- `threads` (int, por defecto 8): tamaño del pool para los modos concurrentes.

Ejemplo:
```bash
curl -X POST "http://localhost:8080/benchmark/start?tasks=50&threads=8"
```

Respuesta (ejemplo):
```json
{
  "totalTasks": 50,
  "threadsUsed": 8,
  "results": [
    { "mode": "SEQUENTIAL", "timeMs": 4321, "averageMs": 86.42, "speedup": 1.0, "efficiency": 1.0 },
    { "mode": "EXECUTOR_SERVICE", "timeMs": 987, "averageMs": 19.74, "speedup": 4.38, "efficiency": 0.55 },
    { "mode": "SPRING_ASYNC", "timeMs": 1012, "averageMs": 20.24, "speedup": 4.27, "efficiency": 0.53 }
  ]
}
```

### 2) Último resultado
`GET /benchmark/result`

Devuelve el último `BenchmarkSummary` ejecutado.

```bash
curl "http://localhost:8080/benchmark/result"
```

### 3) Modos disponibles
`GET /benchmark/modes`

Devuelve la lista de modos y su descripción.
```bash
curl "http://localhost:8080/benchmark/modes"
```

## Métricas
- **Tiempo total (ms)**: duración por modo (medido con `System.nanoTime()`).
- **Promedio por tarea (ms)**: `tiempo_total / tasks`.
- **Speedup**: `T_secuencial / T_modo`.
- **Eficiencia**: `speedup / hilos` (para modos concurrentes).

## Estrategias implementadas
- **SEQUENTIAL**: ejecución monohilo.
- **EXECUTOR_SERVICE**: `Executors.newFixedThreadPool(threads)`; envío de `Runnable`/`Callable` y espera con `invokeAll`/`Future`.
- **SPRING_ASYNC**: métodos anotados con `@Async("taskExecutor")` y espera de `Future/CompletableFuture`.

## Configuración de hilos
Archivo: `src/main/java/com/benchmark/casopractico2/Config/AsyncConfig.java`
```java
@Bean(name = "taskExecutor")
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(16);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Async-");
    executor.initialize();
    return executor;
}
```
Ajustar tamaños según el entorno. El parámetro `threads` del endpoint controla los modos concurrentes (pool fijo en `ExecutorService`).

> Nota: existe `AsyncTaskService` con `@Async("benchmarkExecutor")`. Si se usa, alinear el nombre del *bean* con `AsyncConfig` o añadir un *bean* adicional llamado `"benchmarkExecutor"` para evitar *NoSuchBeanDefinitionException*.

## Tareas simuladas (carga)
Clase: `Servicio/ComputationalTask.java`  
Simula carga CPU (búsqueda de primos en rango). Es una tarea **CPU‑bound** sin acceso a recursos compartidos.

## Estructura del proyecto (archivos relevantes)
- `src/main/java/com/benchmark/casopractico2/Main.java` — clase principal; `@SpringBootApplication` y `@EnableAsync`.
- `src/main/java/com/benchmark/casopractico2/Config/AsyncConfig.java` — configuración del `ThreadPoolTaskExecutor` (Spring Async).
- `src/main/java/com/benchmark/casopractico2/Controlador/BenchmarkController.java` — endpoints `/benchmark` (`/start`, `/result`, `/modes`).
- `src/main/java/com/benchmark/casopractico2/Servicio/BenchmarkService.java` — lógica del benchmark, medición, speedup y eficiencia.
- `src/main/java/com/benchmark/casopractico2/Servicio/BenchmarkSummary.java` — DTO de salida global.
- `src/main/java/com/benchmark/casopractico2/Servicio/BenchmarkResult.java` — DTO por modo.
- `src/main/java/com/benchmark/casopractico2/Servicio/ComputationalTask.java` — carga computacional.
- `src/main/java/com/benchmark/casopractico2/Servicio/AsyncTaskService.java` — helper asíncrono (ver nota de bean).
- `src/main/resources/application.properties` — nombre de aplicación.
- `pom.xml` — proyecto Maven (Spring Boot 3.x), construcción y *plugin* de empaquetado.

## Suposiciones y consideraciones
- Las tareas son independientes, no hay sincronización explícita. Si se añadieran recursos compartidos, proteger con primitivas (`synchronized`, `Lock`, `Atomic*`, etc.).
- El *speedup* real depende del hardware, del *scheduler* del SO y de otros procesos.
- Para cargas **CPU‑bound**, aumentar hilos por encima de núcleos no siempre mejora; medir y elegir el tamaño adecuado del pool.

## Cómo repetir y comparar
Ejecutar varias veces por modo para minimizar ruido:
```bash
for t in 1 2 4 8 16; do
  curl -s -X POST "http://localhost:8080/benchmark/start?tasks=100&threads=$t" | jq '.results'
done
```
Registrar tiempos, *speedup* y *eficiencia* para cada `threads` y justificar conclusiones.

## Pruebas
Incluye `CasoPractico2ApplicationTests` (carga de contexto). Se pueden añadir pruebas de servicio midiendo tiempos sintéticos.

