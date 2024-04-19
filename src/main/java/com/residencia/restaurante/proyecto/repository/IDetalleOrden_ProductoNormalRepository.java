package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.DetalleOrden_ProductoNormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleOrden_ProductoNormalRepository extends JpaRepository<DetalleOrden_ProductoNormal,Integer> {
}
