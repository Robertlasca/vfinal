package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.entity.Cocina;
import com.residencia.restaurante.proyecto.entity.Impresora;
import com.residencia.restaurante.proyecto.entity.MedioPago;
import com.residencia.restaurante.proyecto.repository.IImpresoraRepository;
import com.residencia.restaurante.proyecto.service.IImpresoraService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ImpresoraServiceImpl implements IImpresoraService {

    @Autowired
    IImpresoraRepository iImpresoraRepository;
    /**
     * Obtiene una lista de todas las impresoras activas.
     * @return Lista de impresoras activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Impresora>> obtenerImpresorasActivas() {
        try {
            return new ResponseEntity<List<Impresora>>(iImpresoraRepository.getAllByEstadoTrue(),HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Impresora>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las impresoras no activas.
     * @return Lista de impresoras no activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Impresora>> obtenerImpresorasNoActivas() {
        try {
            return new ResponseEntity<List<Impresora>>(iImpresoraRepository.getAllByEstadoFalse(),HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Impresora>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las impresoras registradas.
     * @return Lista de impresoras con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Impresora>> obtenerImpresoras() {
        try {
            return new ResponseEntity<List<Impresora>>(iImpresoraRepository.findAll(),HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Impresora>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Cambia el estado de una impresora (activo/inactivo) según el ID proporcionado en el mapa de datos.
     * @param objetoMap Mapa de datos que contiene el ID de la impresora y el nuevo estado.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> cambiarEstado(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id") && objetoMap.containsKey("estado")){
                Optional<Impresora> optionalImpresora=iImpresoraRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!optionalImpresora.isEmpty()){
                    Impresora impresora= optionalImpresora.get();
                    if(objetoMap.get("estado").equalsIgnoreCase("false")){
                        impresora.setEstado(false);
                    }else{
                        impresora.setEstado(true);
                    }

                    iImpresoraRepository.save(impresora);

                    return Utils.getResponseEntity("El estado de la impresora ha sido cambiada.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("La impresora no existe.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Agrega una nueva impresora utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información de la nueva impresora.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> agregar(Map<String, String> objetoMap) {

        try {
            if(validarImpresoraMap(objetoMap,false)){
                if(!impresoraExistente(objetoMap)){
                    iImpresoraRepository.save(obtenerImpresoraDesdeMap(objetoMap,false));
                    return Utils.getResponseEntity("Impresora agregada correctamente.",HttpStatus.OK);

                }

                return Utils.getResponseEntity("Esta impresora ya existe.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Actualiza los datos de una impresora existente utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información actualizada de la impresora.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> actualizar(Map<String, String> objetoMap) {
        try {
            if(validarImpresoraMap(objetoMap,true)){
                Optional<Impresora> optional=iImpresoraRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!optional.isEmpty()){
                    if(optional.get().getNombre().equalsIgnoreCase(objetoMap.get("nombre"))){
                        iImpresoraRepository.save(obtenerImpresoraDesdeMap(objetoMap,true));
                        return Utils.getResponseEntity("Impresora actualizada.",HttpStatus.OK);
                    }else{
                        if(!impresoraExistente(objetoMap)){
                            iImpresoraRepository.save(obtenerImpresoraDesdeMap(objetoMap,true));
                            return Utils.getResponseEntity("Impresora actualizado",HttpStatus.OK);
                        }
                        return Utils.getResponseEntity("No puedes asignarle este nombre.",HttpStatus.BAD_REQUEST);
                    }

                }
                return Utils.getResponseEntity("No existe la impresora.",HttpStatus.BAD_REQUEST);
            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene los datos de una impresora específica según su ID.
     * @param id ID de la impresora.
     * @return Datos de la impresora solicitada con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<Impresora> obtenerImpresorasId(Integer id) {
        try {
            Optional<Impresora> optional=iImpresoraRepository.findById(id);

            if(optional.isPresent()){
                Impresora impresora= optional.get();
                return new ResponseEntity<>(impresora,HttpStatus.OK);
            }
            return new ResponseEntity<>(new Impresora(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new Impresora(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Impresora obtenerImpresoraDesdeMap(Map<String, String> objetoMap, boolean esAgregado) {
        Impresora impresora= new Impresora();
        boolean disponibidad=true;
        boolean estado=true;

        if(esAgregado){
            Optional<Impresora> optionalImpresora=iImpresoraRepository.findById(Integer.parseInt(objetoMap.get("id")));

            impresora.setId(Integer.parseInt(objetoMap.get("id")));
            optionalImpresora.ifPresent(value -> impresora.setEstado(value.isEstado()));

        }else {
            impresora.setEstado(true);
        }

        impresora.setNombre(objetoMap.get("nombre"));
        impresora.setDireccionIp("direccionIP");

        if(objetoMap.get("porDefecto").equalsIgnoreCase("true")){
            Optional<Impresora> impresoraDefault=iImpresoraRepository.getImpresoraByPorDefectoTrue();
            if (!impresoraDefault.isEmpty()){
                Impresora impresoraIsDefault= impresoraDefault.get();
                impresoraIsDefault.setPorDefecto((false));
                impresora.setPorDefecto(true);

                iImpresoraRepository.save(impresoraIsDefault);
            }else{
                impresora.setPorDefecto(true);
            }
        }else{
            impresora.setPorDefecto(false);
        }
        return impresora;
    }
    //Se valida una caja Existente mediante el nombre
    private boolean impresoraExistente(Map<String, String> objetoMap) {
        return iImpresoraRepository.existsImpresoraByNombreLikeIgnoreCase(objetoMap.get("nombre"));
    }
    //Se valida que el json contenga las llaves
    private boolean validarImpresoraMap(Map<String, String> objetoMap, boolean validarId) {
        if(objetoMap.containsKey("nombre")){
            if(objetoMap.containsKey("id") && validarId){
                return true;
            } else if (!validarId) {
                return true;
            }
        }
        return false;
    }
}
