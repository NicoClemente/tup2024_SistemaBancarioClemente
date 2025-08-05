package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.*;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión integral de préstamos bancarios.
 * Maneja solicitudes de préstamos, evaluación crediticia, cálculo de cuotas
 * y consulta del estado de préstamos existentes.
 */
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
     * Procesa una solicitud de préstamo bancario.
     * Evalúa la elegibilidad del cliente, calcula el plan de pagos
     * y acredita el monto en la cuenta del cliente si es aprobado.
     * 
     * @param request Datos de la solicitud de préstamo
     * @return Respuesta con el estado del préstamo y plan de pagos si es aprobado
     */
    public PrestamoResponseDto solicitarPrestamo(PrestamoRequestDto request) {
        try {
            // Verifica que el cliente existe
            Cliente cliente = clienteService.buscarClientePorDni(request.getNumeroCliente());

            // Verifica que el cliente tenga una cuenta en la moneda solicitada
            verificarCuentaEnMoneda(request.getNumeroCliente(), request.getMoneda());

            // Consulta score crediticio
            boolean buenoScore = scoreCrediticioService.evaluarScore(cliente.getDni()).isElegible();
            if (!buenoScore) {
                return new PrestamoResponseDto(
                        "RECHAZADO",
                        "Su solicitud fue rechazada debido a su calificación crediticia",
                        null);
            }

            // Crea el préstamo
            Prestamo prestamo = crearPrestamo(request);

            // Calcula plan de pagos
            List<CuotaPrestamo> planPagos = calcularPlanPagos(prestamo);
            prestamo.setPlanPagos(planPagos);
            prestamo.setEstado(EstadoPrestamo.APROBADO);

            // Acredita dinero en la cuenta del cliente
            acreditarPrestamoEnCuenta(request.getNumeroCliente(), request.getMoneda(), request.getMontoPrestamo());

            // Guarda préstamo
            prestamoDao.save(prestamo);

            return new PrestamoResponseDto(
                    "APROBADO",
                    "El monto del préstamo fue acreditado en su cuenta",
                    planPagos);

        } catch (IllegalArgumentException e) {
            return new PrestamoResponseDto(
                    "RECHAZADO",
                    e.getMessage(),
                    null);
        }
    }

    /**
     * Consulta todos los préstamos activos de un cliente específico.
     * Filtra solo préstamos aprobados o activos, excluyendo los pagados.
     * 
     * @param numeroCliente DNI del cliente a consultar
     * @return Información detallada de los préstamos del cliente
     * @throws IllegalArgumentException si el cliente no existe
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

    /**
     * Verifica que el cliente tenga al menos una cuenta en la moneda solicitada.
     * 
     * @param numeroCliente DNI del cliente
     * @param moneda        Moneda del préstamo solicitado
     * @throws IllegalArgumentException si el cliente no tiene cuenta en esa moneda
     */
    private void verificarCuentaEnMoneda(long numeroCliente, String moneda) {
        List<Cuenta> cuentas = cuentaService.getCuentasByCliente(numeroCliente);

        TipoMoneda tipoMoneda = convertirStringAMoneda(moneda);

        boolean tieneCuentaEnMoneda = cuentas.stream()
                .anyMatch(cuenta -> cuenta.getMoneda() == tipoMoneda);

        if (!tieneCuentaEnMoneda) {
            throw new IllegalArgumentException("El cliente no posee una cuenta en " + moneda);
        }
    }

    /**
     * Convierte una cadena de texto a enum TipoMoneda.
     * 
     * @param moneda Cadena con el nombre de la moneda
     * @return TipoMoneda correspondiente
     * @throws IllegalArgumentException si la moneda no es válida
     */
    private TipoMoneda convertirStringAMoneda(String moneda) {
        try {
            return TipoMoneda.valueOf(moneda.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + moneda);
        }
    }

    /**
     * Crea una nueva instancia de préstamo con los datos de la solicitud.
     * 
     * @param request Datos de la solicitud
     * @return Préstamo inicializado con estado PENDIENTE
     */
    private Prestamo crearPrestamo(PrestamoRequestDto request) {
        Prestamo prestamo = new Prestamo();
        prestamo.setNumeroCliente(request.getNumeroCliente());
        prestamo.setMontoPrestamo(request.getMontoPrestamo());
        prestamo.setPlazoMeses(request.getPlazoMeses());
        prestamo.setMoneda(request.getMoneda());
        prestamo.setEstado(EstadoPrestamo.PENDIENTE);
        return prestamo;
    }

    /**
     * Calcula el plan de pagos del préstamo usando el sistema de cuotas fijas.
     * Utiliza la fórmula de amortización francesa para distribuir capital e intereses.
     * 
     * @param prestamo Préstamo para calcular las cuotas
     * @return Lista de cuotas mensuales con monto fijo
     */
    private List<CuotaPrestamo> calcularPlanPagos(Prestamo prestamo) {
        List<CuotaPrestamo> planPagos = new ArrayList<>();
        double cuotaMensual = prestamo.calcularCuotaMensual();

        // Genera cuotas numeradas del 1 al plazo en meses
        for (int i = 1; i <= prestamo.getPlazoMeses(); i++) {
            planPagos.add(new CuotaPrestamo(i, cuotaMensual));
        }

        return planPagos;
    }

    /**
     * Acredita el monto del préstamo en una cuenta del cliente.
     * Busca la primera cuenta disponible en la moneda solicitada.
     * 
     * @param numeroCliente DNI del cliente
     * @param moneda        Moneda del préstamo
     * @param monto         Cantidad a acreditar
     * @throws IllegalArgumentException si no se encuentra cuenta en la moneda o hay error en la acreditación
     */
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

    /**
     * Convierte un préstamo a DTO con información resumida para consultas.
     * 
     * @param prestamo Préstamo a convertir
     * @return DTO con información esencial del préstamo
     */
    private PrestamoInfoDto convertirAPrestamoInfo(Prestamo prestamo) {
        return new PrestamoInfoDto(
                prestamo.getMontoPrestamo(),
                prestamo.getPlazoMeses(),
                prestamo.getPagosRealizados(),
                prestamo.getSaldoRestante(),
                prestamo.getMoneda());
    }
}