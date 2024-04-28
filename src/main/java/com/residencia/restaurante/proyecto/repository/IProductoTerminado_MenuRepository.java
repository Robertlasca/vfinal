package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Menu;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado_Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductoTerminado_MenuRepository extends JpaRepository<ProductoTerminado_Menu,Integer> {
    List<ProductoTerminado_Menu> getAllByMenu(Menu menu);
}
