package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.MovimientosCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovimientoCajaRepository extends JpaRepository<MovimientosCaja,Integer> {
    List<MovimientosCaja> findAllByTipoMovimientoEqualsIgnoreCase(String tipo);

    List<MovimientosCaja> findAllByArqueo_Id(Integer id);

    @Query("SELECT SUM(m.cantidad) FROM MovimientosCaja m WHERE m.tipoMovimiento = 'retiro' AND m.arqueo.id = :arqueoId")
    Double sumCantidadByTipoRetiroAndArqueoId(@Param("arqueoId") Integer arqueoId);

    @Query("SELECT SUM(m.cantidad) FROM MovimientosCaja m WHERE m.tipoMovimiento = 'ingreso' AND m.arqueo.id = :arqueoId")
    Double sumCantidadByTipoIngresoAndArqueoId(@Param("arqueoId") Integer arqueoId);


}
