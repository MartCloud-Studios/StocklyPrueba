package com.stockly.stocklybackend;

import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.entity.Rol;
import com.stockly.stocklybackend.acceso.repository.RolRepository;
import com.stockly.stocklybackend.catalogo.entity.UnidadMedida;
import com.stockly.stocklybackend.catalogo.repository.UnidadMedidaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StocklyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StocklyBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner rolesIniciales(RolRepository rolRepository) {
        return args -> {
            for (NombreRol nombre : NombreRol.values()) {
                if (rolRepository.findByNombre(nombre).isEmpty()) {
                    Rol rol = new Rol();
                    rol.setNombre(nombre);
                    rolRepository.save(rol);
                }
            }
        };
    }

    @Bean
    CommandLineRunner unidadBaseInicial(UnidadMedidaRepository unidadMedidaRepository) {
        return args -> {
            if (unidadMedidaRepository
                    .findByNegocioIsNullAndNombreIgnoreCase("Unidad").isEmpty()) {
                UnidadMedida unidad = new UnidadMedida();
                unidad.setNombre("Unidad");
                unidad.setActivo(true);
                unidadMedidaRepository.save(unidad);
            }
        };
    }
}
