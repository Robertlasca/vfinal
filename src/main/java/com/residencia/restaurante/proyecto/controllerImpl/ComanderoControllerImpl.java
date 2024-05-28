package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.controller.IComanderoController;
import com.residencia.restaurante.proyecto.dto.ComandaDTO;
import com.residencia.restaurante.proyecto.dto.ProductoDto;
import com.residencia.restaurante.proyecto.entity.Orden;
import com.residencia.restaurante.proyecto.service.IComanderoService;
import com.residencia.restaurante.security.utils.Utils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
public class ComanderoControllerImpl implements IComanderoController {
    @Autowired
    private IComanderoService comanderoService;
    @Override
    public ResponseEntity<Orden> abrirOrden(Map<String, String> objetoMap) {
        try {
            return comanderoService.abrirOrden(objetoMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Orden>(new Orden(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> asignarPlatillos(Map<String, String> objetoMap) {
        try {
            return comanderoService.asignarPlatillos(objetoMap);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoDto>> obtenerProductos() {
        try {
            return comanderoService.obtenerProductos();

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoDto> obtenerProducto(Map<String, String> objetoMap) {
        try {
            return comanderoService.obtenerProducto(objetoMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ProductoDto(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Orden>> obtenerOrdenes() {
        try {
            return  comanderoService.obtenerOrdenes();

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ComandaDTO> obtenerComandaPorIdOrden(Integer id) {
        try {
            return comanderoService.obtenerComandaPorIdOrden(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ComandaDTO> obtenerComandaPorIdOrdenMesa(Integer id) {
        try {
            return comanderoService.obtenerComandaPorIdOrdenMesa(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cerrarCuenta(Map<String,String> objetoMap) {
        try{
            return comanderoService.cerrarCuenta(objetoMap);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
