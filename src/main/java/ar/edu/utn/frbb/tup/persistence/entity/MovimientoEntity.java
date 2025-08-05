package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Movimiento;
import ar.edu.utn.frbb.tup.model.TipoOperacion;

import java.time.LocalDateTime;

public class MovimientoEntity {
    private Long id;
    private LocalDateTime fechaHora;
    private String tipoOperacion;
    private double monto;
    private Long cuentaOrigen;
    private Long cuentaDestino;
    private String descripcion;

    public MovimientoEntity() {}

    public MovimientoEntity(Movimiento movimiento) {
        this.id = movimiento.getId();
        this.fechaHora = movimiento.getFechaHora();
        this.tipoOperacion = movimiento.getTipoOperacion().toString();
        this.monto = movimiento.getMonto();
        this.cuentaOrigen = movimiento.getCuentaOrigen();
        this.cuentaDestino = movimiento.getCuentaDestino();
        this.descripcion = movimiento.getDescripcion();
    }

    public Movimiento toMovimiento() {        
        return new Movimiento(
            this.id,
            this.fechaHora,
            TipoOperacion.valueOf(this.tipoOperacion),
            this.monto,
            this.cuentaOrigen,
            this.cuentaDestino,
            this.descripcion
        );
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Long getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(Long cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public Long getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(Long cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "MovimientoEntity{" +
                "id=" + id +
                ", fechaHora=" + fechaHora +
                ", tipoOperacion='" + tipoOperacion + '\'' +
                ", monto=" + monto +
                ", cuentaOrigen=" + cuentaOrigen +
                ", cuentaDestino=" + cuentaDestino +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}