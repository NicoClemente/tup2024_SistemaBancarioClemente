package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PrestamoEntity extends BaseEntity {
    private final Long numeroCliente;
    private final double montoPrestamo;
    private final int plazoMeses;
    private final String moneda;
    private final double tasaInteresAnual;
    private final LocalDateTime fechaSolicitud;
    private final String estado;
    private final List<CuotaPrestamoEntity> planPagos;
    private final int pagosRealizados;
    private final double saldoRestante;

    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId() != null ? prestamo.getId() : System.currentTimeMillis());
        this.numeroCliente = prestamo.getNumeroCliente();
        this.montoPrestamo = prestamo.getMontoPrestamo();
        this.plazoMeses = prestamo.getPlazoMeses();
        this.moneda = prestamo.getMoneda();
        this.tasaInteresAnual = prestamo.getTasaInteresAnual();
        this.fechaSolicitud = prestamo.getFechaSolicitud();
        this.estado = prestamo.getEstado() != null ? prestamo.getEstado().name() : null;
        this.pagosRealizados = prestamo.getPagosRealizados();
        this.saldoRestante = prestamo.getSaldoRestante();        
        this.planPagos = prestamo.getPlanPagos() != null ? 
            prestamo.getPlanPagos().stream()
                .map(CuotaPrestamoEntity::new)
                .collect(Collectors.toList()) : null;
    }

    public Prestamo toPrestamo() {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(this.getId());
        prestamo.setNumeroCliente(this.numeroCliente);
        prestamo.setMontoPrestamo(this.montoPrestamo);
        prestamo.setPlazoMeses(this.plazoMeses);
        prestamo.setMoneda(this.moneda);
        prestamo.setTasaInteresAnual(this.tasaInteresAnual);
        prestamo.setFechaSolicitud(this.fechaSolicitud);
        prestamo.setEstado(this.estado != null ? EstadoPrestamo.valueOf(this.estado) : null);
        prestamo.setPagosRealizados(this.pagosRealizados);
        prestamo.setSaldoRestante(this.saldoRestante);
        
        if (this.planPagos != null) {
            List<CuotaPrestamo> cuotas = this.planPagos.stream()
                .map(CuotaPrestamoEntity::toCuotaPrestamo)
                .collect(Collectors.toList());
            prestamo.setPlanPagos(cuotas);
        }
        
        return prestamo;
    }

    // Getters
    public Long getNumeroCliente() { return numeroCliente; }
    public double getMontoPrestamo() { return montoPrestamo; }
    public int getPlazoMeses() { return plazoMeses; }
    public String getMoneda() { return moneda; }
    public double getTasaInteresAnual() { return tasaInteresAnual; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public String getEstado() { return estado; }
    public List<CuotaPrestamoEntity> getPlanPagos() { return planPagos; }
    public int getPagosRealizados() { return pagosRealizados; }
    public double getSaldoRestante() { return saldoRestante; }
}
