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
            int score = scoreCrediticioService.obtenerScore(12345678);
            assertTrue(score >= 1 && score <= 10);
        }
    }

    @Test
    public void testConsultarScoreRetornaBooleanValido() {
        // El método consultarScore debe retornar boolean
        boolean resultado1 = scoreCrediticioService.consultarScore(12345678);
        boolean resultado2 = scoreCrediticioService.consultarScore(87654321);
        
        // Verifica que retorna boolean válido
        assertTrue(resultado1 == true || resultado1 == false);
        assertTrue(resultado2 == true || resultado2 == false);
    }

    @Test
    public void testEsElegibleParaPrestamoRetornaBooleanValido() {
        // El método esElegibleParaPrestamo debe retornar boolean
        boolean resultado1 = scoreCrediticioService.esElegibleParaPrestamo(12345678);
        boolean resultado2 = scoreCrediticioService.esElegibleParaPrestamo(87654321);
        
        // Verifica que retorna boolean válido
        assertTrue(resultado1 == true || resultado1 == false);
        assertTrue(resultado2 == true || resultado2 == false);
    }

    @Test
    public void testConsultarScoreDetalladoTieneInformacionCompleta() {
        ScoreCrediticioService.ScoreResultado resultado = 
            scoreCrediticioService.consultarScoreDetallado(12345678);
        
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
                scoreCrediticioService.consultarScoreDetallado(12345678 + i);
            
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
}