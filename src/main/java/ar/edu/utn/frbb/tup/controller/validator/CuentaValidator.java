package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.dto.CuentaDto;
import org.springframework.stereotype.Component;

@Component
public class CuentaValidator {

    public void validate(CuentaDto cuentaDto) {
        validateTipoCuenta(cuentaDto.getTipoCuenta());
        validateTipoMoneda(cuentaDto.getTipoMoneda());
        validateDniTitular(cuentaDto.getDniTitular());
        validateSaldoInicial(cuentaDto.getSaldoInicial());
    }

    private void validateTipoCuenta(String tipoCuenta) {
        try {
            TipoCuenta.valueOf(tipoCuenta.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de cuenta no es válido. Use: CUENTA_CORRIENTE o CAJA_AHORRO");
        }
    }

    private void validateTipoMoneda(String tipoMoneda) {
        try {
            TipoMoneda.valueOf(tipoMoneda.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de moneda no es válido. Use: PESOS o DOLARES");
        }
    }

    private void validateDniTitular(long dniTitular) {
        if (dniTitular <= 0) {
            throw new IllegalArgumentException("El DNI del titular debe ser un número positivo");
        }
    }

    private void validateSaldoInicial(double saldoInicial) {
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
    }
}