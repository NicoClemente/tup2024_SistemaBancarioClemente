package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimiento;
import ar.edu.utn.frbb.tup.model.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de cuentas bancarias.
 * Maneja operaciones de creación, consulta y gestión de cuentas.
 */
@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    /**
     * Endpoint para crear una nueva cuenta bancaria.
     * Valida que el cliente exista y que no tenga ya una cuenta del mismo tipo y
     * moneda.
     * 
     * @param cuentaDto Datos de la cuenta a crear
     * @return ResponseEntity con la cuenta creada o error si no se puede crear
     * @throws CuentaAlreadyExistsException     si la cuenta ya existe
     * @throws TipoCuentaAlreadyExistsException si el cliente ya tiene una cuenta
     *                                          del mismo tipo
     * 
     * POST /cuenta
     */
    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody CuentaDto cuentaDto) {
        try {
            cuentaValidator.validate(cuentaDto);

            Cuenta cuenta = cuentaService.darDeAltaCuenta(cuentaDto);
            return ResponseEntity.ok(cuenta);
        } catch (CuentaAlreadyExistsException | TipoCuentaAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para buscar una cuenta específica por su número.
     * 
     * @param numeroCuenta Número único de la cuenta a buscar
     * @return ResponseEntity con la cuenta encontrada o 404 si no existe
     * 
     * GET /cuenta/{numeroCuenta}
     */
    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<Cuenta> buscarCuenta(@PathVariable long numeroCuenta) {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta != null) {
            return ResponseEntity.ok(cuenta);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para obtener todas las cuentas de un cliente específico.
     * 
     * @param dni DNI del cliente propietario de las cuentas
     * @return ResponseEntity con la lista de cuentas del cliente o 404 si el
     *         cliente no existe
     * 
     * GET /cuenta/cliente/{dni}
     */
    @GetMapping("/cliente/{dni}")
    public ResponseEntity<List<Cuenta>> obtenerCuentasPorCliente(@PathVariable long dni) {
        try {
            List<Cuenta> cuentas = cuentaService.getCuentasByCliente(dni);
            return ResponseEntity.ok(cuentas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para consultar el saldo actual de una cuenta.
     * Registra la consulta como un movimiento en el historial de la cuenta.
     * 
     * @param numeroCuenta Número de la cuenta a consultar
     * @return ResponseEntity con el saldo actual o 404 si la cuenta no existe
     * 
     * GET /cuenta/{numeroCuenta}/saldo
     */
    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<Double> consultarSaldo(@PathVariable long numeroCuenta) {
        try {
            double saldo = cuentaService.consultarSaldo(numeroCuenta);
            return ResponseEntity.ok(saldo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener el historial de movimientos de una cuenta.
     * Incluye todos los depósitos, retiros, transferencias y consultas realizadas.
     * 
     * @param numeroCuenta Número de la cuenta para obtener movimientos
     * @return ResponseEntity con la lista de movimientos o 404 si la cuenta no
     *         existe
     * 
     * GET /cuenta/{numeroCuenta}/movimientos
     */
    @GetMapping("/{numeroCuenta}/movimientos")
    public ResponseEntity<List<Movimiento>> obtenerMovimientos(@PathVariable long numeroCuenta) {
        try {
            List<Movimiento> movimientos = cuentaService.obtenerMovimientos(numeroCuenta);
            return ResponseEntity.ok(movimientos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}