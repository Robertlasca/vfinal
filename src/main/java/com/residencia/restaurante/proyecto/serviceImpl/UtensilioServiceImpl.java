package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.AlmacenDTO;
import com.residencia.restaurante.proyecto.dto.CocinaUtensilioDTO;
import com.residencia.restaurante.proyecto.dto.EstacionDTO;
import com.residencia.restaurante.proyecto.dto.UtensilioDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.ICocinaRepository;
import com.residencia.restaurante.proyecto.repository.ICocina_UtensilioRepository;
import com.residencia.restaurante.proyecto.repository.IUtensilioRepository;
import com.residencia.restaurante.proyecto.service.IUtensilioService;
import com.residencia.restaurante.proyecto.wrapper.Cocina_UtensilioWrapper;
import com.residencia.restaurante.proyecto.wrapper.InventarioWrapper;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.utils.Utils;
import jdk.jshell.execution.Util;
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
public class UtensilioServiceImpl implements IUtensilioService {

    @Autowired
    IUtensilioRepository utensilioRepository;

    @Autowired
    ICocinaRepository cocinaRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private ICocina_UtensilioRepository cocinaUtensilioRepository;




    @Override
    public ResponseEntity<UtensilioDTO> obtenerUtensilioId(Integer id) {
        try {

            Optional<Utensilio> utensilioOptional=utensilioRepository.findById(id);

            if(utensilioOptional.isPresent()){
                Utensilio utensilio= utensilioOptional.get();
                List<CocinaUtensilioDTO> cocinaUtensilioDTOS=new ArrayList<>();
                UtensilioDTO utensilioDTO=new UtensilioDTO();
                utensilioDTO.setId(utensilio.getId());
                utensilioDTO.setNombre(utensilio.getNombre());
                utensilioDTO.setDescripcion(utensilio.getDescripcion());
                utensilioDTO.setImagen(utensilioDTO.getImagen());
                List<Cocina_Utensilio> cocinaUtensilios=cocinaUtensilioRepository.findAllByUtensilio_Id(utensilio.getId());
                if(!cocinaUtensilios.isEmpty()){
                    for (Cocina_Utensilio cocinaU:cocinaUtensilios
                         ) {
                        CocinaUtensilioDTO cocinaUtensilioDTO= new CocinaUtensilioDTO();
                        cocinaUtensilioDTO.setId(cocinaU.getId());
                        cocinaUtensilioDTO.setCantidad(cocinaU.getCantidad());
                        cocinaUtensilioDTO.setNombreCocina(cocinaU.getCocina().getNombre());
                        cocinaUtensilioDTOS.add(cocinaUtensilioDTO);
                    }
                    utensilioDTO.setCocinaUtensilioDTOS(cocinaUtensilioDTOS);
                }
                return new ResponseEntity<UtensilioDTO>(utensilioDTO,HttpStatus.OK);
            }
            return new ResponseEntity<UtensilioDTO>(new UtensilioDTO(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<UtensilioDTO>(new UtensilioDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UtensilioDTO>> obtenerUtensilios() {
        try {
            List<Utensilio> list=utensilioRepository.findAll();
            List<UtensilioDTO> utensilioDTOS=new ArrayList<>();
            for (Utensilio utensilio:list
                 ) {
                UtensilioDTO utensilioDTO= new UtensilioDTO();
                utensilioDTO.setDescripcion(utensilio.getDescripcion());
                utensilioDTO.setId(utensilio.getId());
                utensilioDTO.setNombre(utensilio.getNombre());
                utensilioDTO.setImagen(utensilio.getImagen());
                List<CocinaUtensilioDTO> cocinaUtensilioDTOS=new ArrayList<>();

                List<Cocina_Utensilio> cocinaUtensilios=cocinaUtensilioRepository.findAllByUtensilio_Id(utensilio.getId());
                if(!cocinaUtensilios.isEmpty()){

                    for (Cocina_Utensilio cocinaU:cocinaUtensilios
                    ) {
                        CocinaUtensilioDTO cocinaUtensilioDTO= new CocinaUtensilioDTO();
                        cocinaUtensilioDTO.setId(cocinaU.getId());
                        cocinaUtensilioDTO.setCantidad(cocinaU.getCantidad());
                        cocinaUtensilioDTO.setNombreCocina(cocinaU.getCocina().getNombre());
                        cocinaUtensilioDTOS.add(cocinaUtensilioDTO);
                    }
                    utensilioDTO.setCocinaUtensilioDTOS(cocinaUtensilioDTOS);
                }

                utensilioDTOS.add(utensilioDTO);

            }
            return new ResponseEntity<List<UtensilioDTO>>(utensilioDTOS,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<UtensilioDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> eliminar(Integer id) {
        try {
            Optional<Utensilio> utensilioOptional= utensilioRepository.findById(id);
            if(!utensilioOptional.isEmpty()){
                Utensilio utensilio= utensilioOptional.get();
                uploadFileService.eliminarImagen(utensilio.getImagen());                utensilioRepository.delete(utensilio);
                return Utils.getResponseEntity("Utensilio eliminado correctamente.",HttpStatus.OK);
            }
            return Utils.getResponseEntity("Utensilio no existe.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Cocina_Utensilio>> obtenerUtensiliosXCocina(Integer id) {
        try {

                return new ResponseEntity<List<Cocina_Utensilio>>(cocinaUtensilioRepository.findAllByCocina_Id(id),HttpStatus.OK);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Cocina_Utensilio>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregarUtensilio(String nombre, String descripcion,String inventario, MultipartFile file) {
        try {
            //Se guarda nombre descripcion e imagen
            Utensilio utensilio= new Utensilio();
            utensilio.setNombre(nombre);
            utensilio.setDescripcion(descripcion);
            if(file==null || file.isEmpty()){
                utensilio.setImagen("default.jpg");

            }else{
                String nombreImagen= uploadFileService.guardarImagen(file);
                utensilio.setImagen(nombreImagen);

            }

            utensilioRepository.save(utensilio);

            ObjectMapper objectMapper=new ObjectMapper();

            try {
                List<Cocina_UtensilioWrapper> cocinaUtensilioWrappers= objectMapper.readValue(inventario, new TypeReference<List<Cocina_UtensilioWrapper>>() {
                });
                if(!cocinaUtensilioWrappers.isEmpty()){
                    for(Cocina_UtensilioWrapper cocinaUtensilioWrapper:cocinaUtensilioWrappers){
                        Cocina_Utensilio cocinaUtensilio= new Cocina_Utensilio();
                        cocinaUtensilio.setCantidad(cocinaUtensilioWrapper.getCantidad());
                        cocinaUtensilio.setUtensilio(utensilio);

                        Optional<Cocina> cocinaOptional= cocinaRepository.findById(cocinaUtensilioWrapper.getIdCocina());
                        if(cocinaOptional.isPresent()){
                            Cocina cocina= cocinaOptional.get();
                            cocinaUtensilio.setCocina(cocina);
                        }
                        cocinaUtensilioRepository.save(cocinaUtensilio);
                        System.out.println("si llegue hasta aqui");
                    }
                }

            }catch (Exception e){
                e.printStackTrace();

            }
            return Utils.getResponseEntity("Utensilio guardado correctamente.",HttpStatus.OK);


        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> actualizarUtensilio(Integer id, String nombre, String descripcion, MultipartFile file) {
        try {

            Optional<Utensilio> utensilioOptional=utensilioRepository.findById(id);
            if(utensilioOptional.isPresent()){


                    Utensilio utensilio=utensilioOptional.get();
                    utensilio.setNombre(nombre);
                    utensilio.setDescripcion(descripcion);


                    if(file==null || file.isEmpty()){
                        utensilio.setImagen(utensilio.getImagen());
                    }else {
                        if(!utensilio.getImagen().equalsIgnoreCase("default.jpg")){
                            uploadFileService.eliminarImagen(utensilio.getImagen());
                        }
                        String nombreImagen=uploadFileService.guardarImagen(file);
                        utensilio.setImagen(nombreImagen);
                    }
                    utensilioRepository.save(utensilio);
                    return Utils.getResponseEntity("Utensilio actualizado.",HttpStatus.OK);

            }

            return Utils.getResponseEntity("No existe el utensilio.",HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> eliminarInventario(Integer id) {
        try{
            Optional<Cocina_Utensilio> cocinaUtensilio=cocinaUtensilioRepository.findById(id);
            if(cocinaUtensilio.isPresent()){
                Cocina_Utensilio cocinaUtensilio1=cocinaUtensilio.get();
                cocinaUtensilioRepository.delete(cocinaUtensilio1);
                return Utils.getResponseEntity("EL utensilio ya no pertenece al almacén.",HttpStatus.OK);
            }
            return Utils.getResponseEntity("Ya no existe esta relación.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> editarStock(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id")&& objetoMap.containsKey("cantidad")){
                Optional<Cocina_Utensilio> optionalInventario=cocinaUtensilioRepository.findById(Integer.parseInt(objetoMap.get("id")));
                if(optionalInventario.isPresent()){
                    double cantidad=Double.parseDouble(objetoMap.get("cantidad"));
                    Cocina_Utensilio cocinaUtensilio=optionalInventario.get();
                    cocinaUtensilio.setCantidad((int) cantidad);
                    cocinaUtensilioRepository.save(cocinaUtensilio);

                        return Utils.getResponseEntity("Stock actualizado.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("El inventario no existe.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<EstacionDTO>> listarAlmacenesPorIdUtensilio(Integer id) {
        try {
            List<Cocina> almacens= cocinaUtensilioRepository.findEstacionNotRelatedToUtensilio(id);
            List<EstacionDTO> almacenDTOS= new ArrayList<>();
            for (Cocina almacen:almacens) {
                EstacionDTO estacionDTO= new EstacionDTO();
                estacionDTO.setCocina(almacen);

                almacenDTOS.add(estacionDTO);
            }
            return new ResponseEntity<List<EstacionDTO>>(almacenDTOS,HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<EstacionDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregarInventario(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("idUtensilio") && objetoMap.containsKey("inventario")){
                Optional<Utensilio> optionalMateriaPrima=utensilioRepository.findById(Integer.parseInt(objetoMap.get("idUtensilio")));
                if(!optionalMateriaPrima.isEmpty()){
                    Utensilio materiaPrima= optionalMateriaPrima.get();

                    ObjectMapper objectMapper= new ObjectMapper();
                    try {
                        List<Cocina_UtensilioWrapper> listaInventario=objectMapper.readValue(objetoMap.get("inventario"), new TypeReference<List<Cocina_UtensilioWrapper>>() {});
                        if(!listaInventario.isEmpty()){
                            for(Cocina_UtensilioWrapper inventarioWrapper: listaInventario){
                                Cocina_Utensilio cocinaUtensilio=new Cocina_Utensilio();
                                cocinaUtensilio.setCantidad(inventarioWrapper.getCantidad());

                               cocinaUtensilio.setUtensilio(materiaPrima);

                                Optional<Cocina> optionalAlmacen= cocinaRepository.findById(inventarioWrapper.getIdCocina());
                                if(!optionalAlmacen.isEmpty()){
                                    Cocina almacen=optionalAlmacen.get();
                                    cocinaUtensilio.setCocina(almacen);
                                }
                                cocinaUtensilio.setCantidad(inventarioWrapper.getCantidad());
                                cocinaUtensilioRepository.save(cocinaUtensilio);


                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    return Utils.getResponseEntity("Inventario agregado.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("No existe el utensilio.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validarCocina(int idCocina) {
        Optional<Cocina> optional=cocinaRepository.findById(idCocina);
        return optional.isPresent();
    }

    private Utensilio obtenerUtensilioDesdeMap(Map<String, String> objetoMap, boolean esAgregado) {
        Utensilio utensilio= new Utensilio();


        if(esAgregado){
            utensilio.setId(Integer.parseInt(objetoMap.get("id")));
            Optional<Utensilio> utensilioOptional=utensilioRepository.findById(Integer.parseInt(objetoMap.get("id")));
            if(!utensilioOptional.isEmpty()){
                //utensilio.setCocina(utensilioOptional.get().getCocina());
            }


        }else{
            Optional<Cocina> cocinaOptional=cocinaRepository.findById(Integer.parseInt(objetoMap.get("idCocina")));
            if(!cocinaOptional.isEmpty()){
                Cocina cocina=cocinaOptional.get();
                //utensilio.setCocina(cocina);

            }

        }

        utensilio.setNombre(objetoMap.get("nombre"));
        //utensilio.setCantidad(Integer.parseInt(objetoMap.get("cantidad")));
        utensilio.setDescripcion(objetoMap.get("descripcion"));

        return utensilio;
    }

    //Se valida que el json contenga las llaves
    private boolean validarUtensilioMap(Map<String, String> objetoMap, boolean validarId) {
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
