package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.dto.PrestamoRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PrestamoValidator {

    public void validate(PrestamoRequestDto request) {
        validateNumeroCliente(request.getNumeroCliente());
        validateMontoPrestamo(request.getMontoPrestamo());
        validatePlazoMeses(request.getPlazoMeses());
        validateMoneda(request.getMoneda());
    }

    private void validateNumeroCliente(long numeroCliente) {
        if (numeroCliente <= 0) {
            throw new IllegalArgumentException("El número de cliente debe ser positivo");
        }
    }

    private void validateMontoPrestamo(double montoPrestamo) {
        if (montoPrestamo <= 0) {
            throw new IllegalArgumentException("El monto del préstamo debe ser mayor a cero");
        }
        
        
        if (montoPrestamo > 1000000) {
            throw new IllegalArgumentException("El monto del préstamo no puede superar $1.000.000");
        }
        
        
        if (montoPrestamo < 1000) {
            throw new IllegalArgumentException("El monto del préstamo debe ser de al menos $1.000");
        }
    }

    private void validatePlazoMeses(int plazoMeses) {
        if (plazoMeses <= 0) {
            throw new IllegalArgumentException("El plazo en meses debe ser positivo");
        }
        
        
        if (plazoMeses < 3) {
            throw new IllegalArgumentException("El plazo mínimo es de 3 meses");
        }
        
        if (plazoMeses > 60) {
            throw new IllegalArgumentException("El plazo máximo es de 60 meses");
        }
    }

    private void validateMoneda(String moneda) {
        if (moneda == null || moneda.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        
        
        String monedaUpper = moneda.toUpperCase();
        if (!monedaUpper.equals("PESOS") && !monedaUpper.equals("DOLARES")) {
            throw new IllegalArgumentException("Moneda no válida. Use: PESOS o DOLARES");
        }
    }
}