package ar.edu.utn.frbb.tup.model.exception;

public class NoAlcanzaException extends Exception {
    
    public NoAlcanzaException() {
        super("Saldo insuficiente para realizar la operación");
    }
    
    public NoAlcanzaException(String mensaje) {
        super(mensaje);
    }
}