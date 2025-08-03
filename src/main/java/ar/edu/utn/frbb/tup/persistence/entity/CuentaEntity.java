package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CuentaEntity extends BaseEntity{
    String nombre;
    LocalDateTime fechaCreacion;
    double balance;
    String tipoCuenta;
    String tipoMoneda;
    Long titular;
    long numeroCuenta;
    List<MovimientoEntity> movimientos;

    public CuentaEntity(Cuenta cuenta) {
        super(cuenta.getNumeroCuenta());
        this.balance = cuenta.getBalance();
        this.tipoCuenta = cuenta.getTipoCuenta().toString();
        this.tipoMoneda = cuenta.getMoneda().toString();
        this.titular = cuenta.getTitular() != null ? cuenta.getTitular().getDni() : null;
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.movimientos = new ArrayList<>();
        
        if (cuenta.getMovimientos() != null) {
            for (Movimiento m : cuenta.getMovimientos()) {
                this.movimientos.add(new MovimientoEntity(m));
            }
        }
    }

    public Cuenta toCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(this.balance);
        cuenta.setNumeroCuenta(this.numeroCuenta);
        cuenta.setTipoCuenta(TipoCuenta.valueOf(this.tipoCuenta));
        cuenta.setMoneda(TipoMoneda.valueOf(this.tipoMoneda));
        cuenta.setFechaCreacion(this.fechaCreacion);
                
        List<Movimiento> movimientosList = new ArrayList<>();
        if (this.movimientos != null) {
            for (MovimientoEntity me : this.movimientos) {
                movimientosList.add(me.toMovimiento());
            }
        }
        cuenta.setMovimientos(movimientosList);
        
        return cuenta;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public Long getTitular() {
        return titular;
    }

    public void setTitular(Long titular) {
        this.titular = titular;
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public List<MovimientoEntity> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoEntity> movimientos) {
        this.movimientos = movimientos;
    }
}