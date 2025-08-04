package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OperacionServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private OperacionService operacionService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDepositarSuccess() throws CantidadNegativaException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(1000);

        when(cuentaService.find(123456789L)).thenReturn(cuenta);

        Cuenta cuentaActualizada = operacionService.depositar(123456789L, 500);

        assertEquals(1500, cuentaActualizada.getBalance());
        assertEquals(1, cuentaActualizada.getMovimientos().size());
        assertEquals(TipoOperacion.DEPOSITO, cuentaActualizada.getMovimientos().get(0).getTipoOperacion());
        verify(cuentaDao, times(1)).save(cuenta);
    }

    @Test
    public void testDepositarCuentaNoExiste() {
        when(cuentaService.find(999999999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, 
            () -> operacionService.depositar(999999999L, 500));
    }

    @Test
    public void testDepositarMontoNegativo() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(1000);

        when(cuentaService.find(123456789L)).thenReturn(cuenta);

        assertThrows(CantidadNegativaException.class, 
            () -> operacionService.depositar(123456789L, -500));
    }

    @Test
    public void testRetirarSuccess() throws NoAlcanzaException, CantidadNegativaException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(1000);

        when(cuentaService.find(123456789L)).thenReturn(cuenta);

        Cuenta cuentaActualizada = operacionService.retirar(123456789L, 300);

        assertEquals(700, cuentaActualizada.getBalance());
        assertEquals(1, cuentaActualizada.getMovimientos().size());
        assertEquals(TipoOperacion.RETIRO, cuentaActualizada.getMovimientos().get(0).getTipoOperacion());
        verify(cuentaDao, times(1)).save(cuenta);
    }

    @Test
    public void testRetirarSaldoInsuficiente() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(100);

        when(cuentaService.find(123456789L)).thenReturn(cuenta);

        assertThrows(NoAlcanzaException.class, 
            () -> operacionService.retirar(123456789L, 500));
    }

    @Test
    public void testRetirarMontoNegativo() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(1000);

        when(cuentaService.find(123456789L)).thenReturn(cuenta);

        assertThrows(CantidadNegativaException.class, 
            () -> operacionService.retirar(123456789L, -300));
    }

    @Test
    public void testTransferirSuccess() throws NoAlcanzaException, CantidadNegativaException {
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(123456789);
        cuentaOrigen.setBalance(2000);
        cuentaOrigen.setMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(987654321);
        cuentaDestino.setBalance(500);
        cuentaDestino.setMoneda(TipoMoneda.PESOS);

        when(cuentaService.find(123456789L)).thenReturn(cuentaOrigen);
        when(cuentaService.find(987654321L)).thenReturn(cuentaDestino);

        operacionService.transferir(123456789L, 987654321L, 800);

        assertEquals(1200, cuentaOrigen.getBalance());
        assertEquals(1300, cuentaDestino.getBalance());
        
        // Verifica movimientos
        assertEquals(1, cuentaOrigen.getMovimientos().size());
        assertEquals(1, cuentaDestino.getMovimientos().size());
        assertEquals(TipoOperacion.TRANSFERENCIA_ENVIADA, cuentaOrigen.getMovimientos().get(0).getTipoOperacion());
        assertEquals(TipoOperacion.TRANSFERENCIA_RECIBIDA, cuentaDestino.getMovimientos().get(0).getTipoOperacion());
        
        verify(cuentaDao, times(1)).save(cuentaOrigen);
        verify(cuentaDao, times(1)).save(cuentaDestino);
    }

    @Test
    public void testTransferirCuentaOrigenNoExiste() {
        when(cuentaService.find(999999999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, 
            () -> operacionService.transferir(999999999L, 123456789L, 500));
    }

    @Test
    public void testTransferirCuentaDestinoNoExiste() {
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(123456789);
        cuentaOrigen.setBalance(1000);

        when(cuentaService.find(123456789L)).thenReturn(cuentaOrigen);
        when(cuentaService.find(999999999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, 
            () -> operacionService.transferir(123456789L, 999999999L, 500));
    }

    @Test
    public void testTransferirMonedasDiferentes() {
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(123456789);
        cuentaOrigen.setBalance(2000);
        cuentaOrigen.setMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(987654321);
        cuentaDestino.setBalance(500);
        cuentaDestino.setMoneda(TipoMoneda.DOLARES);

        when(cuentaService.find(123456789L)).thenReturn(cuentaOrigen);
        when(cuentaService.find(987654321L)).thenReturn(cuentaDestino);

        assertThrows(IllegalArgumentException.class, 
            () -> operacionService.transferir(123456789L, 987654321L, 500));
    }

    @Test
    public void testTransferirSaldoInsuficiente() {
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(123456789);
        cuentaOrigen.setBalance(100);
        cuentaOrigen.setMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(987654321);
        cuentaDestino.setBalance(500);
        cuentaDestino.setMoneda(TipoMoneda.PESOS);

        when(cuentaService.find(123456789L)).thenReturn(cuentaOrigen);
        when(cuentaService.find(987654321L)).thenReturn(cuentaDestino);

        assertThrows(NoAlcanzaException.class, 
            () -> operacionService.transferir(123456789L, 987654321L, 500));
    }

    @Test
    public void testTransferirMontoNegativo() {
        Cuenta cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumeroCuenta(123456789);
        cuentaOrigen.setBalance(1000);
        cuentaOrigen.setMoneda(TipoMoneda.PESOS);

        Cuenta cuentaDestino = new Cuenta();
        cuentaDestino.setNumeroCuenta(987654321);
        cuentaDestino.setBalance(500);
        cuentaDestino.setMoneda(TipoMoneda.PESOS);

        when(cuentaService.find(123456789L)).thenReturn(cuentaOrigen);
        when(cuentaService.find(987654321L)).thenReturn(cuentaDestino);

        assertThrows(CantidadNegativaException.class, 
            () -> operacionService.transferir(123456789L, 987654321L, -300));
    }
}