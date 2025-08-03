package ar.edu.utn.frbb.tup.model;

public enum EstadoPrestamo {
    PENDIENTE("PENDIENTE"),
    APROBADO("APROBADO"), 
    RECHAZADO("RECHAZADO"),
    ACTIVO("ACTIVO"),
    PAGADO("PAGADO");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}