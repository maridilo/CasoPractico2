package com.benchmark.casopractico2.Servicio;


import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class BenchmarkService {

    private BenchmarkSummary lastResult;

    public BenchmarkSummary getLastResult() {
        return lastResult;
    }

    public BenchmarkSummary runBenchmark(int totalTasks, int threads) throws Exception {
        List<BenchmarkResult> results = new ArrayList<>();

        // 1️⃣ Secuencial
        BenchmarkResult sequential = runSequential(totalTasks);
        results.add(sequential);

        // 2️⃣ ExecutorService
        BenchmarkResult executor = runWithExecutor(totalTasks, threads, sequential.getTimeMs());
        results.add(executor);

        // 3️⃣ Spring @Async
        BenchmarkResult async = runWithSpringAsync(totalTasks, threads, sequential.getTimeMs());
        results.add(async);

        BenchmarkSummary summary = new BenchmarkSummary();
        summary.setTotalTasks(totalTasks);
        summary.setThreadsUsed(threads);
        summary.setResults(results);

        lastResult = summary; // Guardamos el último resultado

        return summary;
    }

    // ----------------- Métodos auxiliares -----------------

    private BenchmarkResult runSequential(int tasks) {
        long start = System.nanoTime();
        for (int i = 0; i < tasks; i++) {
            performTask();
        }
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;
        double avgMs = ((double) durationMs) / tasks;

        BenchmarkResult result = new BenchmarkResult();
        result.setMode("SEQUENTIAL");
        result.setTimeMs(durationMs);
        result.setAverageMs(round(avgMs));
        result.setSpeedup(1.0);
        result.setEfficiency(1.0);
        return result;
    }

    private BenchmarkResult runWithExecutor(int tasks, int threads, long sequentialTimeMs) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            callables.add(() -> { performTask(); return null; });
        }

        long start = System.nanoTime();
        executor.invokeAll(callables);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;
        double avgMs = ((double) durationMs) / tasks;

        BenchmarkResult result = new BenchmarkResult();
        result.setMode("EXECUTOR_SERVICE");
        result.setTimeMs(durationMs);
        result.setAverageMs(round(avgMs));
        result.setSpeedup(round((double) sequentialTimeMs / durationMs));
        result.setEfficiency(round((double) sequentialTimeMs / durationMs / threads));
        return result;
    }

    private BenchmarkResult runWithSpringAsync(int tasks, int threads, long sequentialTimeMs) throws Exception {
        List<Future<Void>> futures = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < tasks; i++) {
            futures.add(performAsyncTask());
        }
        for (Future<Void> f : futures) {
            f.get();
        }
        long end = System.nanoTime();

        long durationMs = (end - start) / 1_000_000;
        double avgMs = ((double) durationMs) / tasks;

        BenchmarkResult result = new BenchmarkResult();
        result.setMode("SPRING_ASYNC");
        result.setTimeMs(durationMs);
        result.setAverageMs(round(avgMs));
        result.setSpeedup(round((double) sequentialTimeMs / durationMs));
        result.setEfficiency(round((double) sequentialTimeMs / durationMs / threads));
        return result;
    }

    @Async("taskExecutor")
    public Future<Void> performAsyncTask() {
        performTask();
        return new AsyncResult<>(null);
    }

    // Simula tarea pesada (CPU)
    private void performTask() {
        long sum = 0;
        for (int i = 1; i <= 100_000; i++) sum += i;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
