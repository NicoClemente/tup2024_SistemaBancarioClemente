package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * Servicio que simula la consulta a un servicio externo de calificación
 * crediticia.
 * Genera un score numérico del 1 al 10, donde valores >= 6 permiten acceso a
 * préstamos.
 */
@Service
public class ScoreCrediticioService {

    private static final int SCORE_MINIMO_APROBACION = 6;
    private static final int SCORE_MAXIMO = 10;
    private static final int SCORE_MINIMO = 1;

    private final Random random = new Random();

    /**
     * Consulta el score crediticio de un cliente por DNI.
     * Método principal llamado desde PrestamoService.
     * 
     * @param dni 
     * @return true si tiene buen score (>=6), false si tiene mal score (<6)
     */
    public boolean consultarScore(long dni) {
        return esElegibleParaPrestamo(dni);
    }

    /**
     * Genera un score crediticio aleatorio entre 1 y 10.
     * 
     * @param dni DNI del cliente
     * @return Score crediticio del 1 al 10
     */
    public int obtenerScore(long dni) {
        // Genera score aleatorio entre 1 y 10
        return random.nextInt(SCORE_MAXIMO) + SCORE_MINIMO;
    }

    /**
     * Evalúa si un cliente tiene score suficiente para acceder a un préstamo.
     * 
     * @param dni DNI del cliente
     * @return true si el score es >= 6, false en caso contrario
     */
    public boolean esElegibleParaPrestamo(long dni) {
        int score = obtenerScore(dni);
        return score >= SCORE_MINIMO_APROBACION;
    }

    /**
     * Obtiene el score y devuelve información detallada.
     * 
     * @param dni DNI del cliente
     * @return Objeto con score y elegibilidad
     */
    public ScoreResultado consultarScoreDetallado(long dni) {
        int score = obtenerScore(dni);
        boolean esElegible = score >= SCORE_MINIMO_APROBACION;
        String mensaje = generarMensajeScore(score);

        return new ScoreResultado(score, esElegible, mensaje);
    }

    /**
     * Genera mensaje explicativo según el score obtenido.
     * 
     * @param score Score obtenido
     * @return Mensaje descriptivo del score
     */
    private String generarMensajeScore(int score) {
        if (score >= 9) {
            return "Excelente calificación crediticia";
        } else if (score >= 7) {
            return "Buena calificación crediticia";
        } else if (score >= 6) {
            return "Calificación crediticia aceptable";
        } else if (score >= 4) {
            return "Calificación crediticia regular - No elegible para préstamos";
        } else {
            return "Calificación crediticia deficiente - No elegible para préstamos";
        }
    }

    /* Clase interna para encapsular el resultado del score. */
    public static class ScoreResultado {
        private final int score;
        private final boolean esElegible;
        private final String mensaje;

        public ScoreResultado(int score, boolean esElegible, String mensaje) {
            this.score = score;
            this.esElegible = esElegible;
            this.mensaje = mensaje;
        }

        public int getScore() {
            return score;
        }

        public boolean isElegible() {
            return esElegible;
        }

        public String getMensaje() {
            return mensaje;
        }

        @Override
        public String toString() {
            return String.format("Score: %d/10 - %s - Elegible: %s",
                    score, mensaje, esElegible ? "Sí" : "No");
        }
    }
}