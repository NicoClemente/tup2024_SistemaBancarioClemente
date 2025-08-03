package ar.edu.utn.frbb.tup.model.dto;

public class OperacionDto {
    private Long numeroCuenta;
    private double monto;
    private String tipoOperacion;
    private Long numeroCuentaDestino;

    public Long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Long getNumeroCuentaDestino() {
        return numeroCuentaDestino;
    }

    public void setNumeroCuentaDestino(Long numeroCuentaDestino) {
        this.numeroCuentaDestino = numeroCuentaDestino;
    }
}