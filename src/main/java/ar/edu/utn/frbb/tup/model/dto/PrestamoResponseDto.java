package ar.edu.utn.frbb.tup.model.dto;

import ar.edu.utn.frbb.tup.model.CuotaPrestamo;
import java.util.List;

public class PrestamoResponseDto {
    private String estado;
    private String mensaje;
    private List<CuotaPrestamo> planPagos;

    public PrestamoResponseDto() {}

    public PrestamoResponseDto(String estado, String mensaje, List<CuotaPrestamo> planPagos) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.planPagos = planPagos;
    }

    // Getters y Setters
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<CuotaPrestamo> getPlanPagos() { return planPagos; }
    public void setPlanPagos(List<CuotaPrestamo> planPagos) { this.planPagos = planPagos; }
}