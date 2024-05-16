package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.CajaDTO;
import com.residencia.restaurante.proyecto.dto.MateriaPrimaDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.IAlmacenRepository;
import com.residencia.restaurante.proyecto.repository.ICategoriaMateriaPrimaRepository;
import com.residencia.restaurante.proyecto.repository.IInventarioRepository;
import com.residencia.restaurante.proyecto.repository.IMateriaPrimaRepository;
import com.residencia.restaurante.proyecto.service.IMateriaPrimaService;
import com.residencia.restaurante.proyecto.wrapper.InventarioWrapper;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.repository.IUsuarioRepository;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MateriaPrimaServiceImpl implements IMateriaPrimaService {
    @Autowired
    IMateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    IAlmacenRepository almacenRepository;
    @Autowired
    ICategoriaMateriaPrimaRepository categoriaMateriaPrimaRepository;
    @Autowired
    IUsuarioRepository usuarioRepository;
    @Autowired
    IInventarioRepository inventarioRepository;
    @Autowired
    private UploadFileService uploadFileService;
    /**
     * Obtiene todas las materias primas activas.
     * @return ResponseEntity<List<MateriaPrima>> Lista de materias primas activas.
     */
    @Override
    public ResponseEntity<List<MateriaPrimaDTO>> obtenerMateriasPrimasActivas() {
        try {
            List<MateriaPrimaDTO> materiaConEstado = new ArrayList<>();
            for (MateriaPrima materiaPrima : materiaPrimaRepository.getAllByVisibilidadTrue()) {
                MateriaPrimaDTO materiaPrimaDTO= new MateriaPrimaDTO();
                materiaPrimaDTO.setMateriaPrimaDTO(materiaPrima);
                materiaPrimaDTO.setEstado("Visible");


                materiaConEstado.add(materiaPrimaDTO);
            }
            return new ResponseEntity<List<MateriaPrimaDTO>>(materiaConEstado, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<MateriaPrimaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene todas las materias primas no activas.
     * @return ResponseEntity<List<MateriaPrima>> Lista de materias primas no activas.
     */
    @Override
    public ResponseEntity<List<MateriaPrimaDTO>> obtenerMateriasPrimasNoActivas() {
        try {
            List<MateriaPrimaDTO> materiaConEstado = new ArrayList<>();
            for (MateriaPrima materiaPrima : materiaPrimaRepository.getAllByVisibilidadFalse()) {
                MateriaPrimaDTO materiaPrimaDTO= new MateriaPrimaDTO();
                materiaPrimaDTO.setMateriaPrimaDTO(materiaPrima);
                materiaPrimaDTO.setEstado("No visible");


                materiaConEstado.add(materiaPrimaDTO);
            }
            return new ResponseEntity<List<MateriaPrimaDTO>>(materiaConEstado, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<MateriaPrimaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene todas las materias primas.
     * @return ResponseEntity<List<MateriaPrima>> Lista de todas las materias primas.
     */
    @Override
    public ResponseEntity<List<MateriaPrimaDTO>> obtenerMateriasPrimas() {
        try {
            List<MateriaPrimaDTO> materiaConEstado = new ArrayList<>();

            for (MateriaPrima materiaPrima : materiaPrimaRepository.findAll()) {
                List<String> almacenes=new ArrayList<>();
                MateriaPrimaDTO materiaPrimaDTO= new MateriaPrimaDTO();
                materiaPrimaDTO.setMateriaPrimaDTO(materiaPrima);
                if(materiaPrima.isVisibilidad()){
                    materiaPrimaDTO.setEstado("Visible");
                }else{
                    materiaPrimaDTO.setEstado("No visible");
                }
                List<Inventario> inventarios= inventarioRepository.getAllByMateriaPrima_Id(materiaPrima.getId());
                if(!inventarios.isEmpty()){
                    for (Inventario inventario:inventarios) {
                        almacenes.add(inventario.getAlmacen().getNombre());
                    }

                }
                materiaPrimaDTO.setAlmacenes(almacenes);

                materiaConEstado.add(materiaPrimaDTO);
            }
            return new ResponseEntity<List<MateriaPrimaDTO>>(materiaConEstado, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<MateriaPrimaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Cambia el estado de visibilidad de una materia prima.
     * @param objetoMap Un mapa de datos con la información de la materia prima y su nuevo estado.
     * @return ResponseEntity<String> Respuesta que indica el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> cambiarEstado(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id") && objetoMap.containsKey("visibilidad")){
                Optional<MateriaPrima> materiaPrimaOptional= materiaPrimaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!materiaPrimaOptional.isEmpty()){
                    MateriaPrima materiaPrima= materiaPrimaOptional.get();
                    if(objetoMap.get("visibilidad").equalsIgnoreCase("false")){
                        materiaPrima.setVisibilidad(false);
                    }else{
                        materiaPrima.setVisibilidad(true);
                    }

                    materiaPrimaRepository.save(materiaPrima);

                    return Utils.getResponseEntity("El estado de la materia prima ha sido cambiada.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("La materia prima no existe.",HttpStatus.BAD_REQUEST);

            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Agrega una nueva materia prima.
     * @param objetoMap Un mapa de datos con la información de la nueva materia prima.
     * @return ResponseEntity<String> Respuesta que indica el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> agregar(Map<String, String> objetoMap) {
        try {

            if(validarMateriaPrimaMap(objetoMap,false)){
                if(!materiaPrimaExistente(objetoMap)){

                    Optional<CategoriaMateriaPrima> optionalCategoriaMateriaPrima= categoriaMateriaPrimaRepository.findById(Integer.parseInt(objetoMap.get("categoriaId")));
                    if(!optionalCategoriaMateriaPrima.isEmpty()){
                        CategoriaMateriaPrima categoriaMateriaPrima=optionalCategoriaMateriaPrima.get();
                        MateriaPrima materiaPrima= new MateriaPrima();

                        materiaPrima.setVisibilidad(true);
                        materiaPrima.setCategoriaMateriaPrima(categoriaMateriaPrima);
                        materiaPrima.setCostoUnitario(Double.parseDouble(objetoMap.get("costo")));
                        materiaPrima.setUnidadMedida(objetoMap.get("unidadMedida"));
                        materiaPrima.setNombre(objetoMap.get("nombre"));


                        materiaPrimaRepository.save(materiaPrima);

                        //Asignar materia prima a los almacenes
                        ObjectMapper objectMapper= new ObjectMapper();
                        try {
                            List<InventarioWrapper> listaInventario=objectMapper.readValue(objetoMap.get("inventario"), new TypeReference<List<InventarioWrapper>>() {});
                            if(!listaInventario.isEmpty()){
                                for(InventarioWrapper inventario: listaInventario){
                                    Inventario inventario1=new Inventario();
                                    inventario1.setMateriaPrima(materiaPrima);
                                    Optional<Almacen> optionalAlmacen= almacenRepository.findById(inventario.getAlmacenId());
                                    if(!optionalAlmacen.isEmpty()){
                                        Almacen almacen=optionalAlmacen.get();
                                        inventario1.setAlmacen(almacen);
                                    }
                                    Optional<Usuario> optionalUsuario=usuarioRepository.findById(Integer.parseInt(objetoMap.get("usuarioId")));
                                    if(!optionalUsuario.isEmpty()){
                                        Usuario usuario=optionalUsuario.get();
                                        inventario1.setUsuario(usuario);
                                    }
                                    inventario1.setStockActual(inventario.getStockActual());
                                    inventario1.setStockMax(inventario.getStockMaximo());
                                    inventario1.setStockMin(inventario.getStockMinimo());

                                    inventarioRepository.save(inventario1);


                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        return Utils.getResponseEntity("Materia prima registrada correctamente.",HttpStatus.OK);



                    }

                    return Utils.getResponseEntity("La categoría ya no existe.",HttpStatus.BAD_REQUEST);


                }
                return Utils.getResponseEntity("Esta materia prima ya existe.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Actualiza una materia prima existente.
     * @param objetoMap Un mapa de datos con la información actualizada de la materia prima.
     * @return ResponseEntity<String> Respuesta que indica el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> actualizar(Map<String, String> objetoMap) {
        try {
            if(validarMateriaPrimaMap(objetoMap,true)){
                Optional<MateriaPrima> optional=materiaPrimaRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(!optional.isEmpty()){
                    if(optional.get().getNombre().equalsIgnoreCase(objetoMap.get("nombre"))){
                        materiaPrimaRepository.save(obtenerMateriaPrimaDesdeMap(objetoMap));
                        return Utils.getResponseEntity("Materia prima actualizada",HttpStatus.OK);
                    }else{
                        if(!materiaPrimaExistente(objetoMap)){
                            materiaPrimaRepository.save(obtenerMateriaPrimaDesdeMap(objetoMap));
                            return Utils.getResponseEntity("Materia prima actualizada",HttpStatus.OK);
                        }
                        return Utils.getResponseEntity("No puedes asignarle este nombre.",HttpStatus.BAD_REQUEST);
                    }

                }
                return Utils.getResponseEntity("No existe la materia prima.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private MateriaPrima obtenerMateriaPrimaDesdeMap(Map<String, String> objetoMap) {
        Optional<MateriaPrima> optionalMateriaPrima=materiaPrimaRepository.findById(Integer.parseInt(objetoMap.get("id")));
        MateriaPrima materiaPrima=optionalMateriaPrima.get();
        materiaPrima.setNombre(objetoMap.get("nombre"));
        materiaPrima.setCostoUnitario(Double.parseDouble(objetoMap.get("costo")));

        if(objetoMap.containsKey("categoriaId")){
            Optional<CategoriaMateriaPrima> optionalCategoriaMateriaPrima=categoriaMateriaPrimaRepository.findById(Integer.parseInt(objetoMap.get("categoriaId")));
            if(!optionalCategoriaMateriaPrima.isEmpty()){
                CategoriaMateriaPrima categoriaMateriaPrima=optionalCategoriaMateriaPrima.get();
                materiaPrima.setCategoriaMateriaPrima(categoriaMateriaPrima);
            }
        }

        return materiaPrima;
    }
    /**
     * Obtiene los datos de una materia prima específica.
     * @param id El ID de la materia prima.
     * @return ResponseEntity<MateriaPrima> Materia prima solicitada.
     */
    @Override
    public ResponseEntity<MateriaPrima> obtenerMateriaPrimaId(Integer id) {
        try {
            Optional<MateriaPrima> materiaPrimaOptional=materiaPrimaRepository.findById(id);

            if(materiaPrimaOptional.isPresent()){
                MateriaPrima materiaPrima= materiaPrimaOptional.get();
                return new ResponseEntity<>(materiaPrima,HttpStatus.OK);
            }
            return new ResponseEntity<>(new MateriaPrima(),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<MateriaPrima>(new MateriaPrima(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregarMateria(String nombre, int idCategoria, int idUsuario, String unidadMedida, double costoUnitario, String inventario, MultipartFile file) {
        try{
            if(!materiaPrimaExistente1(nombre)){
                Optional<CategoriaMateriaPrima> optionalCategoriaMateriaPrima= categoriaMateriaPrimaRepository.findById(idCategoria);
                if(!optionalCategoriaMateriaPrima.isEmpty()){
                    CategoriaMateriaPrima categoriaMateriaPrima=optionalCategoriaMateriaPrima.get();
                    MateriaPrima materiaPrima= new MateriaPrima();

                    materiaPrima.setVisibilidad(true);
                    materiaPrima.setCategoriaMateriaPrima(categoriaMateriaPrima);
                    materiaPrima.setCostoUnitario(costoUnitario);
                    materiaPrima.setUnidadMedida(unidadMedida);
                    materiaPrima.setNombre(nombre);

                    String nombreImagen=uploadFileService.guardarImagen(file);
                    materiaPrima.setImagen(nombreImagen);

                    materiaPrimaRepository.save(materiaPrima);

                    //Asignar materia prima a los almacenes
                    ObjectMapper objectMapper= new ObjectMapper();
                    try {
                        List<InventarioWrapper> listaInventario=objectMapper.readValue(inventario, new TypeReference<List<InventarioWrapper>>() {});
                        if(!listaInventario.isEmpty()){
                            for(InventarioWrapper inventarioWrapper: listaInventario){
                                Inventario inventario1=new Inventario();
                                inventario1.setMateriaPrima(materiaPrima);
                                System.out.println(inventarioWrapper.getAlmacenId()+"id almacen");
                                Optional<Almacen> optionalAlmacen= almacenRepository.findById(inventarioWrapper.getAlmacenId());
                                if(!optionalAlmacen.isEmpty()){
                                    Almacen almacen=optionalAlmacen.get();
                                    inventario1.setAlmacen(almacen);
                                }
                                Optional<Usuario> optionalUsuario=usuarioRepository.findById(idUsuario);
                                if(!optionalUsuario.isEmpty()){
                                    Usuario usuario=optionalUsuario.get();
                                    inventario1.setUsuario(usuario);
                                }
                                inventario1.setStockActual(inventarioWrapper.getStockActual());
                                inventario1.setStockMax(inventarioWrapper.getStockMaximo());
                                inventario1.setStockMin(inventarioWrapper.getStockMinimo());
                                System.out.println("Listo");

                                inventarioRepository.save(inventario1);


                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    return Utils.getResponseEntity("Materia prima guardada correctamente.",HttpStatus.OK);


                }

                return Utils.getResponseEntity("La categoría no existe.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity("La materia prima ya existe.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> actualizarMateria(int id, String nombre, int idCategoria, double costoUnitario, MultipartFile file) {
        try {

            Optional<MateriaPrima> optional= materiaPrimaRepository.findById(id);
            if (optional.isPresent()){
                if(optional.get().getNombre().equalsIgnoreCase(nombre)){

                    MateriaPrima materiaPrima=optional.get();
                    materiaPrima.setNombre(nombre);
                    materiaPrima.setCostoUnitario(costoUnitario);

                    Optional<CategoriaMateriaPrima> optionalCategoriaMateriaPrima=categoriaMateriaPrimaRepository.findById(idCategoria);
                        if(!optionalCategoriaMateriaPrima.isEmpty()){
                            CategoriaMateriaPrima categoriaMateriaPrima=optionalCategoriaMateriaPrima.get();
                            materiaPrima.setCategoriaMateriaPrima(categoriaMateriaPrima);
                        }
                    if(file.isEmpty()){
                        materiaPrima.setImagen(materiaPrima.getImagen());
                    }else{
                        if(!materiaPrima.getImagen().equalsIgnoreCase("default.jpg")){
                            uploadFileService.eliminarImagen(materiaPrima.getImagen());
                        }
                        String nombreImagen=uploadFileService.guardarImagen(file);
                        materiaPrima.setImagen(nombreImagen);
                    }

                    materiaPrimaRepository.save(materiaPrima);
                    return Utils.getResponseEntity("Materia prima actualizada.",HttpStatus.OK);



                }else{
                    if(!materiaPrimaExistente1(nombre)){
                        MateriaPrima materiaPrima=optional.get();
                        materiaPrima.setNombre(nombre);
                        materiaPrima.setCostoUnitario(costoUnitario);

                        Optional<CategoriaMateriaPrima> optionalCategoriaMateriaPrima=categoriaMateriaPrimaRepository.findById(idCategoria);
                        if(!optionalCategoriaMateriaPrima.isEmpty()){
                            CategoriaMateriaPrima categoriaMateriaPrima=optionalCategoriaMateriaPrima.get();
                            materiaPrima.setCategoriaMateriaPrima(categoriaMateriaPrima);
                        }
                        if(file.isEmpty()){
                            materiaPrima.setImagen(materiaPrima.getImagen());
                        }else{
                            if(!materiaPrima.getImagen().equalsIgnoreCase("default.jpg")){
                                uploadFileService.eliminarImagen(materiaPrima.getImagen());
                            }
                            String nombreImagen=uploadFileService.guardarImagen(file);
                            materiaPrima.setImagen(nombreImagen);
                        }
                        materiaPrimaRepository.save(materiaPrima);
                        return Utils.getResponseEntity("Materia prima actualizada.",HttpStatus.OK);



                    }
                    return Utils.getResponseEntity("No puedes asignarle este nombre.",HttpStatus.BAD_REQUEST);
                }
            }
            return Utils.getResponseEntity("No existe la materia prima.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<MateriaPrimaDTO>> obtenerMateriasPrimasIdCategoria(Integer id) {
        try {
            List<MateriaPrimaDTO> materiaConEstado = new ArrayList<>();
            for (MateriaPrima materiaPrima : materiaPrimaRepository.getAllByCategoriaMateriaPrima_Id(id)) {
                MateriaPrimaDTO materiaPrimaDTO= new MateriaPrimaDTO();
                materiaPrimaDTO.setMateriaPrimaDTO(materiaPrima);
                if(materiaPrima.isVisibilidad()){
                    materiaPrimaDTO.setEstado("Visible");
                }else{
                    materiaPrimaDTO.setEstado("No visible");
                }

                materiaConEstado.add(materiaPrimaDTO);
            }
            return new ResponseEntity<List<MateriaPrimaDTO>>(materiaConEstado, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<MateriaPrimaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Integer> calcularTotalMateriasPrimas() {
        try {
            return new ResponseEntity<Integer>((int) materiaPrimaRepository.count(),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Integer>(0,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean materiaPrimaExistente1(String nombre) {
        return materiaPrimaRepository.existsMateriaPrimaByNombreLikeIgnoreCase(nombre);
    }

    //Se valida una caja Existente mediante el nombre
    private boolean materiaPrimaExistente(Map<String, String> objetoMap) {
        return materiaPrimaRepository.existsMateriaPrimaByNombreLikeIgnoreCase(objetoMap.get("nombre"));
    }
    //Se valida que el json contenga las llaves
    private boolean validarMateriaPrimaMap(Map<String, String> objetoMap, boolean validarId) {
        if(objetoMap.containsKey("nombre") && objetoMap.containsKey("unidadMedida") && objetoMap.containsKey("categoriaId")){
            if(objetoMap.containsKey("id") && validarId){
                return true;
            } else if (!validarId) {
                return true;
            }
        }
        return false;
    }
}
