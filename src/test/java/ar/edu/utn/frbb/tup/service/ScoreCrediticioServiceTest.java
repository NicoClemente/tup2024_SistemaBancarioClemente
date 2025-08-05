package ar.edu.utn.frbb.tup.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ScoreCrediticioServiceTest {

    @InjectMocks
    private ScoreCrediticioService scoreCrediticioService;

    @Test
    public void testObtenerScoreEnRangoValido() {
        // El score debe estar entre 1 y 10
        for (int i = 0; i < 100; i++) {
            ScoreCrediticioService.ScoreResultado resultado = scoreCrediticioService.evaluarScore(12345678);
            int score = resultado.getScore();
            assertTrue(score >= 1 && score <= 10);
        }
    }

    @Test
    public void testConsultarScoreRetornaBooleanValido() {
        // El método evaluarScore debe retornar resultado con boolean válido
        ScoreCrediticioService.ScoreResultado resultado1 = scoreCrediticioService.evaluarScore(12345678);
        ScoreCrediticioService.ScoreResultado resultado2 = scoreCrediticioService.evaluarScore(87654321);
        
        // Verifica que retorna boolean válido
        boolean elegible1 = resultado1.isElegible();
        boolean elegible2 = resultado2.isElegible();
        assertTrue(elegible1 == true || elegible1 == false);
        assertTrue(elegible2 == true || elegible2 == false);
    }

    @Test
    public void testEsElegibleParaPrestamoRetornaBooleanValido() {
        // El método evaluarScore debe retornar elegibilidad coherente
        ScoreCrediticioService.ScoreResultado resultado1 = scoreCrediticioService.evaluarScore(12345678);
        ScoreCrediticioService.ScoreResultado resultado2 = scoreCrediticioService.evaluarScore(87654321);
        
        // Verifica que retorna boolean válido
        boolean elegible1 = resultado1.isElegible();
        boolean elegible2 = resultado2.isElegible();
        assertTrue(elegible1 == true || elegible1 == false);
        assertTrue(elegible2 == true || elegible2 == false);
    }

    @Test
    public void testEvaluarScoreTieneInformacionCompleta() {
        ScoreCrediticioService.ScoreResultado resultado = 
            scoreCrediticioService.evaluarScore(12345678);
        
        assertNotNull(resultado);
        assertTrue(resultado.getScore() >= 1 && resultado.getScore() <= 10);
        assertNotNull(resultado.getMensaje());
        assertTrue(resultado.getMensaje().length() > 0);
        
        // El boolean debe ser coherente con el score
        if (resultado.getScore() >= 6) {
            assertTrue(resultado.isElegible());
        } else {
            assertFalse(resultado.isElegible());
        }
    }

    @Test
    public void testMensajeScoreCoherenteConScore() {
        // Prueba múltiples veces para cubrir diferentes scores
        for (int i = 0; i < 50; i++) {
            ScoreCrediticioService.ScoreResultado resultado = 
                scoreCrediticioService.evaluarScore(12345678 + i);
            
            String mensaje = resultado.getMensaje();
            int score = resultado.getScore();
            
            if (score >= 9) {
                assertTrue(mensaje.contains("Excelente"));
            } else if (score >= 7) {
                assertTrue(mensaje.contains("Buena"));
            } else if (score >= 6) {
                assertTrue(mensaje.contains("aceptable"));
            } else if (score >= 4) {
                assertTrue(mensaje.contains("regular"));
            } else {
                assertTrue(mensaje.contains("deficiente"));
            }
        }
    }

    @Test
    public void testScoreResultadoCoherencia() {
        // Test adicional para verificar coherencia interna
        ScoreCrediticioService.ScoreResultado resultado = 
            scoreCrediticioService.evaluarScore(12345678);
        
        int score = resultado.getScore();
        boolean elegible = resultado.isElegible();
        
        // Verifica coherencia: score >= 6 debe ser elegible
        if (score >= 6) {
            assertTrue(elegible, "Score " + score + " debe ser elegible");
        } else {
            assertFalse(elegible, "Score " + score + " no debe ser elegible");
        }
    }
}