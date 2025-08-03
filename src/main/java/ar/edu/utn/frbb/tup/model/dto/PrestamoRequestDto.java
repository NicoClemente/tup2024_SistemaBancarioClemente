package ar.edu.utn.frbb.tup.model.dto;

public class PrestamoRequestDto {
    private long numeroCliente;
    private int plazoMeses;
    private double montoPrestamo;
    private String moneda;

    // Getters y Setters
    public long getNumeroCliente() { return numeroCliente; }
    public void setNumeroCliente(long numeroCliente) { this.numeroCliente = numeroCliente; }

    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }

    public double getMontoPrestamo() { return montoPrestamo; }
    public void setMontoPrestamo(double montoPrestamo) { this.montoPrestamo = montoPrestamo; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}