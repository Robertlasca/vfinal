package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Cocina;
import com.residencia.restaurante.proyecto.entity.Cocina_Utensilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICocina_UtensilioRepository extends JpaRepository<Cocina_Utensilio,Integer> {
    List<Cocina_Utensilio> findAllByCocina_Id(Integer id);
    List<Cocina_Utensilio> findAllByUtensilio_Id(Integer id);
    @Query("SELECT a FROM Cocina a WHERE a.id NOT IN (SELECT i.cocina.id FROM Cocina_Utensilio i WHERE i.utensilio.id = :utensilioId) AND a.visibilidad=true")
    List<Cocina> findEstacionNotRelatedToUtensilio(@Param("utensilioId") Integer utensilioId);

}
