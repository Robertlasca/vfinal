package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.controller.IProductoTerminadoController;
import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.ProductoTerminadoDto;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import com.residencia.restaurante.proyecto.service.IProductoTerminadoService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@RestController
public class ProductoTerminadoControllerImpl implements IProductoTerminadoController {
@Autowired
private IProductoTerminadoService productoTerminadoService;
    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerNoActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerTodos() {
        try {

            return productoTerminadoService.obtenerTodos();

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<ProductoTerminadoDto>>( new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregar(String nombre, String unidadMedida, String descripcion, double stockMax, double stockMin, MultipartFile file, int idCategoria, String materias) {
        try {
            return productoTerminadoService.agregar(nombre,unidadMedida,descripcion,stockMax,stockMin,file,idCategoria,materias);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId(Integer id) {
        try {
            return productoTerminadoService.obtenerMateriaPrimaId(id);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new IngredienteProductoTerminado(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoTerminado> obtenerProductoTerminado(Integer id) {
        try {
            return productoTerminadoService.obtenerProductoTerminado(id);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ProductoTerminado>(new ProductoTerminado(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerRecetaId(Integer id) {
        try {
            return productoTerminadoService.obtenerReceta(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
