package ar.edu.utn.frbb.tup.model;

public class CuotaPrestamo {
    private int cuotaNro;
    private double monto;

    public CuotaPrestamo() {}

    public CuotaPrestamo(int cuotaNro, double monto) {
        this.cuotaNro = cuotaNro;
        this.monto = monto;
    }

    // Getters y Setters
    public int getCuotaNro() { return cuotaNro; }
    public void setCuotaNro(int cuotaNro) { this.cuotaNro = cuotaNro; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
}