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

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

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

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<Cuenta> buscarCuenta(@PathVariable long numeroCuenta) {
        Cuenta cuenta = cuentaService.find(numeroCuenta);
        if (cuenta != null) {
            return ResponseEntity.ok(cuenta);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cliente/{dni}")
    public ResponseEntity<List<Cuenta>> obtenerCuentasPorCliente(@PathVariable long dni) {
        try {
            List<Cuenta> cuentas = cuentaService.getCuentasByCliente(dni);
            return ResponseEntity.ok(cuentas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{numeroCuenta}/saldo")
    public ResponseEntity<Double> consultarSaldo(@PathVariable long numeroCuenta) {
        try {
            double saldo = cuentaService.consultarSaldo(numeroCuenta);
            return ResponseEntity.ok(saldo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

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