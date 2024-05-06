package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMenuRepository extends JpaRepository<Menu,Integer> {
    boolean existsByNombreLikeIgnoreCase(String nombre);

    List<Menu> getAllByVisibilidadTrue();
    List<Menu> getAllByVisibilidadFalse();
}
