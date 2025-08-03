package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.dto.*;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;
    
    @PostMapping
    public ResponseEntity<PrestamoResponseDto> solicitarPrestamo(@RequestBody PrestamoRequestDto request) {
        try {            
            prestamoValidator.validate(request);
            
            PrestamoResponseDto response = prestamoService.solicitarPrestamo(request);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            PrestamoResponseDto errorResponse = new PrestamoResponseDto(
                "RECHAZADO",
                e.getMessage(),
                null
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/{clienteId}")
    public ResponseEntity<ConsultaPrestamosDto> consultarPrestamos(@PathVariable long clienteId) {
        try {
            ConsultaPrestamosDto response = prestamoService.consultarPrestamos(clienteId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}