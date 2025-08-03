package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.CuotaPrestamo;

public class CuotaPrestamoEntity {
    private final int cuotaNro;
    private final double monto;

    public CuotaPrestamoEntity(CuotaPrestamo cuota) {
        this.cuotaNro = cuota.getCuotaNro();
        this.monto = cuota.getMonto();
    }

    public CuotaPrestamo toCuotaPrestamo() {
        return new CuotaPrestamo(this.cuotaNro, this.monto);
    }

    public int getCuotaNro() { return cuotaNro; }
    public double getMonto() { return monto; }
}