package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoPersona;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteEntity extends BaseEntity {

    private final String tipoPersona;
    private final String nombre;
    private final String apellido;
    private final String direccion;
    private final String telefono;
    private final String banco;
    private final LocalDate fechaAlta;
    private final LocalDate fechaNacimiento;
    private List<Long> cuentas;

    public ClienteEntity(Cliente cliente) {
        super(cliente.getDni());
        this.tipoPersona = cliente.getTipoPersona() != null ? cliente.getTipoPersona().getDescripcion() : null;
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.direccion = cliente.getDireccion();
        this.telefono = cliente.getTelefono();
        this.banco = cliente.getBanco();
        this.fechaAlta = cliente.getFechaAlta();
        this.fechaNacimiento = cliente.getFechaNacimiento();
        this.cuentas = new ArrayList<>();
        if (cliente.getCuentas() != null && !cliente.getCuentas().isEmpty()) {
            for (Cuenta c: cliente.getCuentas()) {
                cuentas.add(c.getNumeroCuenta());
            }
        }
    }

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setDni(this.getId());
        cliente.setNombre(this.nombre);
        cliente.setApellido(this.apellido);
        cliente.setDireccion(this.direccion);
        cliente.setTelefono(this.telefono);
        cliente.setBanco(this.banco);
        cliente.setTipoPersona(this.tipoPersona != null ? TipoPersona.fromString(this.tipoPersona) : null);
        cliente.setFechaAlta(this.fechaAlta);
        cliente.setFechaNacimiento(this.fechaNacimiento);
        return cliente;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getBanco() {
        return banco;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public List<Long> getCuentas() {
        return cuentas;
    }
}