package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.ComandaDTO;
import com.residencia.restaurante.proyecto.dto.DatosComandaDTO;
import com.residencia.restaurante.proyecto.dto.DatosOrdenesDTO;
import com.residencia.restaurante.proyecto.dto.DetalleOrdenProductoDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IReceptorService;
import com.residencia.restaurante.security.utils.TicketOrden;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReceptorServiceImpl implements IReceptorService {
    @Autowired
    private IOrdenRepository ordenRepository;
    @Autowired
    private IDetalleOrden_MenuRepository detalleOrdenMenuRepository;
    @Autowired
    private IDetalleOrden_ProductoNormalRepository detalleOrdenProductoNormalRepository;
    @Autowired
    private ICocinaRepository cocinaRepository;
    @Autowired
    private IProductoTerminadoRepository productoTerminadoRepository;
    @Autowired
    private IInventarioRepository inventarioRepository;


@Autowired
private IMateriaPrima_MenuRepository materiaPrimaMenuRepository;

@Autowired
private IProductoTerminado_MenuRepository productoTerminadoMenuRepository;
    @Override
    public ResponseEntity<List<DatosComandaDTO>> obtenerComandasPorIdCocina(Integer id) {
        try {
            List<DetalleOrdenProductoDTO> comandaDTOList= new ArrayList<>();
            List<DatosComandaDTO> datosComandaDTOS=new ArrayList<>();

            Optional<Cocina> optionalCocina= cocinaRepository.findById(id);
            if(optionalCocina.isPresent()){
                Cocina cocina= optionalCocina.get();
                List<DetalleOrdenMenu> detalleOrdenMenuList= detalleOrdenMenuRepository.getAllByEstadoEqualsIgnoreCase("En espera");
                if(!detalleOrdenMenuList.isEmpty()){
                    // Set para almacenar los nombres únicos de las mesas y áreas de servicio

                    Set<String> areasYaProcesadas = new HashSet<>();
                    for (DetalleOrdenMenu detalleOrdenMenu:detalleOrdenMenuList) {
                        String areaServicio = detalleOrdenMenu.getOrden().getMesa().getAreaServicio().getNombre()+detalleOrdenMenu.getOrden().getMesa().getNombre();

                        // Comprobar si la mesa ya ha sido procesada
                        if (!areasYaProcesadas.contains(areaServicio)) {

                            DatosComandaDTO datosComandaDTO = new DatosComandaDTO();
                            datosComandaDTO.setNombreCliente(detalleOrdenMenu.getOrden().getNombreCliente());
                            datosComandaDTO.setAreaServicio(detalleOrdenMenu.getOrden().getMesa().getAreaServicio().getNombre());
                            datosComandaDTO.setNombreMesa(detalleOrdenMenu.getOrden().getMesa().getNombre());
                            datosComandaDTOS.add(datosComandaDTO);

                            // Marcar la mesa y el área como procesadas

                            areasYaProcesadas.add(areaServicio);
                        }

                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();

                        if(detalleOrdenMenu.getMenu().getCocina().getId()==cocina.getId()){

                            detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                            detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                            detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                            detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                            detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                            detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                            detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                            detalleOrdenProductoDTO.setEsDetalleMenu("true");

                            comandaDTOList.add(detalleOrdenProductoDTO);
                        }
                        for (int x=0;x<datosComandaDTOS.size();x++) {
                            if( datosComandaDTOS.get(x).getNombreMesa().equalsIgnoreCase(detalleOrdenMenu.getOrden().getMesa().getNombre()) && datosComandaDTOS.get(x).getAreaServicio().equalsIgnoreCase(detalleOrdenMenu.getOrden().getMesa().getAreaServicio().getNombre())){
                                datosComandaDTOS.get(x).setDetalleOrdenProductoDTOS(comandaDTOList);
                            }
                        }
                    }
                    return new ResponseEntity<List<DatosComandaDTO>>(datosComandaDTOS,HttpStatus.OK);
                }


            }
            return new ResponseEntity<List<DatosComandaDTO>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<DatosComandaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<DetalleOrdenProductoDTO>> obtenerComandasEnPreparacionPorIdCocina(Integer id) {
        try {
            List<DetalleOrdenProductoDTO> comandaDTOList= new ArrayList<>();

            Optional<Cocina> optionalCocina= cocinaRepository.findById(id);
            if(optionalCocina.isPresent()){
                Cocina cocina= optionalCocina.get();
                List<DetalleOrdenMenu> detalleOrdenMenuList= detalleOrdenMenuRepository.getAllByEstadoEqualsIgnoreCase("En preparación");
                if(!detalleOrdenMenuList.isEmpty()){
                    for (DetalleOrdenMenu detalleOrdenMenu:detalleOrdenMenuList) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        if(detalleOrdenMenu.getMenu().getCocina().getId()==cocina.getId()){
                            detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                            detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                            detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                            detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                            detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                            detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                            detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                            detalleOrdenProductoDTO.setEsDetalleMenu("true");
                            comandaDTOList.add(detalleOrdenProductoDTO);

                        }
                    }
                    return new ResponseEntity<List<DetalleOrdenProductoDTO>>(comandaDTOList,HttpStatus.OK);
                }


            }
            return new ResponseEntity<List<DetalleOrdenProductoDTO>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<DetalleOrdenProductoDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<DetalleOrdenProductoDTO>> obtenerComandasTerminadasPorIdCocina(Integer id) {
        try {
            List<DetalleOrdenProductoDTO> comandaDTOList= new ArrayList<>();

            Optional<Cocina> optionalCocina= cocinaRepository.findById(id);
            if(optionalCocina.isPresent()){
                Cocina cocina= optionalCocina.get();
                List<DetalleOrdenMenu> detalleOrdenMenuList= detalleOrdenMenuRepository.getAllByEstadoEqualsIgnoreCase("Terminado");
                LocalDate localDate= LocalDate.now();
                if(!detalleOrdenMenuList.isEmpty()){
                    for (DetalleOrdenMenu detalleOrdenMenu:detalleOrdenMenuList) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        if(detalleOrdenMenu.getMenu().getCocina().getId()==cocina.getId() && detalleOrdenMenu.getOrden().getFechaHoraApertura().toLocalDate().equals(localDate)){
                            detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                            detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                            detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                            detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                            detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                            detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                            detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                            detalleOrdenProductoDTO.setEsDetalleMenu("true");
                            comandaDTOList.add(detalleOrdenProductoDTO);

                        }
                    }
                    return new ResponseEntity<List<DetalleOrdenProductoDTO>>(comandaDTOList,HttpStatus.OK);
                }


            }
            return new ResponseEntity<List<DetalleOrdenProductoDTO>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<DetalleOrdenProductoDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cambiarPlatilloPreparacion(Integer id) {
        try {
            Optional<DetalleOrdenMenu> detalleOrdenMenuOptional= detalleOrdenMenuRepository.findById(id);
            if(detalleOrdenMenuOptional.isPresent()){
                DetalleOrdenMenu detalleOrdenMenu= detalleOrdenMenuOptional.get();
                detalleOrdenMenu.setEstado("En preparación");
                detalleOrdenMenuRepository.save(detalleOrdenMenu);
                return Utils.getResponseEntity("Estado del platillo cambiado correctamente.",HttpStatus.OK);
            }
            return Utils.getResponseEntity("Platillo no encontrado.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> cambiarPlatilloTerminado(Integer id) {
        try {
            try {
                Optional<DetalleOrdenMenu> detalleOrdenMenuOptional= detalleOrdenMenuRepository.findById(id);
                if(detalleOrdenMenuOptional.isPresent()){
                    DetalleOrdenMenu detalleOrdenMenu= detalleOrdenMenuOptional.get();
                    detalleOrdenMenu.setEstado("Terminado");
                    detalleOrdenMenuRepository.save(detalleOrdenMenu);
                    return Utils.getResponseEntity("Estado del platillo cambiado correctamente.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("Platillo no encontrado.",HttpStatus.BAD_REQUEST);

            }catch (Exception e){
                e.printStackTrace();
            }
            return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cambiarPlatilloCancelado(Integer id) {
        try {
            try {
                Optional<DetalleOrdenMenu> detalleOrdenMenuOptional= detalleOrdenMenuRepository.findById(id);
                if(detalleOrdenMenuOptional.isPresent()){
                    DetalleOrdenMenu detalleOrdenMenu= detalleOrdenMenuOptional.get();
                   List<ProductoTerminado_Menu> productoTerminadoMenus= productoTerminadoMenuRepository.getAllByMenu(detalleOrdenMenu.getMenu());
                   List<MateriaPrima_Menu> materiaPrimaMenus=materiaPrimaMenuRepository.getAllByMenu(detalleOrdenMenu.getMenu());

                   if(!productoTerminadoMenus.isEmpty()){
                       for (ProductoTerminado_Menu productoTerminadoMenu:productoTerminadoMenus){
                           double cantidadRegresada= detalleOrdenMenu.getCantidad()*productoTerminadoMenu.getCantidad();
                           ProductoTerminado productoTerminado= productoTerminadoMenu.getProductoTerminado();
                           productoTerminado.setStockActual(productoTerminado.getStockActual()+cantidadRegresada);
                           productoTerminadoRepository.save(productoTerminado);
                       }
                   }

                   if(!materiaPrimaMenus.isEmpty()){
                       for (MateriaPrima_Menu materiaPrimaMenu:materiaPrimaMenus) {
                           double cantidadRegresada= detalleOrdenMenu.getCantidad()*materiaPrimaMenu.getCantidad();
                           Inventario inventario= materiaPrimaMenu.getInventario();
                           inventario.setStockActual(inventario.getStockActual()+cantidadRegresada);
                           inventarioRepository.save(inventario);
                       }
                   }

                    detalleOrdenMenu.setEstado("Cancelado");
                    detalleOrdenMenuRepository.save(detalleOrdenMenu);
                    return Utils.getResponseEntity("Estado del platillo cambiado correctamente.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("Platillo no encontrado.",HttpStatus.BAD_REQUEST);

            }catch (Exception e){
                e.printStackTrace();
            }
            return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> cerrarCuenta() {
        try {
            // Datos de prueba para imprimir
            List<String[]> products = List.of(
                    new String[]{"2", "Burger", "5.99"},
                    new String[]{"1", "Fries", "2.99"},
                    new String[]{"3", "Soda", "1.50"}
            );

            // Crear ticket de orden

            // Enviar datos al servidor de impresión
           // printClient.sendPrintJob(printData);

            return Utils.getResponseEntity("Impreso correctamente", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<DatosComandaDTO>> obtenerComandasAgrupadasPorCantidad(Integer id) {
        try {
            List<DatosComandaDTO> datosComandaDTOS = new ArrayList<>();

            Optional<Cocina> optionalCocina = cocinaRepository.findById(id);
            if (optionalCocina.isPresent()) {
                Cocina cocina = optionalCocina.get();
                List<DetalleOrdenMenu> detalleOrdenMenuList = detalleOrdenMenuRepository.getAllByEstadoEqualsIgnoreCase("En espera");
                if (!detalleOrdenMenuList.isEmpty()) {
                    // Mapa para agrupar por nombre del producto
                    Map<String, DatosComandaDTO> productoMap = new HashMap<>();
                    Map<String, DatosOrdenesDTO>productoMa=new HashMap<>();

                    for (DetalleOrdenMenu detalleOrdenMenu : detalleOrdenMenuList) {
                        // Filtrar por cocina específica
                        if (detalleOrdenMenu.getMenu().getCocina().getId().equals(cocina.getId())) {
                            String nombreProducto = detalleOrdenMenu.getMenu().getNombre();

                            // Crear o actualizar DatosComandaDTO para este nombre de producto
                            DatosComandaDTO datosComandaDTO = productoMap.get(nombreProducto);

                            if (datosComandaDTO == null) {
                                datosComandaDTO = new DatosComandaDTO();
                                datosComandaDTO.setNombreCliente(detalleOrdenMenu.getOrden().getNombreCliente());
                                datosComandaDTO.setAreaServicio(detalleOrdenMenu.getOrden().getMesa().getAreaServicio().getNombre());
                                datosComandaDTO.setNombreMesa(detalleOrdenMenu.getOrden().getMesa().getNombre());
                                datosComandaDTO.setCantidadPlatillo(0); // Inicializar cantidad en 0
                                datosComandaDTO.setDetalleOrdenProductoDTOS(new ArrayList<>());
                                productoMap.put(nombreProducto, datosComandaDTO);
                            }

                            // Actualizar la cantidad de platillos
                            int nuevaCantidad = datosComandaDTO.getCantidadPlatillo() + detalleOrdenMenu.getCantidad();
                            datosComandaDTO.setCantidadPlatillo(nuevaCantidad);

                            // Agregar detalles a datosComandaDTO
                            DetalleOrdenProductoDTO detalleOrdenProductoDTO = new DetalleOrdenProductoDTO();
                            detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                            detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                            detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                            detalleOrdenProductoDTO.setNombreProducto(nombreProducto);
                            detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                            detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                            detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                            detalleOrdenProductoDTO.setEsDetalleMenu("true");

                            datosComandaDTO.getDetalleOrdenProductoDTOS().add(detalleOrdenProductoDTO);
                        }
                    }

                    // Construir lista de resultados a devolver
                    List<DatosComandaDTO> resultadoFinal = new ArrayList<>(productoMap.values());

                    return new ResponseEntity<>(resultadoFinal, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
