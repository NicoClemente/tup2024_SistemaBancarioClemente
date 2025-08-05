package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * Servicio que simula la consulta a un servicio externo de calificación crediticia.
 * Genera un score numérico del 1 al 10, donde valores >= 6 permiten acceso a préstamos.
 */
@Service
public class ScoreCrediticioService {

    private static final int SCORE_MINIMO_APROBACION = 6;
    private static final int SCORE_MAXIMO = 10;
    private static final int SCORE_MINIMO = 1;

    private final Random random = new Random();

    /**
     * Consulta el score crediticio de un cliente y evalúa su elegibilidad.
     * 
     * @param dni DNI del cliente
     * @return Objeto ScoreResultado con score, elegibilidad y mensaje
     */
    public ScoreResultado evaluarScore(long dni) {
        int score = generarScore();
        boolean esElegible = score >= SCORE_MINIMO_APROBACION;
        String mensaje = generarMensajeScore(score);
        return new ScoreResultado(score, esElegible, mensaje);
    }

    /**
     * Genera un score crediticio aleatorio entre 1 y 10.
     */
    private int generarScore() {
        return random.nextInt(SCORE_MAXIMO) + SCORE_MINIMO;
    }

    /**
     * Genera mensaje explicativo según el score obtenido.
     */
    private String generarMensajeScore(int score) {
        if (score >= 9) return "Excelente calificación crediticia";
        if (score >= 7) return "Buena calificación crediticia";
        if (score >= 6) return "Calificación crediticia aceptable";
        if (score >= 4) return "Calificación crediticia regular - No elegible para préstamos";
        return "Calificación crediticia deficiente - No elegible para préstamos";
    }

    /** Resultado del score crediticio */
    public static class ScoreResultado {
        private final int score;
        private final boolean esElegible;
        private final String mensaje;

        public ScoreResultado(int score, boolean esElegible, String mensaje) {
            this.score = score;
            this.esElegible = esElegible;
            this.mensaje = mensaje;
        }

        public int getScore() { return score; }
        public boolean isElegible() { return esElegible; }
        public String getMensaje() { return mensaje; }

        @Override
        public String toString() {
            return String.format("Score: %d/10 - %s - Elegible: %s",
                    score, mensaje, esElegible ? "Sí" : "No");
        }
    }
}
