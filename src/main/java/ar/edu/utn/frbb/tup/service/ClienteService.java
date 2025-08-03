package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        Cliente cliente = new Cliente(clienteDto);

        if (clienteDao.find(cliente.getDni(), false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con DNI " + cliente.getDni());
        }

        if (cliente.getEdad() < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }

        clienteDao.save(cliente);
        return cliente;
    }

    public Cliente actualizarCliente(long dni, ClienteDto clienteDto) {
        Cliente clienteExistente = buscarClientePorDni(dni);              
        clienteExistente.setNombre(clienteDto.getNombre());
        clienteExistente.setApellido(clienteDto.getApellido());
        clienteExistente.setDireccion(clienteDto.getDireccion());
        clienteExistente.setTelefono(clienteDto.getTelefono());
        clienteExistente.setBanco(clienteDto.getBanco());
        clienteExistente.setTipoPersona(TipoPersona.fromString(clienteDto.getTipoPersona()));
        
        if (clienteDto.getFechaNacimiento() != null) {
            clienteExistente.setFechaNacimiento(LocalDate.parse(clienteDto.getFechaNacimiento()));
        }

        // Valida edad después de la actualización
        if (clienteExistente.getEdad() < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }

        clienteDao.save(clienteExistente);
        return clienteExistente;
    }

    public void eliminarCliente(long dni) {
        Cliente cliente = buscarClientePorDni(dni);
        
        // Verifica que no tenga cuentas con saldo
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getBalance() > 0) {
                throw new IllegalArgumentException("No se puede eliminar un cliente con cuentas que tienen saldo positivo");
            }
        }
        
        clienteDao.delete(dni);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDao.findAll();
    }

    public void agregarCuenta(Cuenta cuenta, long dniTitular) throws TipoCuentaAlreadyExistsException {
        Cliente titular = buscarClientePorDni(dniTitular);
        cuenta.setTitular(titular);
        if (titular.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getMoneda())) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda");
        }
        titular.addCuenta(cuenta);
        clienteDao.save(titular);
    }

    public Cliente buscarClientePorDni(long dni) {
        Cliente cliente = clienteDao.find(dni, true);
        if(cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        return cliente;
    }
}