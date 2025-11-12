package com.benchmark.casopractico2.Controlador;


import com.benchmark.casopractico2.Servicio.BenchmarkService;
import com.benchmark.casopractico2.Servicio.BenchmarkSummary;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    // Inicia el benchmark
    @PostMapping("/start")
    public BenchmarkSummary start(@RequestParam(defaultValue = "50") int tasks,
                                  @RequestParam(defaultValue = "8") int threads) throws Exception {
        return benchmarkService.runBenchmark(tasks, threads);
    }

    // Devuelve último resultado
    @GetMapping("/result")
    public BenchmarkSummary getResult() {
        return benchmarkService.getLastResult();
    }

    // Lista modos disponibles
    @GetMapping("/modes")
    public Object getModes() {
        return new Object[]{
                new ModeInfo("SEQUENTIAL", "Ejecución en un solo hilo"),
                new ModeInfo("EXECUTOR_SERVICE", "Pool de hilos manual con ExecutorService"),
                new ModeInfo("SPRING_ASYNC", "Ejecución asíncrona con @Async de Spring")
        };
    }

    // Clase interna para modo
    static class ModeInfo {
        public String mode;
        public String description;
        public ModeInfo(String mode, String description) {
            this.mode = mode;
            this.description = description;
        }
    }
}
