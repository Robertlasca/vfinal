package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.controller.IMenuController;
import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.MenuDTO;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.ProductoTerminado;
import com.residencia.restaurante.proyecto.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MenuControllerImpl implements IMenuController {
    @Autowired
    private IMenuService menuService;
    @Override
    public ResponseEntity<List<MenuDTO>> obtenerActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<MenuDTO>> obtenerNoActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<MenuDTO>> obtenerTodos() {
        return null;
    }

    @Override
    public ResponseEntity<String> agregar(String nombre, String descripcion, double costoProduccion, double margenGanancia, double precioVenta, MultipartFile file, int idCategoria, int idCocina, String receta) {
        return null;
    }

    @Override
    public ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<ProductoTerminado> obtenerProductoTerminado(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerRecetaId(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerIngredientesOProductosTerminadosIdCocina(Integer id) {
        try {
            return menuService.obtenerIngredientesOProductosTerminadosIdCocina(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<RecetaDTO> obtenerIngredientesOProductosTerminadosXId(Map<String, String> objetoMap) {
        try {
            return menuService.obtenerIngredientesOProductosTerminadosXId(objetoMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new RecetaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
