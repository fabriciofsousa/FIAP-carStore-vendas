package br.com.fiap.vendas.repository;

import br.com.fiap.vendas.infra.database.entity.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusValidatorTest {

    @Test
    void deveRetornarEnumQuandoStatusValidoCaseInsensitive() {
        assertEquals(Status.INICIADA, Status.validateStatus("INICIADA"));
        assertEquals(Status.CONCLUIDA, Status.validateStatus("concluida"));
        assertEquals(Status.CANCELADA, Status.validateStatus("CaNcElAdA"));
    }

    @Test
    void deveLancarExcecaoQuandoStatusInvalido() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Status.validateStatus("FINALIZADA")
        );
        assertTrue(ex.getMessage().contains("Status inválido"));
    }

    @Test
    void deveLancarExcecaoQuandoStatusVazioOuEspaco() {
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus(""));
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus("   "));
    }

    @Test
    void deveLancarNullPointerQuandoStatusForNulo() {
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus(null));
    }

    @Test
    void deveLancarExcecaoQuandoStringParecidaMasNaoIgual() {
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus("INICIADAS"));
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus("iniciad"));
        assertThrows(IllegalArgumentException.class, () -> Status.validateStatus("CANCELADA!"));
    }
}
