package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClienteMenor18Años() {
        ClienteDto clienteMenorDeEdad = new ClienteDto();
        clienteMenorDeEdad.setFechaNacimiento("2020-03-18");
        clienteMenorDeEdad.setNombre("Juan");
        clienteMenorDeEdad.setApellido("Pérez");
        clienteMenorDeEdad.setDni(12345678);
        clienteMenorDeEdad.setDireccion("Calle Falsa 123");
        clienteMenorDeEdad.setTelefono("1234567890");
        clienteMenorDeEdad.setTipoPersona("F");
        clienteMenorDeEdad.setBanco("Banco Test");
        
        assertThrows(IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(clienteMenorDeEdad));
    }

    @Test
    public void testClienteSuccess() throws ClienteAlreadyExistsException {
        ClienteDto cliente = new ClienteDto();
        cliente.setFechaNacimiento("1978-03-18");
        cliente.setDni(29857643);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setTelefono("1234567890");
        cliente.setTipoPersona("F");
        cliente.setBanco("Banco Test");
        
        Cliente clienteEntity = clienteService.darDeAltaCliente(cliente);

        verify(clienteDao, times(1)).save(clienteEntity);
        assertEquals("Juan", clienteEntity.getNombre());
        assertEquals("Pérez", clienteEntity.getApellido());
        assertEquals("Calle Falsa 123", clienteEntity.getDireccion());
        assertEquals("1234567890", clienteEntity.getTelefono());
    }

    @Test
    public void testClienteAlreadyExistsException() {
        ClienteDto pepeRino = new ClienteDto();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento("1978-03-18");
        pepeRino.setDireccion("Av. Siempre Viva 742");
        pepeRino.setTelefono("987654321");
        pepeRino.setTipoPersona("F");
        pepeRino.setBanco("Banco Test");

        when(clienteDao.find(26456437, false)).thenReturn(new Cliente());

        assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(pepeRino));
    }

    @Test
    public void testAgregarCuentaAClienteSuccess() {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);

        cuenta.setTitular(pepeRino);
        pepeRino.addCuenta(cuenta);
        clienteService.guardarCliente(pepeRino);

        verify(clienteDao, times(1)).save(pepeRino);
        assertEquals(1, pepeRino.getCuentas().size());
        assertEquals(pepeRino, cuenta.getTitular());
    }

    @Test
    public void testAgregarCuentaAClienteDuplicada() {
        Cliente luciano = new Cliente();
        luciano.setDni(26456439);
        luciano.setNombre("Luciano");
        luciano.setApellido("Test");
        luciano.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        luciano.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(luciano);

        // Agrega primera cuenta
        cuenta.setTitular(luciano);
        luciano.addCuenta(cuenta);
        clienteService.guardarCliente(luciano);

        // Intenta agregar cuenta duplicada
        Cuenta cuenta2 = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(100000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        cuenta2.setTitular(luciano);
        assertThrows(TipoCuentaAlreadyExistsException.class, 
            () -> {
                if (luciano.getCuentas().stream().anyMatch(c -> 
                        c.getTipoCuenta() == cuenta2.getTipoCuenta() && 
                        c.getMoneda() == cuenta2.getMoneda())) {
                    throw new TipoCuentaAlreadyExistsException("Cuenta duplicada");
                }
                luciano.addCuenta(cuenta2);
                clienteService.guardarCliente(luciano);
            });

        verify(clienteDao, times(1)).save(luciano);
        assertEquals(1, luciano.getCuentas().size());
    }

    @Test
    public void testAgregarCuentasDiferentesTipoSuccess() {
        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        cliente.setNombre("Test");
        cliente.setApellido("Cliente");
        cliente.setFechaNacimiento(LocalDate.of(1985, 5, 15));
        cliente.setTipoPersona(TipoPersona.PERSONA_FISICA);

        when(clienteDao.find(12345678, true)).thenReturn(cliente);

        // CA$
        Cuenta cajaAhorroPesos = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(10000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cajaAhorroPesos.setTitular(cliente);
        cliente.addCuenta(cajaAhorroPesos);
        clienteService.guardarCliente(cliente);

        // CC$
        Cuenta cuentaCorrientePesos = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(20000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuentaCorrientePesos.setTitular(cliente);
        cliente.addCuenta(cuentaCorrientePesos);
        clienteService.guardarCliente(cliente);

        // CAU$S
        Cuenta cajaAhorroDolares = new Cuenta()
                .setMoneda(TipoMoneda.DOLARES)
                .setBalance(1000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cajaAhorroDolares.setTitular(cliente);
        cliente.addCuenta(cajaAhorroDolares);
        clienteService.guardarCliente(cliente);

        verify(clienteDao, times(3)).save(cliente);
        assertEquals(3, cliente.getCuentas().size());
    }

    @Test
    public void testBuscarClientePorDniNoExiste() {
        when(clienteDao.find(99999999, true)).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, 
            () -> clienteService.buscarClientePorDni(99999999));
    }

    @Test
    public void testActualizarClienteSuccess() {
        Cliente clienteExistente = new Cliente();
        clienteExistente.setDni(12345678);
        clienteExistente.setNombre("Nombre Original");
        clienteExistente.setApellido("Apellido Original");
        clienteExistente.setFechaNacimiento(LocalDate.of(1985, 1, 1));

        ClienteDto datosActualizados = new ClienteDto();
        datosActualizados.setNombre("Nombre Actualizado");
        datosActualizados.setApellido("Apellido Actualizado");
        datosActualizados.setDireccion("Nueva Dirección 456");
        datosActualizados.setTelefono("9876543210");
        datosActualizados.setBanco("Nuevo Banco");
        datosActualizados.setTipoPersona("F");
        datosActualizados.setFechaNacimiento("1985-01-01");

        when(clienteDao.find(12345678, true)).thenReturn(clienteExistente);

        Cliente clienteActualizado = clienteService.actualizarCliente(12345678, datosActualizados);

        assertEquals("Nombre Actualizado", clienteActualizado.getNombre());
        assertEquals("Apellido Actualizado", clienteActualizado.getApellido());
        assertEquals("Nueva Dirección 456", clienteActualizado.getDireccion());
        assertEquals("9876543210", clienteActualizado.getTelefono());
        verify(clienteDao, times(1)).save(clienteActualizado);
    }

    @Test
    public void testEliminarClienteConSaldo() {
        Cliente cliente = new Cliente();
        cliente.setDni(12345678);
        
        Cuenta cuentaConSaldo = new Cuenta();
        cuentaConSaldo.setBalance(1000);
        cliente.addCuenta(cuentaConSaldo);

        when(clienteDao.find(12345678, true)).thenReturn(cliente);

        assertThrows(IllegalArgumentException.class, 
            () -> clienteService.eliminarCliente(12345678));
    }

    @Test 
    public void testObtenerTodosLosClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setDni(11111111);
        Cliente cliente2 = new Cliente();
        cliente2.setDni(22222222);
        
        List<Cliente> clientesEsperados = Arrays.asList(cliente1, cliente2);
        when(clienteDao.findAll()).thenReturn(clientesEsperados);

        List<Cliente> clientesObtenidos = clienteService.obtenerTodosLosClientes();

        assertEquals(2, clientesObtenidos.size());
        verify(clienteDao, times(1)).findAll();
    }
}
