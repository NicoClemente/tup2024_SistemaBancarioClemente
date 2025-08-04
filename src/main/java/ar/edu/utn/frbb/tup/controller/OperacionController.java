package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.OperacionValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.dto.OperacionDto;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.service.OperacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones bancarias transaccionales.
 * Maneja depósitos, retiros y transferencias entre cuentas.
 */
@RestController
@RequestMapping("/operacion")
public class OperacionController {

    @Autowired
    private OperacionService operacionService;

    @Autowired
    private OperacionValidator operacionValidator;

    @PostMapping("/deposito")
    public ResponseEntity<Cuenta> depositar(@RequestBody OperacionDto operacionDto) {
        try {
            operacionValidator.validateDeposito(operacionDto);
            Cuenta cuenta = operacionService.depositar(operacionDto.getNumeroCuenta(), operacionDto.getMonto());
            return ResponseEntity.ok(cuenta);
        } catch (CantidadNegativaException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para realizar un depósito en una cuenta bancaria.
     * Valida que el monto sea positivo y que la cuenta exista.
     * 
     * @param operacionDto Datos de la operación de depósito (número de cuenta y monto)
     * @return ResponseEntity con la cuenta actualizada o error si la operación falla
     * @throws CantidadNegativaException si el monto es negativo o cero
     * 
     * POST /operacion/deposito
     */
    @PostMapping("/retiro")
    public ResponseEntity<Cuenta> retirar(@RequestBody OperacionDto operacionDto) {
        try {
            operacionValidator.validateRetiro(operacionDto);
            Cuenta cuenta = operacionService.retirar(operacionDto.getNumeroCuenta(), operacionDto.getMonto());
            return ResponseEntity.ok(cuenta);
        } catch (NoAlcanzaException e) {
            return ResponseEntity.badRequest().build();
        } catch (CantidadNegativaException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para realizar una transferencia entre dos cuentas bancarias.
     * Valida que ambas cuentas existan, sean de la misma moneda y que la cuenta
     * origen tenga saldo suficiente.
     * 
     * @param operacionDto Datos completos de la transferencia (cuenta origen, destino y monto)
     * @return ResponseEntity con mensaje de éxito o error descriptivo
     * @throws NoAlcanzaException        si el saldo de la cuenta origen es insuficiente
     * @throws CantidadNegativaException si el monto es negativo o cero
     * 
     * POST /operacion/transferencia
     */
    @PostMapping("/transferencia")
    public ResponseEntity<String> transferir(@RequestBody OperacionDto operacionDto) {
        try {
            operacionValidator.validateTransferencia(operacionDto);
            operacionService.transferir(
                    operacionDto.getNumeroCuenta(),
                    operacionDto.getNumeroCuentaDestino(),
                    operacionDto.getMonto());
            return ResponseEntity.ok("Transferencia realizada exitosamente");
        } catch (NoAlcanzaException e) {
            return ResponseEntity.status(400).body("Saldo insuficiente");
        } catch (CantidadNegativaException e) {
            return ResponseEntity.status(400).body("El monto debe ser positivo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Cuenta no encontrada");
        }
    }
}