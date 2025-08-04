package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.dto.*;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de préstamos bancarios.
 * Proporciona endpoints para solicitud y consulta de préstamos.
 */
@RestController
@RequestMapping("/api/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;
    
    /**
     * Endpoint para solicitar un nuevo préstamo bancario.
     * Evalúa automáticamente la elegibilidad crediticia del cliente,
     * calcula el plan de pagos y acredita el monto en la cuenta del cliente.
     * 
     * @param request Datos de la solicitud de préstamo
     * @return PrestamoResponseDto con el estado del préstamo y plan de pagos si es aprobado
     * 
     * POST /api/prestamo
     */
    @PostMapping
    public ResponseEntity<PrestamoResponseDto> solicitarPrestamo(@RequestBody PrestamoRequestDto request) {
        try {            
            // Valida datos de entrada
            prestamoValidator.validate(request);
            
            // Procesa solicitud de préstamo
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
    
    /**
     * Endpoint para consultar los préstamos activos de un cliente.
     * Retorna únicamente préstamos en estado APROBADO o ACTIVO.
     * 
     * @param clienteId DNI del cliente a consultar
     * @return ResponseEntity con la información de préstamos del cliente o 404 si no existe
     * 
     * GET /api/prestamo/{clienteId}
     */
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