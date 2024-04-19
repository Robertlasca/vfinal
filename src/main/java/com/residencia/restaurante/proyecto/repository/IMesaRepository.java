package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMesaRepository extends JpaRepository<Mesa,Integer> {
}
