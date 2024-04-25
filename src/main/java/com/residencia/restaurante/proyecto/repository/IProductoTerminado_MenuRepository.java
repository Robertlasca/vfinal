package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.ProductoTerminado_Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductoTerminado_MenuRepository extends JpaRepository<ProductoTerminado_Menu,Integer> {
}
