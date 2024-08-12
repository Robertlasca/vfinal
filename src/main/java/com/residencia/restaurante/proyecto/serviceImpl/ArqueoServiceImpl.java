package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.DetalleOrdenProductoDTO;
import com.residencia.restaurante.proyecto.dto.OrdenDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IArqueoService;
import com.residencia.restaurante.proyecto.wrapper.ArqueoSaldosWrapper;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.repository.IUsuarioRepository;
import com.residencia.restaurante.security.utils.Utils;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ArqueoServiceImpl implements IArqueoService {
    @Autowired
    private IArqueoRepository arqueoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;
    @Autowired
    private ICajaRepository cajaRepository;

    @Autowired
    private IArqueoSaldosRepository arqueoSaldosRepository;

    @Autowired
    private IMedioPagoRepository medioPagoRepository;

    @Autowired
    private IOrdenRepository ordenRepository;

    @Autowired
    private IDetalleOrden_MenuRepository detalleOrdenMenuRepository;

    /**
     * Abre un nuevo arqueo verificando primero que no haya un arqueo activo para la caja indicada.
     * Guarda el arqueo con los saldos iniciales y los medios de pago disponibles.
     * @param objetoMap Datos necesarios para abrir el arqueo como monto inicial, monto máximo, caja y usuario.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> abrirArqueo(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("monto_inicial") && objetoMap.containsKey("monto_max") && objetoMap.containsKey("caja") && objetoMap.containsKey("usuario") ){
                if(!arqueoRepository.existsArqueoByEstadoArqueoTrueAndCaja_Id(Integer.parseInt(objetoMap.get("caja")))){
                    Arqueo arqueo= new Arqueo();
                    arqueo.setSaldoInicial(Double.parseDouble(objetoMap.get("monto_inicial")));
                    arqueo.setEstadoArqueo(true);
                    Optional<Caja> optionalCaja=cajaRepository.findById(Integer.parseInt(objetoMap.get("caja")));
                    if(!optionalCaja.isEmpty()){
                        Caja caja=optionalCaja.get();
                        arqueo.setCaja(caja);


                        Optional<Usuario> usuarioOptional=usuarioRepository.findById(Integer.parseInt(objetoMap.get("usuario")));
                        if(!usuarioOptional.isEmpty()){
                            Usuario usuario=usuarioOptional.get();
                            arqueo.setUsuario(usuario);
                            arqueo.setSaldoFinal(Double.parseDouble(objetoMap.get("monto_inicial")));
                            arqueo.setSaldoMaximo(Double.parseDouble(objetoMap.get("monto_max")));
                            arqueoRepository.save(arqueo);

                            List<MedioPago> medioPagos= medioPagoRepository.getAllByDisponibilidadTrue();
                            for (MedioPago medioPago : medioPagos) {
                                ArqueoSaldos arqueoSaldos=new ArqueoSaldos();
                                arqueoSaldos.setArqueo(arqueo);
                                arqueoSaldos.setMedioPago(medioPago);
                                arqueoSaldos.setSaldoAnotado(0);
                                arqueoSaldos.setSaldoSistema(0);
                                arqueoSaldosRepository.save(arqueoSaldos);

                            }



                            return Utils.getResponseEntity("Arqueo abierto corractamente.",HttpStatus.OK);
                        }else {
                            return Utils.getResponseEntity("El usuario no existe.",HttpStatus.BAD_REQUEST);
                        }
                    }else {
                        return Utils.getResponseEntity("La caja no existe.",HttpStatus.BAD_REQUEST);
                    }

                }
                return Utils.getResponseEntity("La caja ya tiene un arqueo en curso.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Cierra un arqueo existente actualizando su estado, fecha de cierre y saldos anotados por cada medio de pago.
     * @param objetoMap Datos necesarios para cerrar el arqueo, incluido el ID del arqueo y los saldos finales por medio de pago.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    public ResponseEntity<String> cerrarArqueo(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("id_arqueo") && objetoMap.containsKey("mediosPago") && objetoMap.containsKey("saldo")){
                Optional<Arqueo> optionalArqueo= arqueoRepository.findById(Integer.parseInt(objetoMap.get("id_arqueo")));
                if(!optionalArqueo.isEmpty()){

                    Arqueo arqueo=optionalArqueo.get();

                    if(ordenRepository.existsByCajaIdAndEstadoNotIn(arqueo.getCaja().getId())){
                        return Utils.getResponseEntity("No puedes cerrar el arqueo ya existen comandas en proceso.",HttpStatus.BAD_REQUEST);
                    }

                    arqueo.setFechaHoraCierre(LocalDateTime.now());
                    arqueo.setEstadoArqueo(false);
                    arqueo.setComentario(objetoMap.get("comentario"));
                    arqueo.setSaldoIngresado(Double.parseDouble(objetoMap.get("saldo")));
                    arqueoRepository.save(arqueo);

                    ObjectMapper objectMapper=new ObjectMapper();
                    List<ArqueoSaldosWrapper> arqueoSaldosWrappers=objectMapper.readValue(objetoMap.get("mediosPago"),new TypeReference<List<ArqueoSaldosWrapper>>(){});

                    if(!arqueoSaldosWrappers.isEmpty()){
                        for (ArqueoSaldosWrapper arqueoSaldos: arqueoSaldosWrappers){
                            Optional<ArqueoSaldos> arqueoSaldosOptional=arqueoSaldosRepository.findArqueoSaldosByArqueo_IdAndMedioPago_Id(Integer.parseInt(objetoMap.get("id_arqueo")),arqueoSaldos.getMediopago());
                            if(!arqueoSaldosOptional.isEmpty()){
                                ArqueoSaldos arqueoSaldos1=arqueoSaldosOptional.get();
                                arqueoSaldos1.setSaldoAnotado(arqueoSaldos.getSaldoAnotado());
                                arqueoSaldosRepository.save(arqueoSaldos1);
                            }

                        }
                    }
                    return Utils.getResponseEntity("Arqueo cerrado correctamente.",HttpStatus.OK);
                }
                return Utils.getResponseEntity("No existe el arqueo.",HttpStatus.BAD_REQUEST);

            }

            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de todos los arqueos registrados.
     * @return Lista de arqueos con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Arqueo>> obtenerArqueos() {
        try {
            return new ResponseEntity<List<Arqueo>>(arqueoRepository.findAll(),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Arqueo>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene una lista de los arqueos actualmente activos.
     * @return Lista de arqueos activos con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Arqueo>> obtenerArqueosActivos() {
        try {
            return new ResponseEntity<List<Arqueo>>(arqueoRepository.findArqueoByEstadoArqueoTrue(),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Arqueo>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene los arqueos activos asociados a un empleado específico.
     * @param id Identificador del empleado.
     * @return Lista de arqueos activos para el empleado con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<List<Arqueo>> obtenerArqueoXEmpleado(Integer id) {
        try {
            Optional<Usuario> usuarioOptional=usuarioRepository.findById(id);
            if(usuarioOptional.isPresent()){
                return new ResponseEntity<List<Arqueo>>(arqueoRepository.findArqueoByUsuarioIdAndEstadoArqueoTrue(id),HttpStatus.OK);
            }
            return new ResponseEntity<List<Arqueo>>(new ArrayList<>(),HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Arqueo>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Obtiene un arqueo específico por su ID.
     * @param id Identificador del arqueo.
     * @return Datos del arqueo solicitado con estado HTTP correspondiente.
     */
    @Override
    public ResponseEntity<Arqueo> obtenerArqueoId(Integer id) {
        try {
            Optional<Arqueo> arqueoOptional=arqueoRepository.findById(id);
            if(arqueoOptional.isPresent()){
                Arqueo arqueo=arqueoOptional.get();
                return new ResponseEntity<Arqueo>(arqueo,HttpStatus.OK);
            }
            return new ResponseEntity<Arqueo>(new Arqueo(),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<Arqueo>(new Arqueo(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<OrdenDTO>> obtenerOrdenesEnCurso(Integer id) {
        try{
            Optional<Arqueo> arqueoOptional=arqueoRepository.findById(id);
            if(arqueoOptional.isPresent()){


            List<Orden> ordenList=ordenRepository.findOrdenesByCajaIdAndEstado(arqueoOptional.get().getCaja().getId(),"En curso");
            List<OrdenDTO> ordenDTOList=new ArrayList<>();
            if(!ordenList.isEmpty()){
                for (Orden orden:
                     ordenList) {
                    OrdenDTO ordenDTO= new OrdenDTO();
                    ordenDTO.setIdOrden(orden.getId());
                    ordenDTO.setNombreCliente(orden.getNombreCliente());
                    ordenDTO.setFolio(orden.getFolio());
                    ordenDTO.setNombreArea(orden.getMesa().getAreaServicio().getNombre());
                    ordenDTO.setNombreMesa(orden.getMesa().getNombre());
                    ordenDTO.setEstado(orden.getEstado());
                    ordenDTO.setFecha(orden.getFechaHoraApertura().toString());
                    if(orden.getUsuario()!=null){
                        ordenDTO.setIdUsuario(String.valueOf(orden.getUsuario().getId()));
                        ordenDTO.setNombreUsuario(orden.getUsuario().getNombre());
                    }


                    double total=0;

                    List<DetalleOrdenMenu> detalleOrdenMenuList= detalleOrdenMenuRepository.getAllByOrden(orden);

                    List<DetalleOrdenProductoDTO>  detalleOrdenProductoDTOS=new ArrayList<>();
                    if (!detalleOrdenMenuList.isEmpty()) {


                        for (DetalleOrdenMenu detalleOrdenMenu: detalleOrdenMenuList) {
                            DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                            detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                            detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                            detalleOrdenProductoDTO.setEsDetalleMenu("esDetalleOrdenMenu");
                            detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                            detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                            detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                            detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                            detalleOrdenProductoDTO.setPrecioUnitario(detalleOrdenMenu.getMenu().getPrecioVenta());
                            detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                            detalleOrdenProductoDTO.setImagen(detalleOrdenMenu.getMenu().getImagen());
                            total=total+detalleOrdenMenu.getTotal();
                            detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                        }
                        ordenDTO.setDetalleOrdenMenuList(detalleOrdenProductoDTOS);

                    }


                    ordenDTO.setTotal(total);
                    ordenDTOList.add(ordenDTO);

                }
                return new ResponseEntity<List<OrdenDTO>>(ordenDTOList,HttpStatus.OK);
            }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<OrdenDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<OrdenDTO>> obtenerOrdenesEnProcesoPago(Integer id) {
        try{
            Optional<Arqueo> arqueoOptional=arqueoRepository.findById(id);
            if(arqueoOptional.isPresent()) {


                List<Orden> ordenList = ordenRepository.findOrdenesByCajaIdAndEstado(arqueoOptional.get().getCaja().getId(), "Proceso de pago");
                List<OrdenDTO> ordenDTOList = new ArrayList<>();
                if (!ordenList.isEmpty()) {
                    for (Orden orden :
                            ordenList) {
                        OrdenDTO ordenDTO = new OrdenDTO();
                        ordenDTO.setIdOrden(orden.getId());
                        ordenDTO.setNombreCliente(orden.getNombreCliente());
                        ordenDTO.setFolio(orden.getFolio());
                        ordenDTO.setNombreArea(orden.getMesa().getAreaServicio().getNombre() + orden.getMesa().getNombre());
                        ordenDTO.setEstado(orden.getEstado());
                        ordenDTO.setFecha(orden.getFechaHoraApertura().toString());
                        if(orden.getUsuario()!=null){
                            ordenDTO.setIdUsuario(orden.getUsuario().getNombre());
                            ordenDTO.setNombreUsuario(orden.getUsuario().getNombre());
                        }

                        double total = 0;

                        List<DetalleOrdenMenu> detalleOrdenMenuList = detalleOrdenMenuRepository.getAllByOrden(orden);

                        List<DetalleOrdenProductoDTO>  detalleOrdenProductoDTOS=new ArrayList<>();
                        if (!detalleOrdenMenuList.isEmpty()) {


                                for (DetalleOrdenMenu detalleOrdenMenu: detalleOrdenMenuList) {
                                    DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                                    detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                                    detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                                    detalleOrdenProductoDTO.setEsDetalleMenu("esDetalleOrdenMenu");
                                    detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                                    detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                                    detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                                    detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                                    detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                                    detalleOrdenProductoDTO.setImagen(detalleOrdenMenu.getMenu().getImagen());
                                    total=total+detalleOrdenMenu.getTotal();
                                    detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                                }
                                ordenDTO.setDetalleOrdenMenuList(detalleOrdenProductoDTOS);

                        }


                        ordenDTO.setTotal(total);
                        ordenDTOList.add(ordenDTO);

                    }
                    return new ResponseEntity<List<OrdenDTO>>(ordenDTOList, HttpStatus.OK);

                }
                return new ResponseEntity<List<OrdenDTO>>(ordenDTOList, HttpStatus.BAD_REQUEST);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<OrdenDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<OrdenDTO>> obtenerOrdenesTerminadas(Integer id) {
        try{
            Optional<Arqueo> arqueoOptional=arqueoRepository.findById(id);
            if(arqueoOptional.isPresent()) {


                List<Orden> ordenList = ordenRepository.findOrdenesByCajaIdAndEstado(arqueoOptional.get().getCaja().getId(), "Terminada");
                List<OrdenDTO> ordenDTOList = new ArrayList<>();
                if (!ordenList.isEmpty()) {
                    for (Orden orden :
                            ordenList) {
                        OrdenDTO ordenDTO = new OrdenDTO();
                        ordenDTO.setIdOrden(orden.getId());
                        ordenDTO.setNombreCliente(orden.getNombreCliente());
                        ordenDTO.setFolio(orden.getFolio());
                        ordenDTO.setNombreArea(orden.getMesa().getAreaServicio().getNombre() + orden.getMesa().getNombre());
                        ordenDTO.setEstado(orden.getEstado());
                        ordenDTO.setFecha(orden.getFechaHoraApertura().toString());
                        if(orden.getUsuario()!=null){
                            ordenDTO.setIdUsuario(orden.getUsuario().getNombre());
                            ordenDTO.setNombreUsuario(orden.getUsuario().getNombre());
                        }
                        double total = 0;

                        List<DetalleOrdenMenu> detalleOrdenMenuList = detalleOrdenMenuRepository.getAllByOrden(orden);

                        List<DetalleOrdenProductoDTO>  detalleOrdenProductoDTOS=new ArrayList<>();
                        if (!detalleOrdenMenuList.isEmpty()) {


                            for (DetalleOrdenMenu detalleOrdenMenu: detalleOrdenMenuList) {
                                DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                                detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                                detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                                detalleOrdenProductoDTO.setEsDetalleMenu("esDetalleOrdenMenu");
                                detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                                detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                                detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                                detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                                detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                                detalleOrdenProductoDTO.setImagen(detalleOrdenMenu.getMenu().getImagen());
                                total=total+detalleOrdenMenu.getTotal();
                                detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                            }
                            ordenDTO.setDetalleOrdenMenuList(detalleOrdenProductoDTOS);

                        }


                        ordenDTO.setTotal(total);
                        ordenDTOList.add(ordenDTO);

                    }
                    return new ResponseEntity<List<OrdenDTO>>(ordenDTOList, HttpStatus.OK);
                }

                return new ResponseEntity<List<OrdenDTO>>(ordenDTOList, HttpStatus.BAD_REQUEST);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<OrdenDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
