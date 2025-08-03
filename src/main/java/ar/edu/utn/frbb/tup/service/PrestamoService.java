package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.*;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoDao prestamoDao;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private ScoreCrediticioService scoreCrediticioService;

    /**
     * Procesa una solicitud de préstamo
     */
    public PrestamoResponseDto solicitarPrestamo(PrestamoRequestDto request) {
        try {
            // 1. Verifica que el cliente existe
            Cliente cliente = clienteService.buscarClientePorDni(request.getNumeroCliente());
            
            // 2. Verifica que el cliente tenga una cuenta en la moneda solicitada
            verificarCuentaEnMoneda(request.getNumeroCliente(), request.getMoneda());
            
            // 3. Consulta score crediticio
            boolean buenoScore = scoreCrediticioService.consultarScore(cliente.getDni());
            if (!buenoScore) {
                return new PrestamoResponseDto(
                    "RECHAZADO", 
                    "Su solicitud fue rechazada debido a su calificación crediticia", 
                    null
                );
            }
            
            // 4. Crea el préstamo
            Prestamo prestamo = crearPrestamo(request);
            
            // 5. Calcula plan de pagos
            List<CuotaPrestamo> planPagos = calcularPlanPagos(prestamo);
            prestamo.setPlanPagos(planPagos);
            prestamo.setEstado(EstadoPrestamo.APROBADO);
            
            // 6. Acredita dinero en la cuenta del cliente
            acreditarPrestamoEnCuenta(request.getNumeroCliente(), request.getMoneda(), request.getMontoPrestamo());
            
            // 7. Guarda préstamo
            prestamoDao.save(prestamo);
            
            return new PrestamoResponseDto(
                "APROBADO",
                "El monto del préstamo fue acreditado en su cuenta",
                planPagos
            );
            
        } catch (IllegalArgumentException e) {
            return new PrestamoResponseDto(
                "RECHAZADO",
                e.getMessage(),
                null
            );
        }
    }

    /**
     * Consulta los préstamos de un cliente
     */
    public ConsultaPrestamosDto consultarPrestamos(long numeroCliente) {
        // Verifica que el cliente existe
        clienteService.buscarClientePorDni(numeroCliente);
        
        // Obtener préstamos del cliente
        List<Prestamo> prestamos = prestamoDao.findByNumeroCliente(numeroCliente);
        
        // Convertir a DTOs
        List<PrestamoInfoDto> prestamosInfo = prestamos.stream()
            .filter(p -> p.getEstado() == EstadoPrestamo.APROBADO || p.getEstado() == EstadoPrestamo.ACTIVO)
            .map(this::convertirAPrestamoInfo)
            .collect(Collectors.toList());
        
        ConsultaPrestamosDto response = new ConsultaPrestamosDto();
        response.setNumeroCliente(numeroCliente);
        response.setPrestamos(prestamosInfo);
        
        return response;
    }

    private void verificarCuentaEnMoneda(long numeroCliente, String moneda) {
        List<Cuenta> cuentas = cuentaService.getCuentasByCliente(numeroCliente);
        
        TipoMoneda tipoMoneda = convertirStringAMoneda(moneda);
        
        boolean tieneCuentaEnMoneda = cuentas.stream()
            .anyMatch(cuenta -> cuenta.getMoneda() == tipoMoneda);
        
        if (!tieneCuentaEnMoneda) {
            throw new IllegalArgumentException("El cliente no posee una cuenta en " + moneda);
        }
    }

    private TipoMoneda convertirStringAMoneda(String moneda) {
        try {
            return TipoMoneda.valueOf(moneda.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + moneda);
        }
    }

    private Prestamo crearPrestamo(PrestamoRequestDto request) {
        Prestamo prestamo = new Prestamo();
        prestamo.setNumeroCliente(request.getNumeroCliente());
        prestamo.setMontoPrestamo(request.getMontoPrestamo());
        prestamo.setPlazoMeses(request.getPlazoMeses());
        prestamo.setMoneda(request.getMoneda());
        prestamo.setEstado(EstadoPrestamo.PENDIENTE);
        return prestamo;
    }

    private List<CuotaPrestamo> calcularPlanPagos(Prestamo prestamo) {
        List<CuotaPrestamo> planPagos = new ArrayList<>();
        double cuotaMensual = prestamo.calcularCuotaMensual();
        
        for (int i = 1; i <= prestamo.getPlazoMeses(); i++) {
            planPagos.add(new CuotaPrestamo(i, cuotaMensual));
        }
        
        return planPagos;
    }

    private void acreditarPrestamoEnCuenta(long numeroCliente, String moneda, double monto) {
        List<Cuenta> cuentas = cuentaService.getCuentasByCliente(numeroCliente);
        TipoMoneda tipoMoneda = convertirStringAMoneda(moneda);
        
        // Busca la primera cuenta en la moneda solicitada
        Cuenta cuenta = cuentas.stream()
            .filter(c -> c.getMoneda() == tipoMoneda)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No se encontró cuenta en " + moneda));
        
        // Acredita el monto (similar a un depósito)
        try {
            cuenta.depositar(monto);
            // Actualiza descripción del último movimiento
            if (!cuenta.getMovimientos().isEmpty()) {
                Movimiento ultimoMov = cuenta.getMovimientos().get(cuenta.getMovimientos().size() - 1);
                ultimoMov.setDescripcion("Acreditación de préstamo");
            }
            // Guarda la cuenta actualizada
            cuentaService.save(cuenta);
        } catch (CantidadNegativaException e) {
            throw new IllegalArgumentException("Error al acreditar préstamo");
        }
    }

    private PrestamoInfoDto convertirAPrestamoInfo(Prestamo prestamo) {
        return new PrestamoInfoDto(
            prestamo.getMontoPrestamo(),
            prestamo.getPlazoMeses(),
            prestamo.getPagosRealizados(),
            prestamo.getSaldoRestante()
        );
    }
}