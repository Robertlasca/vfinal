package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden,Integer> {
}
