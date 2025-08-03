package ar.edu.utn.frbb.tup.model.dto;

import java.util.List;

public class ConsultaPrestamosDto {
    private long numeroCliente;
    private List<PrestamoInfoDto> prestamos;

    // Getters y Setters
    public long getNumeroCliente() { return numeroCliente; }
    public void setNumeroCliente(long numeroCliente) { this.numeroCliente = numeroCliente; }

    public List<PrestamoInfoDto> getPrestamos() { return prestamos; }
    public void setPrestamos(List<PrestamoInfoDto> prestamos) { this.prestamos = prestamos; }
}