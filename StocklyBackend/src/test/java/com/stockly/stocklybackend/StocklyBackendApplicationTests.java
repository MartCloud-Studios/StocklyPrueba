package com.stockly.stocklybackend;

import com.stockly.stocklybackend.catalogo.entity.UnidadMedida;
import com.stockly.stocklybackend.catalogo.repository.UnidadMedidaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StocklyBackendApplicationTests {
    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void creaLaUnidadBaseGlobalYLaIncluyeEnLaListaDeUnidades() {
        UnidadMedida unidadBase = unidadMedidaRepository
                .findByNegocioIsNullAndNombreIgnoreCase("Unidad")
                .orElseThrow();

        Assertions.assertNull(unidadBase.getNegocio());
        Assertions.assertTrue(unidadBase.isActivo());
        Assertions.assertTrue(unidadMedidaRepository
                .findByNegocioIdOrNegocioIsNullOrderByNombreAsc(Long.MAX_VALUE)
                .stream()
                .anyMatch(unidad -> unidad.getId().equals(unidadBase.getId())));
    }
}
