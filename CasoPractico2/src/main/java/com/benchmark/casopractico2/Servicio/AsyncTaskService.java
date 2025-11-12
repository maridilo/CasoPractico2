package com.benchmark.casopractico2.Servicio;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTaskService {

    @Async("benchmarkExecutor")
    public CompletableFuture<Void> executeTask(int taskId) {
        new ComputationalTask(taskId).run();
        return CompletableFuture.completedFuture(null);
    }
}
