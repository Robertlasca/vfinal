package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMesaRepository extends JpaRepository<Mesa,Integer> {
    List<Mesa> findAllByAreaServicio_IdAndVisibilidadTrue(Integer id);
    List<Mesa> findAllByAreaServicio_IdAndVisibilidadFalse(Integer id);

    List<Mesa> findAllByAreaServicio_Id(Integer id);
    boolean existsMesaByAreaServicio_IdAndNombreEqualsIgnoreCaseAndVisibilidadTrue(Integer id,String nombre);

    // Método para contar las mesas por el ID del área de servicio
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.areaServicio.id = :id")
    int countByAreaServicio_Id(Integer id);
}
