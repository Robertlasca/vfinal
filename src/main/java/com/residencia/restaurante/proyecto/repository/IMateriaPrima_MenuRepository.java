package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.MateriaPrima_Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMateriaPrima_MenuRepository extends JpaRepository<MateriaPrima_Menu,Integer> {
}
