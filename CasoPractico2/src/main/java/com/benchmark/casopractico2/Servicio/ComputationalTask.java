package com.benchmark.casopractico2.Servicio;


import java.util.Random;

public class ComputationalTask implements Runnable {

    private final int taskId;

    public ComputationalTask(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        // Simula una carga de CPU: buscar primos o hashes
        int count = 0;
        for (int i = 2; i < 10000; i++) {
            if (isPrime(i)) count++;
        }
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
