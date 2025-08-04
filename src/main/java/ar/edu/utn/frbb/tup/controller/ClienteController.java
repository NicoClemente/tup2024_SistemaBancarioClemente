package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de clientes bancarios.
 * Proporciona endpoints para operaciones CRUD de clientes.
 */
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;

    /**
     * Endpoint para crear un nuevo cliente en el sistema bancario.
     * Valida los datos del cliente antes de proceder con la creación.
     * 
     * @param clienteDto Datos del cliente a crear
     * @return Cliente creado con éxito
     * @throws ClienteAlreadyExistsException si el cliente ya existe en el sistema
     * 
     * POST /cliente
     */
    @PostMapping
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        // Valida datos de entrada
        clienteValidator.validate(clienteDto);

        // Procesa alta de cliente
        return clienteService.darDeAltaCliente(clienteDto);
    }

    /**
     * Endpoint para buscar un cliente específico por su DNI.
     * 
     * @param dni Número de documento del cliente a buscar
     * @return ResponseEntity con el cliente encontrado o 404 si no existe
     * 
     * GET /cliente/{dni}
     */
    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable long dni) {
        try {
            Cliente cliente = clienteService.buscarClientePorDni(dni);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener la lista completa de clientes registrados.
     * 
     * @return ResponseEntity con la lista de todos los clientes
     * 
     * GET /cliente
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Endpoint para actualizar los datos de un cliente existente.
     * Valida los nuevos datos antes de proceder con la actualización.
     * 
     * @param dni        DNI del cliente a actualizar
     * @param clienteDto Nuevos datos del cliente
     * @return ResponseEntity con el cliente actualizado o 404 si no existe
     * 
     * PUT /cliente/{dni}
     */
    @PutMapping("/{dni}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable long dni, @RequestBody ClienteDto clienteDto) {
        try {
            clienteValidator.validate(clienteDto);
            Cliente clienteActualizado = clienteService.actualizarCliente(dni, clienteDto);
            return ResponseEntity.ok(clienteActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para eliminar un cliente del sistema.
     * Solo permite eliminar clientes sin cuentas con saldo positivo.
     * 
     * @param dni DNI del cliente a eliminar
     * @return ResponseEntity vacío con código 200 si se eliminó correctamente, 404
     *         si no existe
     * 
     * DELETE /cliente/{dni}
     */
    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable long dni) {
        try {
            clienteService.eliminarCliente(dni);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}