package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.repository.IArqueoRepository;
import com.residencia.restaurante.proyecto.repository.IDetalleOrden_MenuRepository;
import com.residencia.restaurante.proyecto.repository.IDetalleOrden_ProductoNormalRepository;
import com.residencia.restaurante.proyecto.repository.IOrdenRepository;
import com.residencia.restaurante.proyecto.service.IComanderoService;
import com.residencia.restaurante.security.utils.Utils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ComanderoServiceImpl implements IComanderoService {
    @Autowired
    private IArqueoRepository arqueoRepository;
    @Autowired
    private IOrdenRepository ordenRepository;
    @Autowired
    private IDetalleOrden_MenuRepository ordenMenuRepository;
    @Autowired
    private IDetalleOrden_ProductoNormalRepository detalleOrdenProductoNormalRepository;
    @Override
    public ResponseEntity<String> abrirOrden(Map<String, String> objetoMap) {
       try {
           if(objetoMap.containsKey("idMesa") ){

           }


       }catch (Exception e){
           e.printStackTrace();
       }
       return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> asignarPlatillos(Map<String, String> objetoMap) {
        return null;
    }
}
