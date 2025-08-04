package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CuentaService {
    
    @Autowired
    CuentaDao cuentaDao;

    @Autowired
    ClienteService clienteService;

     public Cuenta darDeAltaCuenta(CuentaDto cuentaDto) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        // Crea la cuenta con los datos básicos
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.valueOf(cuentaDto.getTipoCuenta().toUpperCase()));
        cuenta.setMoneda(TipoMoneda.valueOf(cuentaDto.getTipoMoneda().toUpperCase()));
        cuenta.setBalance(cuentaDto.getSaldoInicial());

        // Validaciones de la cuenta
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        if (!tipoCuentaEstaSoportada(cuenta)) {
            throw new IllegalArgumentException("Tipo de cuenta no soportado");
        }

        // Busca el cliente
        Cliente titular = clienteService.buscarClientePorDni(cuentaDto.getDniTitular());
        
        // Valida que el cliente no tenga ya una cuenta igual
        if (clienteYaTieneCuentaDelTipo(titular, cuenta.getTipoCuenta(), cuenta.getMoneda())) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda");
        }

        // Asocia la cuenta al cliente
        cuenta.setTitular(titular);
        titular.addCuenta(cuenta);

        // Guarda todo
        cuentaDao.save(cuenta);
        clienteService.guardarCliente(titular);
        
        // Registra depósito inicial si corresponde
        registrarDepositoInicial(cuenta, cuentaDto.getSaldoInicial());
        
        return cuenta;
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }

    public List<Cuenta> getCuentasByCliente(long dni) {        
        clienteService.buscarClientePorDni(dni);
        return cuentaDao.getCuentasByCliente(dni);
    }

    public double consultarSaldo(long numeroCuenta) {
        Cuenta cuenta = find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        
        // Registra consulta de saldo
        Movimiento consulta = new Movimiento(TipoOperacion.CONSULTA_SALDO, 0, numeroCuenta);
        consulta.setDescripcion("Consulta de saldo");
        cuenta.agregarMovimiento(consulta);
        cuentaDao.save(cuenta);
        
        return cuenta.getBalance();
    }

    public List<Movimiento> obtenerMovimientos(long numeroCuenta) {
        Cuenta cuenta = find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        return cuenta.getMovimientos();
    }

    private boolean clienteYaTieneCuentaDelTipo(Cliente cliente, TipoCuenta tipoCuenta, TipoMoneda tipoMoneda) {
        return cliente.tieneCuenta(tipoCuenta, tipoMoneda);
    }
    
    private void registrarDepositoInicial(Cuenta cuenta, double saldoInicial) {
        if (saldoInicial > 0) {
            try {
                Movimiento movimientoInicial = new Movimiento(TipoOperacion.DEPOSITO, saldoInicial, cuenta.getNumeroCuenta());
                movimientoInicial.setDescripcion("Depósito inicial");
                cuenta.agregarMovimiento(movimientoInicial);
                cuentaDao.save(cuenta);
            } catch (Exception e) {                
            }
        }
    }

    private boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
        // Cuentas soportadas: CA$ (Caja Ahorro Pesos), CC$ (Cuenta Corriente Pesos), CAU$S (Caja Ahorro Dólares)
        TipoCuenta tipo = cuenta.getTipoCuenta();
        TipoMoneda moneda = cuenta.getMoneda();
        
        if (tipo == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.PESOS) return true; // CA$
        if (tipo == TipoCuenta.CUENTA_CORRIENTE && moneda == TipoMoneda.PESOS) return true; // CC$
        if (tipo == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.DOLARES) return true; // CAU$S
        
        return false;
    }
    
    public void save(Cuenta cuenta) {
        cuentaDao.save(cuenta);
    }
}