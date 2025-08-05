package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Servicio para la gestión integral de cuentas bancarias.
 * Maneja la creación, consulta y administración de cuentas,
 * incluyendo validaciones de tipos soportados y registro de movimientos.
 */
@Component
public class CuentaService {

    @Autowired
    CuentaDao cuentaDao;

    @Autowired
    ClienteService clienteService;

    /**
     * Crea una nueva cuenta bancaria para un cliente existente.
     * Valida que el tipo de cuenta esté soportado y que el cliente no tenga ya una cuenta del mismo tipo y moneda.
     * 
     * @param cuentaDto Datos de la cuenta a crear
     * @return Cuenta creada exitosamente
     * @throws CuentaAlreadyExistsException     si la cuenta ya existe
     * @throws TipoCuentaAlreadyExistsException si el cliente ya tiene una cuenta del mismo tipo
     * @throws IllegalArgumentException         si el tipo de cuenta no está soportado
     */
    public Cuenta darDeAltaCuenta(CuentaDto cuentaDto)
            throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        // Crea la cuenta con los datos básicos
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.valueOf(cuentaDto.getTipoCuenta().toUpperCase()));
        cuenta.setMoneda(TipoMoneda.valueOf(cuentaDto.getTipoMoneda().toUpperCase()));
        cuenta.setBalance(cuentaDto.getSaldoInicial());

        // Validaciones de la cuenta
        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        if (!tipoCuentaEstaSoportada(cuenta)) {
            throw new IllegalArgumentException("Tipo de cuenta no soportado");
        }

        // Busca el cliente y valida su existencia
        Cliente titular = clienteService.buscarClientePorDni(cuentaDto.getDniTitular());

        // Valida que el cliente no tenga ya una cuenta igual
        if (clienteYaTieneCuentaDelTipo(titular, cuenta.getTipoCuenta(), cuenta.getMoneda())) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda");
        }

        // Asocia la cuenta al cliente
        cuenta.setTitular(titular);
        titular.addCuenta(cuenta);

        // Persiste los cambios
        cuentaDao.save(cuenta);
        clienteService.guardarCliente(titular);

        // Registra depósito inicial si corresponde
        registrarDepositoInicial(cuenta, cuentaDto.getSaldoInicial());

        return cuenta;
    }

    /**
     * Busca una cuenta específica por su número único.
     * 
     * @param id Número de cuenta a buscar
     * @return Cuenta encontrada o null si no existe
     */
    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }

    /**
     * Obtiene todas las cuentas bancarias de un cliente específico.
     * Verifica previamente que el cliente exista en el sistema.
     * 
     * @param dni DNI del cliente propietario
     * @return Lista de cuentas del cliente
     * @throws IllegalArgumentException si el cliente no existe
     */
    public List<Cuenta> getCuentasByCliente(long dni) {
        clienteService.buscarClientePorDni(dni);
        return cuentaDao.getCuentasByCliente(dni);
    }

    /**
     * Consulta el saldo actual de una cuenta bancaria.
     * Registra la consulta como un movimiento en el historial.
     * 
     * @param numeroCuenta Número de la cuenta a consultar
     * @return Saldo actual de la cuenta
     * @throws IllegalArgumentException si la cuenta no existe
     */
    public double consultarSaldo(long numeroCuenta) {
        Cuenta cuenta = find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }

        // Registra consulta de saldo como movimiento
        Movimiento consulta = new Movimiento(TipoOperacion.CONSULTA_SALDO, 0, numeroCuenta);
        consulta.setDescripcion("Consulta de saldo");
        cuenta.agregarMovimiento(consulta);
        cuentaDao.save(cuenta);

        return cuenta.getBalance();
    }

    /**
     * Obtiene el historial completo de movimientos de una cuenta.
     * Incluye depósitos, retiros, transferencias y consultas.
     * 
     * @param numeroCuenta Número de la cuenta
     * @return Lista de movimientos ordenados cronológicamente
     * @throws IllegalArgumentException si la cuenta no existe
     */
    public List<Movimiento> obtenerMovimientos(long numeroCuenta) {
        Cuenta cuenta = find(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        return cuenta.getMovimientos();
    }

    /**
     * Guarda los cambios de una cuenta en la base de datos.    
     * 
     * @param cuenta Cuenta a guardar
     */
    public void save(Cuenta cuenta) {
        cuentaDao.save(cuenta);
    }

    /**
     * Verifica si un cliente ya posee una cuenta del tipo y moneda especificados.
     * 
     * @param cliente    Cliente a verificar
     * @param tipoCuenta Tipo de cuenta a verificar
     * @param tipoMoneda Tipo de moneda a verificar
     * @return true si el cliente ya tiene una cuenta del tipo especificado
     */
    private boolean clienteYaTieneCuentaDelTipo(Cliente cliente, TipoCuenta tipoCuenta, TipoMoneda tipoMoneda) {
        return cliente.tieneCuenta(tipoCuenta, tipoMoneda);
    }

    /**
     * Registra un movimiento de depósito inicial cuando se crea una cuenta con saldo.
     * 
     * @param cuenta       Cuenta donde registrar el movimiento
     * @param saldoInicial Monto del depósito inicial
     */
    private void registrarDepositoInicial(Cuenta cuenta, double saldoInicial) {
        if (saldoInicial > 0) {
            try {
                Movimiento movimientoInicial = new Movimiento(TipoOperacion.DEPOSITO, saldoInicial,
                        cuenta.getNumeroCuenta());
                movimientoInicial.setDescripcion("Depósito inicial");
                cuenta.agregarMovimiento(movimientoInicial);
                cuentaDao.save(cuenta);
            } catch (Exception e) {

            }
        }
    }

    /**
     * Valida si un tipo de cuenta específico está soportado por el sistema.
     * Los tipos soportados son:
     * - CA$ (Caja de Ahorro en Pesos)
     * - CC$ (Cuenta Corriente en Pesos)
     * - CAU$S (Caja de Ahorro en Dólares)
     * 
     * @param cuenta Cuenta a validar
     * @return true si el tipo está soportado, false en caso contrario
     */
    private boolean tipoCuentaEstaSoportada(Cuenta cuenta) {
        TipoCuenta tipo = cuenta.getTipoCuenta();
        TipoMoneda moneda = cuenta.getMoneda();

        if (tipo == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.PESOS)
            return true; // CA$
        if (tipo == TipoCuenta.CUENTA_CORRIENTE && moneda == TipoMoneda.PESOS)
            return true; // CC$
        if (tipo == TipoCuenta.CAJA_AHORRO && moneda == TipoMoneda.DOLARES)
            return true; // CAU$S

        return false;
    }
}