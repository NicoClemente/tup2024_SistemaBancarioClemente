package ar.edu.utn.frbb.tup.model;

import java.time.LocalDateTime;
import java.util.List;

public class Prestamo {
    private Long id;
    private Long numeroCliente;
    private double montoPrestamo;
    private int plazoMeses;
    private String moneda;
    private double tasaInteresAnual; 
    private LocalDateTime fechaSolicitud;
    private EstadoPrestamo estado;
    private List<CuotaPrestamo> planPagos;
    private int pagosRealizados;
    private double saldoRestante;

    public Prestamo() {
        this.fechaSolicitud = LocalDateTime.now();
        this.tasaInteresAnual = 0.05;
        this.pagosRealizados = 0;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNumeroCliente() { return numeroCliente; }
    public void setNumeroCliente(Long numeroCliente) { this.numeroCliente = numeroCliente; }

    public double getMontoPrestamo() { return montoPrestamo; }
    public void setMontoPrestamo(double montoPrestamo) { 
        this.montoPrestamo = montoPrestamo;
        this.saldoRestante = montoPrestamo;
    }

    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public double getTasaInteresAnual() { return tasaInteresAnual; }
    public void setTasaInteresAnual(double tasaInteresAnual) { this.tasaInteresAnual = tasaInteresAnual; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public List<CuotaPrestamo> getPlanPagos() { return planPagos; }
    public void setPlanPagos(List<CuotaPrestamo> planPagos) { this.planPagos = planPagos; }

    public int getPagosRealizados() { return pagosRealizados; }
    public void setPagosRealizados(int pagosRealizados) { this.pagosRealizados = pagosRealizados; }

    public double getSaldoRestante() { return saldoRestante; }
    public void setSaldoRestante(double saldoRestante) { this.saldoRestante = saldoRestante; }

    /**
     * Calcula el monto de cuota mensual fija usando fórmula de amortización francesa
     * PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
     * Donde: P = monto principal, r = tasa mensual, n = número de cuotas
     */
    public double calcularCuotaMensual() {
        double tasaMensual = tasaInteresAnual / 12;
        double factor = Math.pow(1 + tasaMensual, plazoMeses);
        return montoPrestamo * (tasaMensual * factor) / (factor - 1);
    }
}