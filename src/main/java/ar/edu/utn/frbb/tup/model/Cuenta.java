package ar.edu.utn.frbb.tup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cuenta {
    private long numeroCuenta;
    private LocalDateTime fechaCreacion;
    private double balance;
    private TipoCuenta tipoCuenta;
    private Cliente titular;
    private TipoMoneda moneda;
    private List<Movimiento> movimientos;  

    public Cuenta() {
        this.numeroCuenta = Math.abs(new Random().nextLong());
        this.balance = 0;
        this.fechaCreacion = LocalDateTime.now();
        this.movimientos = new ArrayList<>();  
    }
    
    //Getters y Setters

    public Cliente getTitular() {
        return titular;
    }

    public void setTitular(Cliente titular) {
        this.titular = titular;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public Cuenta setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
        return this;
    }

    public TipoMoneda getMoneda() {
        return moneda;
    }

    public Cuenta setMoneda(TipoMoneda moneda) {
        this.moneda = moneda;
        return this;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Cuenta setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public Cuenta setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public void agregarMovimiento(Movimiento movimiento) {
        this.movimientos.add(movimiento);
    }

    public void depositar(double monto) throws CantidadNegativaException {
        if (monto <= 0) {
            throw new CantidadNegativaException();
        }
        this.balance += monto;
        
        Movimiento movimiento = new Movimiento(TipoOperacion.DEPOSITO, monto, this.numeroCuenta);
        movimiento.setDescripcion("Depósito en cuenta");
        agregarMovimiento(movimiento);
    }

    public void debitarDeCuenta(double cantidadADebitar) throws NoAlcanzaException, CantidadNegativaException {
        if (cantidadADebitar <= 0) {
            throw new CantidadNegativaException();
        }

        if (balance < cantidadADebitar) {
            throw new NoAlcanzaException();
        }
        this.balance -= cantidadADebitar;
        
        Movimiento movimiento = new Movimiento(TipoOperacion.RETIRO, cantidadADebitar, this.numeroCuenta);
        movimiento.setDescripcion("Retiro de cuenta");
        agregarMovimiento(movimiento);
    }

    public void transferirA(Cuenta cuentaDestino, double monto) throws NoAlcanzaException, CantidadNegativaException {
        if (monto <= 0) {
            throw new CantidadNegativaException();
        }
        
        if (balance < monto) {
            throw new NoAlcanzaException();
        }
        
        this.balance -= monto;
        Movimiento movimientoEnvio = new Movimiento(TipoOperacion.TRANSFERENCIA_ENVIADA, monto, this.numeroCuenta, cuentaDestino.getNumeroCuenta());
        movimientoEnvio.setDescripcion("Transferencia enviada a cuenta " + cuentaDestino.getNumeroCuenta());
        this.agregarMovimiento(movimientoEnvio);
        
        cuentaDestino.balance += monto;
        Movimiento movimientoRecepcion = new Movimiento(TipoOperacion.TRANSFERENCIA_RECIBIDA, monto, this.numeroCuenta, cuentaDestino.getNumeroCuenta());
        movimientoRecepcion.setDescripcion("Transferencia recibida de cuenta " + this.numeroCuenta);
        cuentaDestino.agregarMovimiento(movimientoRecepcion);
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "numeroCuenta=" + numeroCuenta +
                ", fechaCreacion=" + fechaCreacion +
                ", balance=" + balance +
                ", tipoCuenta=" + tipoCuenta +
                ", moneda=" + moneda +
                '}';
    }
}