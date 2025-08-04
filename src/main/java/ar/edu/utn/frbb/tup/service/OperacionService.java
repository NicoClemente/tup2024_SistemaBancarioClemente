package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperacionService {

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private CuentaService cuentaService;

    public Cuenta depositar(Long numeroCuenta, double monto) throws CantidadNegativaException {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }

        cuenta.depositar(monto);
        cuentaDao.save(cuenta);
        return cuenta;
    }

    public Cuenta retirar(Long numeroCuenta, double monto) throws NoAlcanzaException, CantidadNegativaException {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }

        cuenta.debitarDeCuenta(monto);
        cuentaDao.save(cuenta);
        return cuenta;
    }

    public void transferir(Long numeroCuentaOrigen, Long numeroCuentaDestino, double monto) 
            throws NoAlcanzaException, CantidadNegativaException {
        
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
        
        // Guarda ambas cuentas
        cuentaDao.save(cuentaOrigen);
        cuentaDao.save(cuentaDestino);
    }
}