package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductoTerminadoRepository extends JpaRepository<ProductoTerminado,Integer> {
}
