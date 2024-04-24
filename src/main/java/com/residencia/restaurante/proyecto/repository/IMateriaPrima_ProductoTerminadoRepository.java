package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.MateriaPrima_ProductoTerminado;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMateriaPrima_ProductoTerminadoRepository extends JpaRepository<MateriaPrima_ProductoTerminado,Integer> {

    List<MateriaPrima_ProductoTerminado> getAllByProductoTerminado(ProductoTerminado productoTerminado);
}
