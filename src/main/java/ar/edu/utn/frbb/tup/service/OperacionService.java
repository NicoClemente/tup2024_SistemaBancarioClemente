package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para operaciones bancarias transaccionales.
 * Maneja todas las operaciones que modifican el saldo de las cuentas:
 * depósitos, retiros y transferencias entre cuentas.
 */
@Service
public class OperacionService {

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private CuentaService cuentaService;

    /**
     * Realiza un depósito de dinero en una cuenta bancaria.
     * Incrementa el saldo de la cuenta y registra el movimiento.
     * 
     * @param numeroCuenta Número de la cuenta donde depositar
     * @param monto        Cantidad de dinero a depositar
     * @return Cuenta con el saldo actualizado
     * @throws CantidadNegativaException si el monto es negativo o cero
     * @throws IllegalArgumentException  si la cuenta no existe
     */
    public Cuenta depositar(Long numeroCuenta, double monto) throws CantidadNegativaException {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }

        // Realiza depósito y registrar movimiento
        cuenta.depositar(monto);
        cuentaDao.save(cuenta);
        return cuenta;
    }

    /**
     * Realiza un retiro de dinero de una cuenta bancaria.
     * Disminuye el saldo de la cuenta y registra el movimiento.
     * 
     * @param numeroCuenta Número de la cuenta desde donde retirar
     * @param monto        Cantidad de dinero a retirar
     * @return Cuenta con el saldo actualizado
     * @throws NoAlcanzaException        si el saldo es insuficiente
     * @throws CantidadNegativaException si el monto es negativo o cero
     * @throws IllegalArgumentException  si la cuenta no existe
     */
    public Cuenta retirar(Long numeroCuenta, double monto) throws NoAlcanzaException, CantidadNegativaException {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        // Realiza retiro y registrar movimiento
        cuenta.debitarDeCuenta(monto);
        cuentaDao.save(cuenta);
        return cuenta;
    }

    /**
     * Realiza una transferencia de dinero entre dos cuentas bancarias.
     * Debita de la cuenta origen y acredita en la cuenta destino.
     * Solo permite transferencias entre cuentas de la misma moneda.
     * 
     * @param numeroCuentaOrigen  Número de la cuenta que envía el dinero
     * @param numeroCuentaDestino Número de la cuenta que recibe el dinero
     * @param monto               Cantidad de dinero a transferir
     * @throws NoAlcanzaException        si la cuenta origen no tiene saldo suficiente
     * @throws CantidadNegativaException si el monto es negativo o cero
     * @throws IllegalArgumentException  si alguna cuenta no existe o son de
     *                                   diferentes monedas
     */
    public void transferir(Long numeroCuentaOrigen, Long numeroCuentaDestino, double monto)
            throws NoAlcanzaException, CantidadNegativaException {

        // Verifica que ambas cuentas existan
        Cuenta cuentaOrigen = cuentaService.find(numeroCuentaOrigen);
        if (cuentaOrigen == null) {
            throw new IllegalArgumentException("La cuenta origen no existe");
        }

        Cuenta cuentaDestino = cuentaService.find(numeroCuentaDestino);
        if (cuentaDestino == null) {
            throw new IllegalArgumentException("La cuenta destino no existe");
        }

        // Valida que las cuentas sean de la misma moneda
        if (!cuentaOrigen.getMoneda().equals(cuentaDestino.getMoneda())) {
            throw new IllegalArgumentException("No se pueden transferir entre cuentas de diferentes monedas");
        }

        // Realiza la transferencia
        cuentaOrigen.transferirA(cuentaDestino, monto);

        // Guarda ambas cuentas actualizadas
        cuentaDao.save(cuentaOrigen);
        cuentaDao.save(cuentaDestino);
    }
}