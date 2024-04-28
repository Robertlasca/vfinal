package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.MateriaPrima_Menu;
import com.residencia.restaurante.proyecto.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMateriaPrima_MenuRepository extends JpaRepository<MateriaPrima_Menu,Integer> {
    List<MateriaPrima_Menu> getAllByMenu(Menu menu);
}
