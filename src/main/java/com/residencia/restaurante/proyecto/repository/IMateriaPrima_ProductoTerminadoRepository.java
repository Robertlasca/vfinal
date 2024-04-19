package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.MateriaPrima_ProductoTerminado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMateriaPrima_ProductoTerminadoRepository extends JpaRepository<MateriaPrima_ProductoTerminado,Integer> {
}
