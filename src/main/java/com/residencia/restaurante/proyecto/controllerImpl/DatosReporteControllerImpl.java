package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.controller.IDatosReporteController;
import com.residencia.restaurante.proyecto.dto.AlmacenDTO;
import com.residencia.restaurante.proyecto.dto.TotalVentasDTO;
import com.residencia.restaurante.proyecto.dto.VentasDTO;
import com.residencia.restaurante.proyecto.entity.Inventario;
import com.residencia.restaurante.proyecto.service.IDatosReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DatosReporteControllerImpl implements IDatosReporteController {
    @Autowired
    private IDatosReporteService datosReporteService;
    @Override
    public ResponseEntity<List<Inventario>> obtenerInventarioAgotadoXAlmacen(Integer id) {
        try{
            return datosReporteService.obtenerInventarioAgotadoXAlmacen(id);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Inventario>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> ventasXDia(String dia) {
        try {
            return datosReporteService.obtenerDatosVentasXDia(dia);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new TotalVentasDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> ventasXDMes(String diaInicio, String diaFin) {
        try {
            return datosReporteService.obtenerDatosVentasXMes(diaInicio,diaFin);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new TotalVentasDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> ventasXMes(String mes) {

        try {
            return datosReporteService.obterDatosVentasMes(mes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
