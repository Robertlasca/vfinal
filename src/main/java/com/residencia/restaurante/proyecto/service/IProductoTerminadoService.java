package com.residencia.restaurante.proyecto.service;

import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.ProductoTerminadoDto;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductoTerminadoService {

    ResponseEntity<List<ProductoTerminadoDto>> obtenerActivos();


    ResponseEntity<List<ProductoTerminadoDto>> obtenerNoActivos();


    ResponseEntity<List<ProductoTerminadoDto>> obtenerTodos();


    ResponseEntity<String> agregar(String nombre,
                                    String unidadMedida,
                                   String descripcion,
                                   double stockMax,
                                   double stockMin,
                                   MultipartFile file,
                                    int idCategoria,
                                   String materias
    );


    ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId( Integer id);

    ResponseEntity<ProductoTerminado> obtenerProductoTerminado(Integer id);

    ResponseEntity<List<RecetaDTO>> obtenerReceta(Integer id);
}
