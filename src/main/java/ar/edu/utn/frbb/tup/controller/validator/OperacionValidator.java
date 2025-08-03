package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.dto.OperacionDto;
import org.springframework.stereotype.Component;

@Component
public class OperacionValidator {

    public void validateDeposito(OperacionDto operacionDto) {
        validateNumeroCuenta(operacionDto.getNumeroCuenta());
        validateMonto(operacionDto.getMonto());
    }

    public void validateRetiro(OperacionDto operacionDto) {
        validateNumeroCuenta(operacionDto.getNumeroCuenta());
        validateMonto(operacionDto.getMonto());
    }

    public void validateTransferencia(OperacionDto operacionDto) {
        validateNumeroCuenta(operacionDto.getNumeroCuenta());
        validateNumeroCuentaDestino(operacionDto.getNumeroCuentaDestino());
        validateMonto(operacionDto.getMonto());
        validateCuentasDiferentes(operacionDto.getNumeroCuenta(), operacionDto.getNumeroCuentaDestino());
    }

    private void validateNumeroCuenta(Long numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta <= 0) {
            throw new IllegalArgumentException("El número de cuenta debe ser un número positivo");
        }
    }

    private void validateNumeroCuentaDestino(Long numeroCuentaDestino) {
        if (numeroCuentaDestino == null || numeroCuentaDestino <= 0) {
            throw new IllegalArgumentException("El número de cuenta destino debe ser un número positivo");
        }
    }

    private void validateMonto(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }

    private void validateCuentasDiferentes(Long cuentaOrigen, Long cuentaDestino) {
        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma");
        }
    }
}