package com.benchmark.casopractico2.Servicio;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "totalTasks", "threadsUsed", "results" })
public class BenchmarkSummary {
    private int totalTasks;
    private int threadsUsed;
    private List<BenchmarkResult> results;

    // Getters y setters
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    public int getThreadsUsed() { return threadsUsed; }
    public void setThreadsUsed(int threadsUsed) { this.threadsUsed = threadsUsed; }
    public List<BenchmarkResult> getResults() { return results; }
    public void setResults(List<BenchmarkResult> results) { this.results = results; }
}
