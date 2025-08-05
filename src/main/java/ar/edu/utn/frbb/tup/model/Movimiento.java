package ar.edu.utn.frbb.tup.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Movimiento {
    private static final AtomicLong contadorId = new AtomicLong(1L);
    
    private Long id;
    private LocalDateTime fechaHora;
    private TipoOperacion tipoOperacion;
    private double monto;
    private Long cuentaOrigen;
    private Long cuentaDestino;
    private String descripcion;

    public Movimiento() {
        this.id = contadorId.getAndIncrement();
        this.fechaHora = LocalDateTime.now();
    }

    // Constructor para la capa de persistencia
    public Movimiento(Long id, LocalDateTime fechaHora, TipoOperacion tipoOperacion, 
                     double monto, Long cuentaOrigen, Long cuentaDestino, String descripcion) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.descripcion = descripcion;
    }

    public Movimiento(TipoOperacion tipoOperacion, double monto, Long cuentaOrigen) {
        this();
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
        this.cuentaOrigen = cuentaOrigen;
    }

    public Movimiento(TipoOperacion tipoOperacion, double monto, Long cuentaOrigen, Long cuentaDestino) {
        this(tipoOperacion, monto, cuentaOrigen);
        this.cuentaDestino = cuentaDestino;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {        
        if (this.id == null) {
            this.id = id;
        }
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
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
        return "Movimiento{" +
                "id=" + id +
                ", fechaHora=" + fechaHora +
                ", tipoOperacion=" + tipoOperacion +
                ", monto=" + monto +
                ", cuentaOrigen=" + cuentaOrigen +
                ", cuentaDestino=" + cuentaDestino +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}