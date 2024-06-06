package com.residencia.restaurante.proyecto.repository;

import com.residencia.restaurante.proyecto.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IVentaRepository extends JpaRepository<Venta,Integer> {
    List<Venta> getAllByArqueo_Caja_Nombre(String caja);


}
