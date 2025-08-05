package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class ClienteValidator {

    public void validate(ClienteDto clienteDto) {
        validateTipoPersona(clienteDto.getTipoPersona());
        validateFechaNacimiento(clienteDto.getFechaNacimiento());
        validateEdad(clienteDto.getFechaNacimiento());
        validateNombre(clienteDto.getNombre());
        validateApellido(clienteDto.getApellido());
        validateDni(clienteDto.getDni());
        validateDireccion(clienteDto.getDireccion());
        validateTelefono(clienteDto.getTelefono());
        validateBanco(clienteDto.getBanco());
    }

    private void validateTipoPersona(String tipoPersona) {
        if (tipoPersona == null || (!tipoPersona.equals("F") && !tipoPersona.equals("J"))) {
            throw new IllegalArgumentException("El tipo de persona debe ser 'F' (Física) o 'J' (Jurídica)");
        }
    }

    private void validateFechaNacimiento(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        try {
            LocalDate.parse(fechaNacimiento);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error en el formato de fecha. Use el formato: YYYY-MM-DD");
        }
    }

    private void validateEdad(String fechaNacimiento) {
        LocalDate fecha = LocalDate.parse(fechaNacimiento);
        int edad = Period.between(fecha, LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }
    }

    private void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (nombre.length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres");
        }
    }

    private void validateApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (apellido.length() < 2) {
            throw new IllegalArgumentException("El apellido debe tener al menos 2 caracteres");
        }
    }

    private void validateDni(long dni) {
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI debe ser un número positivo");
        }
        if (String.valueOf(dni).length() < 7 || String.valueOf(dni).length() > 8) {
            throw new IllegalArgumentException("El DNI debe tener entre 7 y 8 dígitos");
        }
    }

    private void validateDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        if (direccion.length() < 5) {
            throw new IllegalArgumentException("La dirección debe tener al menos 5 caracteres");
        }
    }

    private void validateTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (telefono.length() < 8) {
            throw new IllegalArgumentException("El teléfono debe tener al menos 8 dígitos");
        }
        if (!telefono.matches("[0-9\\s\\-\\(\\)\\+]+")) {
            throw new IllegalArgumentException("El teléfono contiene caracteres no válidos");
        }
    }

    private void validateBanco(String banco) {
        if (banco == null || banco.trim().isEmpty()) {
            throw new IllegalArgumentException("El banco es obligatorio");
        }
    }
}