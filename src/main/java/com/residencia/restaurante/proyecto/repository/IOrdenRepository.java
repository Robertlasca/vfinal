package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden,Integer> {
    Optional<Orden> findOrdensByMesaId(Integer id);

    @Query("SELECT MAX(o.folio) FROM Orden o")
    Integer findMaxFolio();

    @Query("SELECT o FROM Orden o WHERE EXTRACT(DAY FROM o.fechaHoraApertura) = :day AND EXTRACT(MONTH FROM o.fechaHoraApertura) = :month AND EXTRACT(YEAR FROM o.fechaHoraApertura) = :year ORDER BY o.fechaHoraApertura DESC")
    List<Orden> findOrdenesDelDia(@Param("day") int day, @Param("month") int month, @Param("year") int year);


    @Query("SELECT COUNT(o) > 0 FROM Orden o WHERE o.mesa.areaServicio.id = :areaServicioId AND o.estado NOT IN ('Terminada', 'Cancelado')")
    boolean existsByAreaServicioIdAndEstadoNotIn(Integer areaServicioId);

    @Query("SELECT COUNT(o) > 0 FROM Orden o WHERE o.caja.id = :cajaId AND o.estado NOT IN ('Terminada', 'Cancelado')")
    boolean existsByCajaIdAndEstadoNotIn(Integer cajaId);



    @Query("SELECT o FROM Orden o WHERE o.caja.id = :cajaId AND o.estado = :estado")
    List<Orden> findOrdenesByCajaIdAndEstado(@Param("cajaId") Integer cajaId, @Param("estado") String estado);
}
