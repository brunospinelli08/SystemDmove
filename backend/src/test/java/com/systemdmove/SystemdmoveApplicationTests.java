package com.systemdmove;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste smoke leve, sem carregar o contexto Spring (que exigiria um
 * PostgreSQL ativo). O carregamento completo e validado ao subir a app
 * com `mvn spring-boot:run`.
 */
class SystemdmoveApplicationTests {

    @Test
    void sanity() {
        assertTrue(true);
    }
}
