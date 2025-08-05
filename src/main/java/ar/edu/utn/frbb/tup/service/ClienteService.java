package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Servicio para la gestión integral de clientes bancarios.
 * Maneja todas las operaciones relacionadas con la administración de clientes,
 * incluyendo creación, listado, actualización y eliminación de clientes.
 */
@Service
public class ClienteService {

    ClienteDao clienteDao;

    /**
     * Constructor del servicio de clientes.
     * 
     * @param clienteDao DAO para operaciones de persistencia de clientes
     */
    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    /**
     * Registra un nuevo cliente en el sistema bancario.
     * Verifica que el cliente no exista previamente y que sea mayor de edad.
     * 
     * @param clienteDto Datos del cliente a registrar
     * @return Cliente registrado exitosamente
     * @throws ClienteAlreadyExistsException si ya existe un cliente con el mismo DNI
     * @throws IllegalArgumentException      si el cliente es menor de 18 años
     */
    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        // Verifica que el cliente no exista
        if (clienteDao.find(clienteDto.getDni(), false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con DNI " + clienteDto.getDni());
        }

        // Valida edad mínima ANTES de crear el cliente
        validateEdadMinima(clienteDto.getFechaNacimiento());

        // Crea el cliente después de las validaciones
        Cliente cliente = new Cliente(clienteDto);

        clienteDao.save(cliente);
        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente.
     * Mantiene las validaciones de negocio como la edad mínima.
     * 
     * @param dni        DNI del cliente a actualizar
     * @param clienteDto Nuevos datos del cliente
     * @return Cliente con los datos actualizados
     * @throws IllegalArgumentException si el cliente no existe o los datos no son válidos
     */
    public Cliente actualizarCliente(long dni, ClienteDto clienteDto) {
        Cliente clienteExistente = buscarClientePorDni(dni);

        // Actualiza campos del cliente
        clienteExistente.setNombre(clienteDto.getNombre());
        clienteExistente.setApellido(clienteDto.getApellido());
        clienteExistente.setDireccion(clienteDto.getDireccion());
        clienteExistente.setTelefono(clienteDto.getTelefono());
        clienteExistente.setBanco(clienteDto.getBanco());
        clienteExistente.setTipoPersona(TipoPersona.fromString(clienteDto.getTipoPersona()));

        // Actualiza fecha de nacimiento si se proporciona
        if (clienteDto.getFechaNacimiento() != null) {
            // Valida edad antes de actualizar
            validateEdadMinima(clienteDto.getFechaNacimiento());
            clienteExistente.setFechaNacimiento(LocalDate.parse(clienteDto.getFechaNacimiento()));
        }

        clienteDao.save(clienteExistente);
        return clienteExistente;
    }

    /**
     * Elimina un cliente del sistema bancario.
     * Solo permite la eliminación si el cliente no tiene cuentas con saldo
     * positivo.
     * 
     * @param dni DNI del cliente a eliminar
     * @throws IllegalArgumentException si el cliente no existe o tiene cuentas con saldo
     */
    public void eliminarCliente(long dni) {
        Cliente cliente = buscarClientePorDni(dni);

        // Verifica que no tenga cuentas con saldo
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getBalance() > 0) {
                throw new IllegalArgumentException(
                        "No se puede eliminar un cliente con cuentas que tienen saldo positivo");
            }
        }

        clienteDao.delete(dni);
    }

    /**
     * Obtiene la lista completa de clientes registrados en el sistema.
     * Incluye todas las cuentas asociadas a cada cliente.
     * 
     * @return Lista de todos los clientes con sus cuentas
     */
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDao.findAll();
    }

    /**
     * Busca un cliente específico por su DNI.
     * Carga todas las cuentas asociadas al cliente.
     * 
     * @param dni DNI del cliente a buscar
     * @return Cliente encontrado con todas sus cuentas
     * @throws IllegalArgumentException si el cliente no existe
     */
    public Cliente buscarClientePorDni(long dni) {
        Cliente cliente = clienteDao.find(dni, true);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return cliente;
    }

    /**
     * Guarda un cliente en la base de datos.
     * Método auxiliar para que otros servicios puedan guardar cambios.
     */
    public void guardarCliente(Cliente cliente) {
        clienteDao.save(cliente);
    }

    /**
     * Valida que el cliente sea mayor de 18 años.
     * 
     * @param fechaNacimiento Fecha de nacimiento en formato String
     * @throws IllegalArgumentException si el cliente es menor de 18 años
     */
    private void validateEdadMinima(String fechaNacimiento) {
        LocalDate fecha = LocalDate.parse(fechaNacimiento);
        int edad = Period.between(fecha, LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }
    }
}