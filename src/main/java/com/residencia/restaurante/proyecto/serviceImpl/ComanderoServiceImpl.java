package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.ComandaDTO;
import com.residencia.restaurante.proyecto.dto.DetalleOrdenProductoDTO;
import com.residencia.restaurante.proyecto.dto.ProductoDto;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IComanderoService;
import com.residencia.restaurante.proyecto.wrapper.DetalleOrdenWrapper;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.repository.IUsuarioRepository;
import com.residencia.restaurante.security.utils.TicketComanda;
import com.residencia.restaurante.security.utils.Utils;
import org.apache.tomcat.util.bcel.Const;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.*;

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
    @Autowired
    private FolioService folioService;

    @Autowired
    private ICajaRepository cajaRepository;

    @Autowired
    private IDetalleOrden_MenuRepository detalleOrdenMenuRepository;
    @Autowired
    private IUsuarioRepository usuarioRepository;
    @Autowired
    private IMesaRepository mesaRepository;
@Autowired
    private IInventarioRepository inventarioRepository;
@Autowired
    private IMateriaPrima_MenuRepository materiaPrimaMenuRepository;

    @Autowired
    private IProductoNormalRepository productoNormalRepository;
    @Autowired
    private IMenuRepository menuRepository;

    @Autowired
    private  IImpresoraRepository impresoraRepository;
    @Override
    public ResponseEntity<Orden> abrirOrden(Map<String, String> objetoMap) {
       try {
           if(objetoMap.containsKey("idMesa") && objetoMap.containsKey("idUsuario") && objetoMap.containsKey("numComensales") && objetoMap.containsKey("idCaja")){
               Optional<Mesa> optionalMesa= mesaRepository.findById(Integer.parseInt(objetoMap.get("idMesa")));
               Optional<Usuario> optionalUsuario=usuarioRepository.findById(Integer.parseInt(objetoMap.get("idUsuario")));
               Optional<Caja> optionalCaja=cajaRepository.findById(Integer.parseInt(objetoMap.get("idCaja")));
               Orden orden= new Orden();
               if (!optionalMesa.get().getEstado().equalsIgnoreCase("Disponible")){
                   return new ResponseEntity<Orden>(new Orden(),HttpStatus.BAD_REQUEST);
               }
               optionalMesa.ifPresent(orden::setMesa);
               Mesa mesa=optionalMesa.get();
               mesa.setEstado("Ocupada");
               mesaRepository.save(mesa);
               optionalUsuario.ifPresent(orden::setUsuario);
               optionalCaja.ifPresent(orden::setCaja);
               if(objetoMap.containsKey("nombreCliente")){
                   orden.setNombreCliente(objetoMap.get("nombreCliente"));
               }
               orden.setEstado("En curso");
               orden.setFolio(folioService.getNextFolio());
               orden.setCantidadComensal(Integer.parseInt(objetoMap.get("numComensales")));
               ordenRepository.save(orden);


               return new ResponseEntity<Orden>(orden,HttpStatus.OK);
           }
           return new ResponseEntity<Orden>(new Orden(),HttpStatus.BAD_REQUEST);


       }catch (Exception e){
           e.printStackTrace();
       }
       return new ResponseEntity<Orden>(new Orden(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> asignarPlatillos(Map<String, String> objetoMap) {
        try{
            if(objetoMap.containsKey("idOrden") && objetoMap.containsKey("detalleOrden")){
              //  if(validarStock(objetoMap.get("detalleOrden")).equalsIgnoreCase("suficiente")){
                Optional<Orden> optional=ordenRepository.findById(Integer.parseInt(objetoMap.get("idOrden")));
                if (optional.isPresent()){
                    Orden orden= optional.get();


                ObjectMapper objectMapper = new ObjectMapper();

                    List<DetalleOrdenWrapper> detalleOrdenWrappers = objectMapper.readValue(objetoMap.get("detalleOrden"), new TypeReference<List<DetalleOrdenWrapper>>() {
                    });


                    Set<Integer> cocinas = new HashSet<>();
                    Map<Integer, List<String>> productosPorCocina = new HashMap<>();



                    if(!detalleOrdenWrappers.isEmpty()) {
                        for (DetalleOrdenWrapper detalleOrdenWrapper : detalleOrdenWrappers) {
                            System.out.println(detalleOrdenWrapper.getIsMenu());
                            if (detalleOrdenWrapper.getIsMenu().equalsIgnoreCase("true")) {
                                //Verificar si es un menu con producto terminados.
                                DetalleOrdenMenu detalleOrdenMenu = new DetalleOrdenMenu();
                                Optional<Menu> menuOptional = menuRepository.findById(detalleOrdenWrapper.getIdProducto());

                                Menu menu = new Menu();
                                Integer cocinaId = 0;
                                if (menuOptional.isPresent()) {
                                    menu = menuOptional.get();
                                    cocinaId=menuOptional.get().getCocina().getId();
                                }

                                if (!cocinas.contains(menu.getCocina().getId())){
                                    cocinas.add(cocinaId);
                                    productosPorCocina.put(cocinaId, new ArrayList<>());
                                }

                                // Añadir el producto a la lista correspondiente a la cocina
                                List<String> productos = productosPorCocina.get(cocinaId);
                                menu.getNombre();
                                detalleOrdenWrapper.getCantidad();
                                detalleOrdenWrapper.getComentario();
                                String productoDetalle = menu.getNombre() + detalleOrdenWrapper.getCantidad() +  detalleOrdenWrapper.getComentario();
                                productos.add(productoDetalle); // O cualquier otra propiedad del menú que represente al producto
                            }


                        }




                    }

                    if(!detalleOrdenWrappers.isEmpty()){
                        for (DetalleOrdenWrapper detalleOrdenWrapper: detalleOrdenWrappers) {
                            System.out.println(detalleOrdenWrapper.getIsMenu());
                            if(detalleOrdenWrapper.getIsMenu().equalsIgnoreCase("true")){
                                //Verificar si es un menu con producto terminados o
                                DetalleOrdenMenu detalleOrdenMenu= new DetalleOrdenMenu();
                                Optional<Menu> menuOptional= menuRepository.findById(detalleOrdenWrapper.getIdProducto());

                                Menu menu= new Menu();
                                if(menuOptional.isPresent()){
                                    menu= menuOptional.get();
                                }


                                detalleOrdenMenu.setMenu(menu);
                                detalleOrdenMenu.setOrden(orden);
                                detalleOrdenMenu.setCantidad(detalleOrdenWrapper.getCantidad());
                                detalleOrdenMenu.setTotal(menu.getPrecioVenta()*detalleOrdenWrapper.getCantidad());
                                detalleOrdenMenu.setComentario(detalleOrdenWrapper.getComentario());
                                detalleOrdenMenu.setEstado("En espera");
                                detalleOrdenMenuRepository.save(detalleOrdenMenu);
                                //Descontar stock



                            }
                            if(detalleOrdenWrapper.getIsMenu().equalsIgnoreCase("false")){

                                DetalleOrden_ProductoNormal detalleOrdenProductoNormal= new DetalleOrden_ProductoNormal();
                                detalleOrdenProductoNormal.setCantidad(detalleOrdenWrapper.getCantidad());
                                Optional<ProductoNormal> productoNormalOptional= productoNormalRepository.findById(detalleOrdenWrapper.getIdProducto());

                                productoNormalOptional.ifPresent(detalleOrdenProductoNormal::setProductoNormal);
                                detalleOrdenProductoNormal.setOrden(orden);
                                detalleOrdenProductoNormal.setEstado("En espera");
                                detalleOrdenProductoNormal.setComentario(detalleOrdenWrapper.getComentario());
                                detalleOrdenProductoNormal.setTotal(detalleOrdenWrapper.getCantidad()*productoNormalOptional.get().getPrecioUnitario());

                                //Descontar del stock
                                if(productoNormalOptional.isPresent()){
                                    ProductoNormal productoNormal=productoNormalOptional.get();
                                   // productoNormal.setStockActual(productoNormal.getStockActual()-detalleOrdenWrapper.getCantidad());
                                   // productoNormalRepository.save(productoNormal);
                                }
                                detalleOrdenProductoNormalRepository.save(detalleOrdenProductoNormal);

                            }




                        }

                        Optional<Impresora> impresoraOptional = impresoraRepository.getImpresoraByPorDefectoTrue();
                        if (impresoraOptional.isPresent()) {
                            System.out.println("No se ha configurado una impresora por defecto.");
                        }

                        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
                        PrintService selectedService = null;

                        for (PrintService service : printServices) {
                            if (service.getName().equalsIgnoreCase(impresoraOptional.get().getNombre()) || service.getName().contains(impresoraOptional.get().getDireccionIp())) {
                                selectedService = service;
                                break;
                            }
                        }

                        if (selectedService == null) {
                            System.out.println("No se encontró la impresora POS-58 en el puerto USB001.");
                        }






                        for (Map.Entry<Integer, List<String>> entry : productosPorCocina.entrySet()) {
                            Integer cocinaId = entry.getKey();
                            List<String> productos = entry.getValue();
                            System.out.println("Cocina ID: " + cocinaId);
                            System.out.println("Productos: " + productos);
                            TicketComanda ticket = new TicketComanda(orden.getNombreCliente(), orden.getMesa().getAreaServicio().getNombre(), orden.getUsuario().getNombre(), productos);
                            ticket.print(selectedService);
                            // Aquí puedes llamar a tu método de impresión o realizar otras operaciones
                        }
                        return Utils.getResponseEntity("Platillos asignados correctamente.",HttpStatus.OK);
                    }
                    return Utils.getResponseEntity("Error al obtener los detalles de la orden.",HttpStatus.BAD_REQUEST);

            }
                return Utils.getResponseEntity("La orden no existe.",HttpStatus.BAD_REQUEST);


                //}

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String validarStock(String detalleOrdenJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<DetalleOrdenWrapper> detalleOrdenWrappers = objectMapper.readValue(detalleOrdenJson, new TypeReference<List<DetalleOrdenWrapper>>() {});
            Map<Integer, Double> necesidadIngredientes = new HashMap<>(); // ID de Inventario y cantidad total requerida

            for (DetalleOrdenWrapper detalle : detalleOrdenWrappers) {
                if (detalle.getIsMenu().equalsIgnoreCase("true")) {
                    List<MateriaPrima_Menu> ingredientesMenu = materiaPrimaMenuRepository.findByMenuId(detalle.getIdProducto());
                    for (MateriaPrima_Menu ingrediente : ingredientesMenu) {
                        int inventarioId = ingrediente.getInventario().getId();
                        double cantidadNecesaria = ingrediente.getCantidad() * detalle.getCantidad();
                        necesidadIngredientes.merge(inventarioId, cantidadNecesaria, Double::sum);
                    }
                }
            }

            StringBuilder menusNoPreparables = new StringBuilder();
            for (Map.Entry<Integer, Double> entrada : necesidadIngredientes.entrySet()) {
                Inventario inventario = inventarioRepository.findById(entrada.getKey()).orElse(null);
                if (inventario != null && entrada.getValue() > inventario.getStockActual()) {
                    menusNoPreparables.append("No hay suficiente stock para el ingrediente con ID de inventario ")
                            .append(entrada.getKey())
                            .append(". Necesitado: ")
                            .append(entrada.getValue())
                            .append(", Disponible: ")
                            .append(inventario.getStockActual())
                            .append(".\n");
                }
            }

            return menusNoPreparables.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar el pedido.";
        }
    }


    @Override
    public ResponseEntity<List<ProductoDto>> obtenerProductos() {

        try {
            List<ProductoDto> productoDtos=new ArrayList<>();
            List<ProductoNormal> productoNormalList= productoNormalRepository.getAllByVisibilidadTrue();
            List<Menu> menuList=menuRepository.getAllByVisibilidadTrue();
            if(!productoNormalList.isEmpty()){
                for (ProductoNormal productoNormal:productoNormalList) {
                    ProductoDto productoDto= new ProductoDto();
                    productoDto.setId(productoNormal.getId());
                    productoDto.setNombre(productoNormal.getNombre());
                    productoDto.setImagen(productoNormal.getImagen());
                    productoDto.setPrecio(productoNormal.getPrecioUnitario());
                    productoDto.setMenu(false);

                    productoDtos.add(productoDto);
                }
            }

            if(!menuList.isEmpty()){
                for (Menu menu:menuList) {
                    ProductoDto productoDto= new ProductoDto();
                    productoDto.setId(menu.getId());
                    productoDto.setNombre(menu.getNombre());
                    productoDto.setImagen(menu.getImagen());
                    productoDto.setPrecio(menu.getPrecioVenta());
                    productoDto.setMenu(true);
                    productoDtos.add(productoDto);
                }

            }
            return new ResponseEntity<List<ProductoDto>>(productoDtos,HttpStatus.OK);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<ProductoDto>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Orden>> obtenerOrdenes() {
        try {
            return new ResponseEntity<List<Orden>>(ordenRepository.findAll(),HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Orden>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoDto> obtenerProducto(Map<String, String> objetoMap) {
        try{
            if(objetoMap.containsKey("id") && objetoMap.containsKey("esMenu")){
                Integer id= Integer.parseInt(objetoMap.get("id"));
                ProductoDto productoDto= new ProductoDto();
                if(objetoMap.get("esMenu").equalsIgnoreCase("true")){
                    Optional<Menu> menuOptional= menuRepository.findById(id);
                    if(menuOptional.isPresent()){
                        Menu menu= menuOptional.get();
                        productoDto.setMenu(true);
                        productoDto.setId(menu.getId());
                        productoDto.setNombre(menu.getNombre());
                        productoDto.setImagen(menu.getImagen());
                        productoDto.setPrecio(menu.getPrecioVenta());
                    }

                }else {
                    Optional<ProductoNormal> productoNormalOptional= productoNormalRepository.findById(id);
                    if(productoNormalOptional.isPresent()){
                        ProductoNormal productoNormal= productoNormalOptional.get();
                        productoDto.setMenu(false);
                        productoDto.setId(productoNormal.getId());
                        productoDto.setNombre(productoNormal.getNombre());
                        productoDto.setImagen(productoNormal.getImagen());
                        productoDto.setPrecio(productoDto.getPrecio());

                    }

                }

                return new ResponseEntity<ProductoDto>(productoDto,HttpStatus.OK);
            }
            return new ResponseEntity<ProductoDto>(new ProductoDto(),HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ProductoDto>(new ProductoDto(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<ComandaDTO> obtenerComandaPorIdOrden(Integer id) {
        try{
            Optional<Orden> optionalOrden=ordenRepository.findById(id);
            if(optionalOrden.isPresent()){
                double total= 0;
                Orden orden= optionalOrden.get();
                List<DetalleOrdenMenu> detalleOrdenMenus= detalleOrdenMenuRepository.getAllByOrden(orden);
                List<DetalleOrden_ProductoNormal>detalleOrdenProductoNormals= detalleOrdenProductoNormalRepository.getAllByOrden(orden);
                List<DetalleOrdenProductoDTO> detalleOrdenProductoDTOS= new ArrayList<>();
                ComandaDTO comandaDTO= new ComandaDTO();
                comandaDTO.setOrden(orden);
                if(!detalleOrdenMenus.isEmpty()){
                    for (DetalleOrdenMenu detalleOrdenMenu: detalleOrdenMenus) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                        detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                        detalleOrdenProductoDTO.setEsDetalleMenu("true");
                        detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                        detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                        detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                        detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                        detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                        total=total+detalleOrdenMenu.getTotal();
                        detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                    }
                }

                if(!detalleOrdenProductoNormals.isEmpty()){
                    for (DetalleOrden_ProductoNormal detalleOrdenProductoNormal:detalleOrdenProductoNormals) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenProductoNormal.getId());
                        detalleOrdenProductoDTO.setIdProducto(detalleOrdenProductoNormal.getProductoNormal().getId());
                        detalleOrdenProductoDTO.setEsDetalleMenu("true");
                        detalleOrdenProductoDTO.setNombreProducto(detalleOrdenProductoNormal.getProductoNormal().getNombre());
                        detalleOrdenProductoDTO.setComentario(detalleOrdenProductoNormal.getComentario());
                        detalleOrdenProductoDTO.setCantidad(detalleOrdenProductoNormal.getCantidad());
                        detalleOrdenProductoDTO.setEstado(detalleOrdenProductoNormal.getEstado());
                        detalleOrdenProductoDTO.setTotal(detalleOrdenProductoNormal.getTotal());
                        total=total+detalleOrdenProductoNormal.getTotal();
                        detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                    }
                }
                comandaDTO.setDetalleOrdenProductoDTOS(detalleOrdenProductoDTOS);

                comandaDTO.setTotal(total);

                return new ResponseEntity<ComandaDTO>(comandaDTO,HttpStatus.OK);

            }
            return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ComandaDTO> obtenerComandaPorIdOrdenMesa(Integer id) {
        try{

            Optional<Orden> optionalOrden=ordenRepository.findOrdensByMesaId(id);
            if(optionalOrden.isPresent()){
                double total= 0;
                Orden orden= optionalOrden.get();
                List<DetalleOrdenMenu> detalleOrdenMenus= detalleOrdenMenuRepository.getAllByOrden(orden);
                List<DetalleOrden_ProductoNormal>detalleOrdenProductoNormals= detalleOrdenProductoNormalRepository.getAllByOrden(orden);
                List<DetalleOrdenProductoDTO> detalleOrdenProductoDTOS= new ArrayList<>();
                ComandaDTO comandaDTO= new ComandaDTO();
                comandaDTO.setOrden(orden);
                if(!detalleOrdenMenus.isEmpty()){
                    for (DetalleOrdenMenu detalleOrdenMenu: detalleOrdenMenus) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenMenu.getId());
                        detalleOrdenProductoDTO.setIdProducto(detalleOrdenMenu.getMenu().getId());
                        detalleOrdenProductoDTO.setEsDetalleMenu("true");
                        detalleOrdenProductoDTO.setNombreProducto(detalleOrdenMenu.getMenu().getNombre());
                        detalleOrdenProductoDTO.setComentario(detalleOrdenMenu.getComentario());
                        detalleOrdenProductoDTO.setCantidad(detalleOrdenMenu.getCantidad());
                        detalleOrdenProductoDTO.setEstado(detalleOrdenMenu.getEstado());
                        detalleOrdenProductoDTO.setTotal(detalleOrdenMenu.getTotal());
                        total=total+detalleOrdenMenu.getTotal();
                        detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                    }
                }

                if(!detalleOrdenProductoNormals.isEmpty()){
                    for (DetalleOrden_ProductoNormal detalleOrdenProductoNormal:detalleOrdenProductoNormals) {
                        DetalleOrdenProductoDTO detalleOrdenProductoDTO= new DetalleOrdenProductoDTO();
                        detalleOrdenProductoDTO.setIdDetalleOrden(detalleOrdenProductoNormal.getId());
                        detalleOrdenProductoDTO.setIdProducto(detalleOrdenProductoNormal.getProductoNormal().getId());
                        detalleOrdenProductoDTO.setEsDetalleMenu("true");
                        detalleOrdenProductoDTO.setNombreProducto(detalleOrdenProductoNormal.getProductoNormal().getNombre());
                        detalleOrdenProductoDTO.setComentario(detalleOrdenProductoNormal.getComentario());
                        detalleOrdenProductoDTO.setCantidad(detalleOrdenProductoNormal.getCantidad());
                        detalleOrdenProductoDTO.setEstado(detalleOrdenProductoNormal.getEstado());
                        detalleOrdenProductoDTO.setTotal(detalleOrdenProductoNormal.getTotal());
                        total=total+detalleOrdenProductoNormal.getTotal();
                        detalleOrdenProductoDTOS.add(detalleOrdenProductoDTO);
                    }
                }
                comandaDTO.setDetalleOrdenProductoDTOS(detalleOrdenProductoDTOS);

                comandaDTO.setTotal(total);

                return new ResponseEntity<ComandaDTO>(comandaDTO,HttpStatus.OK);

            }
            return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ComandaDTO>(new ComandaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> cerrarCuenta(Integer id) {
        try{
            Optional<Orden> ordenOptional= ordenRepository.findById(id);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
