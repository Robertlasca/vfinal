package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.DetalleOrdenMenu;
import com.residencia.restaurante.proyecto.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDetalleOrden_MenuRepository extends JpaRepository<DetalleOrdenMenu,Integer> {
    List<DetalleOrdenMenu> getAllByOrden(Orden orden);
    List<DetalleOrdenMenu> getAllByEstadoEqualsIgnoreCase(String estado);
}
