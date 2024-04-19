package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.DetalleOrdenMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleOrden_MenuRepository extends JpaRepository<DetalleOrdenMenu,Integer> {
}
