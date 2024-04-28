package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.controller.IComanderoController;
import com.residencia.restaurante.proyecto.service.IComanderoService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class ComanderoControllerImpl implements IComanderoController {
    @Autowired
    private IComanderoService comanderoService;
    @Override
    public ResponseEntity<String> abrirOrden(Map<String, String> objetoMap) {
        try {
            return comanderoService.abrirOrden(objetoMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
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
}
