package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.AlmacenDTO;
import com.residencia.restaurante.proyecto.dto.CategoriaDTO;
import com.residencia.restaurante.proyecto.entity.Almacen;
import com.residencia.restaurante.proyecto.entity.Categoria;
import com.residencia.restaurante.proyecto.entity.Cocina;
import com.residencia.restaurante.proyecto.repository.ICategoriaRepository;
import com.residencia.restaurante.proyecto.service.ICategoriaService;
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
public class CategoriaServiceImpl implements ICategoriaService {
    @Autowired
    ICategoriaRepository categoriaRepository;
    /**
     * Obtiene una lista de todas las categorías activas.
     * @return Lista de categorías activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasActivas() {
        try {
            List<CategoriaDTO> categoriaConEstado = new ArrayList<>();
            for (Categoria categoria : categoriaRepository.getAllByVisibilidadTrue()) {
                CategoriaDTO categoriaDTO= new CategoriaDTO();
                categoriaDTO.setCategoria(categoria);
                categoriaDTO.setEstado("Visible");

                categoriaConEstado.add(categoriaDTO);
            }
            return new ResponseEntity<List<CategoriaDTO>>(categoriaConEstado, HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<CategoriaDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las categorías no activas.
     * @return Lista de categorías no activas con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasNoActivas() {
        try {
            List<CategoriaDTO> categoriaConEstado = new ArrayList<>();
            for (Categoria categoria : categoriaRepository.getAllByVisibilidadFalse()) {
                CategoriaDTO categoriaDTO= new CategoriaDTO();
                categoriaDTO.setCategoria(categoria);

                    categoriaDTO.setEstado("No visible");


                categoriaConEstado.add(categoriaDTO);
            }
            return new ResponseEntity<List<CategoriaDTO>>(categoriaConEstado, HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<CategoriaDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todas las categorías registradas.
     * @return Lista de categorías con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<CategoriaDTO>> obtenerCategorias() {
        try {
            List<CategoriaDTO> categoriaConEstado = new ArrayList<>();
            for (Categoria categoria : categoriaRepository.findAll()) {
                CategoriaDTO categoriaDTO= new CategoriaDTO();
                categoriaDTO.setCategoria(categoria);
                if(categoria.isVisibilidad()){
                    categoriaDTO.setEstado("Visible");
                }else{
                    categoriaDTO.setEstado("No visible");
                }

                categoriaConEstado.add(categoriaDTO);
            }
            return new ResponseEntity<List<CategoriaDTO>>(categoriaConEstado, HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<List<CategoriaDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Cambia el estado de una categoría (activo/inactivo) según el ID proporcionado en el mapa de datos.
     * @param objetoMap Mapa de datos que contiene el ID de la categoría y el nuevo estado.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> cambiarEstado(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id") && objetoMap.containsKey("visibilidad")){
                Optional<Categoria> categoriaOptional= categoriaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!categoriaOptional.isEmpty()){
                    Categoria categoria= categoriaOptional.get();
                    if(objetoMap.get("visibilidad").equalsIgnoreCase("false")){
                        categoria.setVisibilidad(false);
                    }else{
                        categoria.setVisibilidad(true);
                    }

                    categoriaRepository.save(categoria);

                    return Utils.getResponseEntity("El estado de la categoría ha sido cambiado.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("La categoría no existe.",HttpStatus.BAD_REQUEST);

            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Agrega una nueva categoría utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información de la nueva categoría.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> agregar(Map<String, String> objetoMap) {
        try {

            if(validarCategoriaMap(objetoMap,false)){
                if(!categoriaExistente(objetoMap)){
                    categoriaRepository.save(obtenerCategoriaDesdeMap(objetoMap,false));
                    return Utils.getResponseEntity("Categoría guardada exitosamente.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("Esta categoría ya existe.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Actualiza los datos de una categoría existente utilizando los datos proporcionados en el mapa.
     * @param objetoMap Mapa de datos que contiene la información actualizada de la categoría.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> actualizar(Map<String, String> objetoMap) {
        try {
            if(validarCategoriaMap(objetoMap,true)){
                Optional<Categoria> optional=categoriaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!optional.isEmpty()){
                    if(optional.get().getNombre().equalsIgnoreCase(objetoMap.get("nombre"))){
                        categoriaRepository.save(obtenerCategoriaDesdeMap(objetoMap,true));
                        return Utils.getResponseEntity("Categoría actualizada",HttpStatus.OK);
                    }else{
                        if(!categoriaExistente(objetoMap)){
                            categoriaRepository.save(obtenerCategoriaDesdeMap(objetoMap,true));
                            return Utils.getResponseEntity("Categoría actualizada",HttpStatus.OK);
                        }
                        return Utils.getResponseEntity("No puedes asignarle este nombre.",HttpStatus.BAD_REQUEST);
                    }

                }
                return Utils.getResponseEntity("No existe la Categoría.",HttpStatus.BAD_REQUEST);
            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene los datos de una categoría específica según su ID.
     * @param id ID de la categoría.
     * @return Datos de la categoría solicitada con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<Categoria> obtenerCategoriasId(Integer id) {
        try {
            Optional<Categoria> categoriaOptional=categoriaRepository.findById(id);

            if(categoriaOptional.isPresent()){
                Categoria categoria= categoriaOptional.get();
                return new ResponseEntity<>(categoria,HttpStatus.OK);
            }
            return new ResponseEntity<>(new Categoria(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new Categoria(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Categoria obtenerCategoriaDesdeMap(Map<String, String> objetoMap, boolean esAgregado) {
        Categoria categoria= new Categoria();
        boolean disponibidad=true;
        boolean estado=true;

        if(esAgregado){
            Optional<Categoria> categoriaOptional= categoriaRepository.findById(Integer.parseInt(objetoMap.get("id")));

            categoria.setId(Integer.parseInt(objetoMap.get("id")));
            categoriaOptional.ifPresent(value -> categoria.setVisibilidad(value.isVisibilidad()));

        }else {
            categoria.setVisibilidad(true);
        }

        categoria.setNombre(objetoMap.get("nombre"));
        return categoria;
    }
    //Se valida una caja Existente mediante el nombre
    private boolean categoriaExistente(Map<String, String> objetoMap) {
        return categoriaRepository.existsCategoriaByNombreLikeIgnoreCase(objetoMap.get("nombre"));
    }
    //Se valida que el json contenga las llaves
    private boolean validarCategoriaMap(Map<String, String> objetoMap, boolean validarId) {
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
