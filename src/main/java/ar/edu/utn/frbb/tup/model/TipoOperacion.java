package ar.edu.utn.frbb.tup.model;

public enum TipoOperacion {
    DEPOSITO("Depósito"),
    RETIRO("Retiro"),
    TRANSFERENCIA_ENVIADA("Transferencia Enviada"),
    TRANSFERENCIA_RECIBIDA("Transferencia Recibida"),
    CONSULTA_SALDO("Consulta de Saldo");

    private final String descripcion;

    TipoOperacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}