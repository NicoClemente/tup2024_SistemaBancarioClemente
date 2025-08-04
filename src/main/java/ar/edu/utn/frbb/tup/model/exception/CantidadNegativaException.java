package ar.edu.utn.frbb.tup.model.exception;

public class CantidadNegativaException extends Exception {
    
    public CantidadNegativaException() {
        super("El monto debe ser mayor a cero");
    }
    
    public CantidadNegativaException(String mensaje) {
        super(mensaje);
    }
}