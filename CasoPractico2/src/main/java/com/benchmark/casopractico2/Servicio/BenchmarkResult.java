package com.benchmark.casopractico2.Servicio;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "mode", "timeMs", "averageMs", "speedup", "efficiency" })
public class BenchmarkResult {
    private String mode;
    private long timeMs;
    private double averageMs;
    private double speedup;
    private double efficiency;

    // Getters y setters
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public long getTimeMs() { return timeMs; }
    public void setTimeMs(long timeMs) { this.timeMs = timeMs; }
    public double getAverageMs() { return averageMs; }
    public void setAverageMs(double averageMs) { this.averageMs = averageMs; }
    public double getSpeedup() { return speedup; }
    public void setSpeedup(double speedup) { this.speedup = speedup; }
    public double getEfficiency() { return efficiency; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }
}
