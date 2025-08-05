package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.*;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrestamoServiceTest {

    @Mock
    private PrestamoDao prestamoDao;

    @Mock
    private ClienteService clienteService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private ScoreCrediticioService scoreCrediticioService;

    @InjectMocks
    private PrestamoService prestamoService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSolicitarPrestamoExitoso() {
        PrestamoRequestDto request = new PrestamoRequestDto();
        request.setNumeroCliente(12345678);
        request.setMontoPrestamo(100000);
        request.setPlazoMeses(12);
        request.setMoneda("PESOS");

        Cliente cliente = new Cliente();
        cliente.setDni(12345678);

        Cuenta cuenta = new Cuenta();
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setBalance(5000);

        ScoreCrediticioService.ScoreResultado scoreResultado = new ScoreCrediticioService.ScoreResultado(8, true,
                "Buena calificación crediticia");

        when(clienteService.buscarClientePorDni(12345678)).thenReturn(cliente);
        when(cuentaService.getCuentasByCliente(12345678)).thenReturn(Arrays.asList(cuenta));
        when(scoreCrediticioService.evaluarScore(12345678)).thenReturn(scoreResultado);

        PrestamoResponseDto response = prestamoService.solicitarPrestamo(request);

        assertEquals("APROBADO", response.getEstado());
        assertEquals("El monto del préstamo fue acreditado en su cuenta", response.getMensaje());
        assertNotNull(response.getPlanPagos());
        assertEquals(12, response.getPlanPagos().size());

        verify(prestamoDao, times(1)).save(any(Prestamo.class));
    }

    @Test
    public void testSolicitarPrestamoClienteNoExiste() {
        PrestamoRequestDto request = new PrestamoRequestDto();
        request.setNumeroCliente(99999999);
        request.setMontoPrestamo(100000);
        request.setPlazoMeses(12);
        request.setMoneda("PESOS");

        when(clienteService.buscarClientePorDni(99999999))
                .thenThrow(new IllegalArgumentException("El cliente no existe"));

        PrestamoResponseDto response = prestamoService.solicitarPrestamo(request);

        assertEquals("RECHAZADO", response.getEstado());
        assertEquals("El cliente no existe", response.getMensaje());
        assertNull(response.getPlanPagos());
    }

    @Test
    public void testSolicitarPrestamoMalScore() {
        PrestamoRequestDto request = new PrestamoRequestDto();
        request.setNumeroCliente(12345679);
        request.setMontoPrestamo(100000);
        request.setPlazoMeses(12);
        request.setMoneda("PESOS");

        Cliente cliente = new Cliente();
        cliente.setDni(12345679);

        Cuenta cuenta = new Cuenta();
        cuenta.setMoneda(TipoMoneda.PESOS);

        ScoreCrediticioService.ScoreResultado scoreResultado = new ScoreCrediticioService.ScoreResultado(3, false,
                "Calificación crediticia deficiente");

        when(clienteService.buscarClientePorDni(12345679)).thenReturn(cliente);
        when(cuentaService.getCuentasByCliente(12345679)).thenReturn(Arrays.asList(cuenta));
        when(scoreCrediticioService.evaluarScore(12345679)).thenReturn(scoreResultado);

        PrestamoResponseDto response = prestamoService.solicitarPrestamo(request);

        assertEquals("RECHAZADO", response.getEstado());
        assertEquals("Su solicitud fue rechazada debido a su calificación crediticia", response.getMensaje());
        assertNull(response.getPlanPagos());
    }

    @Test
    public void testSolicitarPrestamoSinCuentaEnMoneda() {
        PrestamoRequestDto request = new PrestamoRequestDto();
        request.setNumeroCliente(12345678);
        request.setMontoPrestamo(100000);
        request.setPlazoMeses(12);
        request.setMoneda("DOLARES");

        Cliente cliente = new Cliente();
        cliente.setDni(12345678);

        Cuenta cuentaPesos = new Cuenta();
        cuentaPesos.setMoneda(TipoMoneda.PESOS);

        when(clienteService.buscarClientePorDni(12345678)).thenReturn(cliente);
        when(cuentaService.getCuentasByCliente(12345678)).thenReturn(Arrays.asList(cuentaPesos));

        PrestamoResponseDto response = prestamoService.solicitarPrestamo(request);

        assertEquals("RECHAZADO", response.getEstado());
        assertTrue(response.getMensaje().contains("no posee una cuenta en DOLARES"));
    }

    @Test
    public void testConsultarPrestamosExitoso() {
        long numeroCliente = 12345678;

        Cliente cliente = new Cliente();
        cliente.setDni(numeroCliente);

        Prestamo prestamo1 = new Prestamo();
        prestamo1.setMontoPrestamo(100000);
        prestamo1.setPlazoMeses(12);
        prestamo1.setPagosRealizados(3);
        prestamo1.setSaldoRestante(75000);
        prestamo1.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo prestamo2 = new Prestamo();
        prestamo2.setMontoPrestamo(50000);
        prestamo2.setPlazoMeses(6);
        prestamo2.setPagosRealizados(6);
        prestamo2.setSaldoRestante(0);
        prestamo2.setEstado(EstadoPrestamo.PAGADO);

        when(clienteService.buscarClientePorDni(numeroCliente)).thenReturn(cliente);
        when(prestamoDao.findByNumeroCliente(numeroCliente)).thenReturn(Arrays.asList(prestamo1, prestamo2));

        ConsultaPrestamosDto response = prestamoService.consultarPrestamos(numeroCliente);

        assertEquals(numeroCliente, response.getNumeroCliente());
        assertEquals(1, response.getPrestamos().size()); // Solo préstamo activo

        PrestamoInfoDto info = response.getPrestamos().get(0);
        assertEquals(100000, info.getMonto());
        assertEquals(12, info.getPlazoMeses());
        assertEquals(3, info.getPagosRealizados());
        assertEquals(75000, info.getSaldoRestante());
    }

    @Test
    public void testConsultarPrestamosClienteNoExiste() {
        long numeroCliente = 99999999;

        when(clienteService.buscarClientePorDni(numeroCliente))
                .thenThrow(new IllegalArgumentException("El cliente no existe"));

        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.consultarPrestamos(numeroCliente));
    }
}