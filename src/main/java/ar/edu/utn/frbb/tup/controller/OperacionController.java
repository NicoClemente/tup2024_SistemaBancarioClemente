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

    @PostMapping("/transferencia")
    public ResponseEntity<String> transferir(@RequestBody OperacionDto operacionDto) {
        try {
            operacionValidator.validateTransferencia(operacionDto);
            operacionService.transferir(
                operacionDto.getNumeroCuenta(), 
                operacionDto.getNumeroCuentaDestino(), 
                operacionDto.getMonto()
            );
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