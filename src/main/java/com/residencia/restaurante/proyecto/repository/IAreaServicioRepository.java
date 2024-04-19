package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.AreaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAreaServicioRepository extends JpaRepository<AreaServicio,Integer> {
}
