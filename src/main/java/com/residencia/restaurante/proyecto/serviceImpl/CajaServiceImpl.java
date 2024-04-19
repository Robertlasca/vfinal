package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.entity.Caja;
import com.residencia.restaurante.proyecto.repository.ICajaRepository;
import com.residencia.restaurante.proyecto.service.ICajaService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//Implementación de las operaciones
@Service
public class CajaServiceImpl implements ICajaService {
    @Autowired
    private ICajaRepository cajaRepository;
    /**
     * Obtiene una lista de todas las cajas activas.
     * @return Lista de cajas activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Caja>> obtenerCajasActivas() {
        try {
            return new ResponseEntity<List<Caja>>(cajaRepository.getAllByVisibilidadTrue(), HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Caja>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las cajas no activas.
     * @return Lista de cajas no activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Caja>> obtenerCajasNoActivas() {
        try {
            return new ResponseEntity<List<Caja>>(cajaRepository.getAllByVisibilidadFalse(), HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Caja>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las cajas registradas.
     * @return Lista de cajas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Caja>> obtenerCajas() {
        try {
            return new ResponseEntity<List<Caja>>(cajaRepository.findAll(), HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<Caja>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Cambia el estado de una caja (activo/inactivo) según el ID proporcionado en el mapa de datos.
     * @param objetoMap Mapa de datos que contiene el ID de la caja y el nuevo estado.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> cambiarEstado(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id") && objetoMap.containsKey("estado")){
                Optional<Caja> cajaOptional= cajaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!cajaOptional.isEmpty()){
                    Caja caja= cajaOptional.get();
                    if(objetoMap.get("estado").equalsIgnoreCase("false")){
                        caja.setVisibilidad(false);
                    }else{
                        caja.setVisibilidad(true);
                    }

                    cajaRepository.save(caja);

                    return Utils.getResponseEntity("El estado de la caja ha sido cambiado.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("La caja no existe.",HttpStatus.BAD_REQUEST);

            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Agrega una nueva caja utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información de la nueva caja.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> agregar(Map<String, String> objetoMap) {
        try {

            if(validarCajaMap(objetoMap,false)){
                if(!cajaExistente(objetoMap)){
                    cajaRepository.save(obtenerCajaDesdeMap(objetoMap,false));
                    return Utils.getResponseEntity("Caja guardada exitosamente.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("Esta caja ya existe.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Caja obtenerCajaDesdeMap(Map<String, String> objetoMap, boolean esAgregado) {
        Caja caja= new Caja();
        boolean disponibidad=true;
        boolean estado=true;

        if(esAgregado){
            Optional<Caja>cajaOptional=cajaRepository.findById(Integer.parseInt(objetoMap.get("id")));

            System.out.println("Es el nombre de la caja. "+cajaOptional.get().getNombre());
            caja.setId(Integer.parseInt(objetoMap.get("id")));
            cajaOptional.ifPresent(value -> caja.setVisibilidad(value.getVisibilidad()));

        }else {
            caja.setVisibilidad(true);
        }

        caja.setNombre(objetoMap.get("nombre"));
        caja.setDescripcion(objetoMap.get("descripcion"));
        return caja;
    }

    private boolean cajaExistente(Map<String, String> objetoMap) {
        return cajaRepository.existsCajaByNombreLikeIgnoreCase(objetoMap.get("nombre"));
    }
//Se valida que el json contenga las llaves
    private boolean validarCajaMap(Map<String, String> objetoMap, boolean validarId) {
        if(objetoMap.containsKey("nombre")){
            if(objetoMap.containsKey("id") && validarId){
                return true;
            } else if (!validarId) {
                return true;
            }
        }
        return false;
    }
    /**
     * Actualiza los datos de una caja existente utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información actualizada de la caja.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> actualizar(Map<String, String> objetoMap) {
        try {
            if(validarCajaMap(objetoMap,true)){
                Optional<Caja> optional=cajaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!optional.isEmpty()){
                    if(optional.get().getNombre().equalsIgnoreCase(objetoMap.get("nombre"))){
                        cajaRepository.save(obtenerCajaDesdeMap(objetoMap,true));
                        return Utils.getResponseEntity("Caja actualizada",HttpStatus.OK);
                    }else{
                        if(!cajaExistente(objetoMap)){
                            cajaRepository.save(obtenerCajaDesdeMap(objetoMap,true));
                            return Utils.getResponseEntity("Caja actualizada",HttpStatus.OK);
                        }
                        return Utils.getResponseEntity("No puedes asignarle este nombre.",HttpStatus.BAD_REQUEST);
                    }

                }
                return Utils.getResponseEntity("No existe la caja.",HttpStatus.BAD_REQUEST);
            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene los datos de una caja específica según su ID.
     * @param id ID de la caja.
     * @return Datos de la caja solicitada con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<Caja> obtenerCajaId(Integer id) {
        try {
            Optional<Caja> optionalCaja=cajaRepository.findById(id);

            if(optionalCaja.isPresent()){
                Caja caja= optionalCaja.get();
                return new ResponseEntity<>(caja,HttpStatus.OK);
            }
            return new ResponseEntity<>(new Caja(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new Caja(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
