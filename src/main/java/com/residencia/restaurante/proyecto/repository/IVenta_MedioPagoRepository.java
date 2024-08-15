package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Venta_MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVenta_MedioPagoRepository extends JpaRepository<Venta_MedioPago,Integer> {
    List<Venta_MedioPago> getAllByVenta_Id(Integer id);

    @Query("SELECT SUM(vmp.pagoRecibido) FROM Venta_MedioPago vmp " +
            "JOIN vmp.venta v " +
            "JOIN v.arqueo a " +
            "WHERE vmp.medioPago.id = :medioPagoId " +
            "AND a.id = :arqueoId")
    Double sumPagoRecibidoByMedioPagoIdAndArqueoId(@Param("medioPagoId") Integer medioPagoId,
                                                   @Param("arqueoId") Integer arqueoId);
}
