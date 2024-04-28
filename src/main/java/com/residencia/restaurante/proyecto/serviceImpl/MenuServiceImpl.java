package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.MenuDTO;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IMenuService;
import com.residencia.restaurante.proyecto.wrapper.RecetaWrapper;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class MenuServiceImpl implements IMenuService {
    @Autowired
    private IMenuRepository menuRepository;

    @Autowired
    private IMateriaPrima_MenuRepository materiaPrimaMenuRepository;

    @Autowired
    private IProductoTerminado_MenuRepository productoTerminadoMenuRepository;

    @Autowired
    private IProductoTerminadoRepository productoTerminadoRepository;

    @Autowired
    private IAlmacenRepository almacenRepository;

    @Autowired
    private IInventarioRepository inventarioRepository;

    @Autowired
    private ICategoriaRepository categoriaRepository;

    @Autowired
    private  UploadFileService uploadFileService;

    @Autowired
    private IMateriaPrima_ProductoTerminadoRepository materiaPrimaProductoTerminadoRepository;


    @Override
    public ResponseEntity<List<MenuDTO>> obtenerActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<MenuDTO>> obtenerNoActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<MenuDTO>> obtenerTodos() {
        try {
            List<Menu> menuList=menuRepository.findAll();
            List<MenuDTO> menuDTOS=new ArrayList<>();
            for (Menu menu: menuList) {
                MenuDTO menuDTO= new MenuDTO();
                menuDTO.setMenu(menu);
                menuDTO.setGanancia(menu.getPrecioVenta()-menu.getCostoProduccionDirecto());
                if(menu.isVisibilidad()){
                    menuDTO.setDisponibilidad("Visible");

                }else {
                    menuDTO.setDisponibilidad("No visible");
                }

                menuDTOS.add(menuDTO);

            }

            return new ResponseEntity<List<MenuDTO>>(menuDTOS,HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<MenuDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregar(String nombre, String descripcion, double costoProduccion, double margenGanancia, double precioVenta, MultipartFile file, int idCategoria, String receta) {
        try {
            if(!menuRepository.existsByNombreLikeIgnoreCase(nombre)){
                if(validarCategoriaId(idCategoria)){
                    Menu menu=new Menu();
                    Optional<Categoria> categoriaOptional= categoriaRepository.findById(idCategoria);
                    categoriaOptional.ifPresent(menu::setCategoria);
                    menu.setNombre(nombre);
                    menu.setDescripcion(descripcion);
                    menu.setVisibilidad(true);
                    menu.setDependent(false);
                    menu.setCostoProduccionDirecto(costoProduccion);
                    menu.setMargenGanancia(margenGanancia);
                    menu.setPrecioVenta(precioVenta);


                    String nombreImagen= uploadFileService.guardarImagen(file);
                    menu.setImagen(nombreImagen);
                    menuRepository.save(menu);

                    ObjectMapper objectMapper= new ObjectMapper();
                    try {
                        List<RecetaWrapper> recetaWrappers=objectMapper.readValue(receta, new TypeReference<List<RecetaWrapper>>() {
                        });
                        if(!recetaWrappers.isEmpty()){
                            for(RecetaWrapper recetaWrapper: recetaWrappers){
                                if(recetaWrapper.getEsIngrediente().equalsIgnoreCase("true")){
                                    Optional<Inventario> optionalInventario= inventarioRepository.findById(recetaWrapper.getId());
                                    if(optionalInventario.isPresent()){
                                        MateriaPrima_Menu materiaPrimaMenu= new MateriaPrima_Menu();
                                        Inventario inventario= optionalInventario.get();
                                        materiaPrimaMenu.setMenu(menu);
                                        materiaPrimaMenu.setInventario(inventario);
                                        materiaPrimaMenu.setCantidad(recetaWrapper.getCantidad());
                                        materiaPrimaMenuRepository.save(materiaPrimaMenu);


                                    }

                                }else {
                                    Optional<ProductoTerminado> productoTerminadoOptional=productoTerminadoRepository.findById(recetaWrapper.getId());
                                    if(productoTerminadoOptional.isPresent()){
                                        ProductoTerminado productoTerminado=productoTerminadoOptional.get();
                                        ProductoTerminado_Menu productoTerminadoMenu=new ProductoTerminado_Menu();

                                        productoTerminadoMenu.setMenu(menu);
                                        productoTerminadoMenu.setProductoTerminado(productoTerminado);
                                        productoTerminadoMenu.setCantidad(recetaWrapper.getCantidad());
                                        productoTerminadoMenuRepository.save(productoTerminadoMenu);
                                    }

                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return Utils.getResponseEntity("Menú  guardado correctamente.",HttpStatus.OK);


                }
                return Utils.getResponseEntity("Error al asignar la categoría.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity("El nombre del menú ya existe.",HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<ProductoTerminado> obtenerProductoTerminado(Integer id) {
        return null;
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerRecetaId(Integer id) {
        try {
            Optional<Menu> menuOptional= menuRepository.findById(id);
            List<RecetaDTO> recetaDTOS= new ArrayList<>();
            if(menuOptional.isPresent()){
                Menu menu= menuOptional.get();

                List<MateriaPrima_Menu> materiaPrimaMenus= materiaPrimaMenuRepository.getAllByMenu(menu);
                List<ProductoTerminado_Menu> productoTerminadoMenus= productoTerminadoMenuRepository.getAllByMenu(menu);

                if(!materiaPrimaMenus.isEmpty()){
                    for (MateriaPrima_Menu materiaPrimaMenu:materiaPrimaMenus ) {
                        MateriaPrima materiaPrima= materiaPrimaMenu.getInventario().getMateriaPrima();
                        double costo= calcularCostoProduccion(materiaPrima.getCostoUnitario());
                        RecetaDTO recetaDTO= new RecetaDTO();
                        recetaDTO.setEsIngrediente(true);
                        recetaDTO.setId(materiaPrima.getId());
                        recetaDTO.setUnidadMedida(materiaPrima.getUnidadMedida());
                        recetaDTO.setNombre(materiaPrima.getNombre());
                        recetaDTO.setCantidad(materiaPrimaMenu.getCantidad());
                        //Costo de produccion ejemplo que la materia prima ocupe 0.200
                        recetaDTO.setCostoProduccion(rendondearADos((materiaPrimaMenu.getCantidad()*1000)*costo));
                        recetaDTOS.add(recetaDTO);


                    }
                }

                if(!productoTerminadoMenus.isEmpty()){
                    for(ProductoTerminado_Menu productoTerminadoMenu:productoTerminadoMenus){

                        ProductoTerminado productoTerminado= productoTerminadoMenu.getProductoTerminado();
                        RecetaDTO recetaDTO= new RecetaDTO();
                        double precio=rendondearADos(calcularCostoProduccionTotal(productoTerminado.getId()));
                        double costoProduccionXUnidad= rendondearADos(precio/1000);
                        recetaDTO.setId(productoTerminado.getId());
                        recetaDTO.setNombre(productoTerminado.getNombre());
                        recetaDTO.setCostoProduccion(rendondearADos((productoTerminadoMenu.getCantidad()*1000)*costoProduccionXUnidad));
                        recetaDTO.setEsIngrediente(false);
                        recetaDTO.setUnidadMedida(productoTerminado.getUnidadMedida());
                        recetaDTO.setCantidad(productoTerminadoMenu.getCantidad());
                        recetaDTOS.add(recetaDTO);


                    }
                }

                return new ResponseEntity<List<RecetaDTO>>(recetaDTOS,HttpStatus.OK);

            }
            return new ResponseEntity<List<RecetaDTO>>(new ArrayList<>(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<RecetaDTO>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerIngredientesOProductosTerminadosIdCocina(Integer id) {
        try {
            List<RecetaDTO> recetaDTOList = new ArrayList<>();
            List<Inventario> inventarios = obtenerMateriasIdCocina(id);
            List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados = obtenerProductosTerminadosIdCocina(id);

            // Usamos un mapa para mantener un único elemento por nombre de receta
            Map<String, RecetaDTO> recetasUnicas = new HashMap<>();

            // Agregamos las recetas de los inventarios
            for (Inventario inventario : inventarios) {
                RecetaDTO recetaDTO = new RecetaDTO();
                recetaDTO.setId(inventario.getId());
                recetaDTO.setEsIngrediente(true);
                recetaDTO.setNombre(inventario.getMateriaPrima().getNombre());
                recetasUnicas.put(recetaDTO.getNombre(), recetaDTO);
            }

            // Agregamos las recetas de los productos terminados
            for (MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado : materiaPrimaProductoTerminados) {
                RecetaDTO recetaDTO = new RecetaDTO();
                recetaDTO.setId(materiaPrimaProductoTerminado.getProductoTerminado().getId());
                recetaDTO.setEsIngrediente(false);
                recetaDTO.setNombre(materiaPrimaProductoTerminado.getProductoTerminado().getNombre());
                recetasUnicas.put(recetaDTO.getNombre(), recetaDTO);
            }

            // Convertimos el mapa de recetas únicas en una lista
            recetaDTOList.addAll(recetasUnicas.values());

            return new ResponseEntity<List<RecetaDTO>>(recetaDTOList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<RecetaDTO>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<RecetaDTO> obtenerIngredientesOProductosTerminadosXId(Map<String, String> objetoMap) {
        try {
            if(validarExistenciaMateriaOProducto(Integer.parseInt(objetoMap.get("id")),Boolean.parseBoolean(objetoMap.get("esIngrediente"))) ){

                if(objetoMap.get("esIngrediente").equalsIgnoreCase("true")){
                    Optional<Inventario> optionalInventario= inventarioRepository.findById(Integer.parseInt(objetoMap.get("id")));

                    if(optionalInventario.isPresent()){

                        MateriaPrima materiaPrima= optionalInventario.get().getMateriaPrima();

                        double precio= materiaPrima.getCostoUnitario();
                        RecetaDTO recetaDTO= new RecetaDTO();
                        recetaDTO.setNombre(materiaPrima.getNombre());
                        //Precio por gramo
                        recetaDTO.setCostoProduccion(calcularCostoProduccion(precio));
                        recetaDTO.setUnidadMedida(materiaPrima.getUnidadMedida());
                        recetaDTO.setEsIngrediente(true);
                        recetaDTO.setId(materiaPrima.getId());
                        return new ResponseEntity<RecetaDTO>(recetaDTO,HttpStatus.OK);


                    }


                }else {
                    Optional<ProductoTerminado> optionalProductoTerminado= productoTerminadoRepository.findById(Integer.parseInt(objetoMap.get("id")));
                    if(optionalProductoTerminado.isPresent()){
                        ProductoTerminado productoTerminado= optionalProductoTerminado.get();
                        RecetaDTO recetaDTO= new RecetaDTO();
                        double precio=rendondearADos(calcularCostoProduccionTotal(productoTerminado.getId()));
                        double costoProduccionXUnidad= rendondearADos(precio/1000);
                        recetaDTO.setId(productoTerminado.getId());
                        recetaDTO.setNombre(productoTerminado.getNombre());
                        recetaDTO.setCostoProduccion(costoProduccionXUnidad);
                        recetaDTO.setEsIngrediente(false);
                        recetaDTO.setUnidadMedida(productoTerminado.getUnidadMedida());
                        return new ResponseEntity<RecetaDTO>(recetaDTO,HttpStatus.OK);



                    }
                }

            }
            return new ResponseEntity<RecetaDTO>(new RecetaDTO(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<RecetaDTO>(new RecetaDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validarExistenciaMateriaOProducto(int id, boolean esIngrediente) {
        System.out.println("Si llegue a la validacion");
        if(esIngrediente){
            Optional<Inventario> inventarioOptional= inventarioRepository.findById(id);
            System.out.println(inventarioOptional.isPresent());
            if(inventarioOptional.isPresent()){
                return true;
            }
            return false;
        }else {
            Optional<ProductoTerminado> productoTerminadoOptional=productoTerminadoRepository.findById(id);
            if(productoTerminadoOptional.isPresent()){
                return true;
            }
            return false;
        }
    }

    public List<Inventario> obtenerMateriasIdCocina(Integer id) {
        List<Inventario> inventarios = new ArrayList<>();
        Optional<Almacen> optional = almacenRepository.findAlmacenByCocina_Id(id);
        if (optional.isPresent()) {
            List<Inventario> inventarioList = inventarioRepository.getAllByAlmacen_Id(optional.get().getId());
            for (Inventario inventario : inventarioList) {
                if (inventario.getMateriaPrima().isVisibilidad()) {
                    inventarios.add(inventario);
                }

            }
            return inventarios;

        }
        return new ArrayList<>();
    }

    public List<MateriaPrima_ProductoTerminado> obtenerProductosTerminadosIdCocina(Integer id){
        List<MateriaPrima_ProductoTerminado> primaProductoTerminados=new ArrayList<>();
        List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados= materiaPrimaProductoTerminadoRepository.findAll();
        Optional<Almacen> almacenOptional= almacenRepository.findAlmacenByCocina_Id(id);
        if(almacenOptional.isPresent()){
            Almacen almacen= almacenOptional.get();
            for (MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado: materiaPrimaProductoTerminados) {
                Optional<Inventario> inventarioOptional= inventarioRepository.findById(materiaPrimaProductoTerminado.getInventario().getId());
                if(inventarioOptional.isPresent()){
                    Inventario inventario=inventarioOptional.get();
                    if(inventario.getMateriaPrima().isVisibilidad() && materiaPrimaProductoTerminado.getProductoTerminado().isVisibilidad()){
                        primaProductoTerminados.add(materiaPrimaProductoTerminado);
                    }
                }


            }

            return primaProductoTerminados;

        }
        return new ArrayList<>();



    }

    private double calcularCostoProduccion(double precio){
        double costoPorGramo= precio/1000;

        // Crear un objeto DecimalFormat para redondear a 4 decimales
        DecimalFormat df = new DecimalFormat("#.####");

        // Aplicar el formato y convertir el resultado a String
        String costoPorGramoFormateado = df.format(costoPorGramo);

        // Convertir el String resultante de nuevo a double si es necesario
        double costoPorGramoRedondeado = Double.parseDouble(costoPorGramoFormateado);
        return costoPorGramoRedondeado;
    }

    public Double calcularCostoProduccionTotal(Integer idProductoTerminado) {
        List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminadoList =
                materiaPrimaProductoTerminadoRepository.getAllByProductoTerminado(
                        productoTerminadoRepository.findById(idProductoTerminado).get()
                );

        double costoProduccionTotal = 0.0;

        for (MateriaPrima_ProductoTerminado mpt : materiaPrimaProductoTerminadoList) {
            MateriaPrima materiaPrima = mpt.getInventario().getMateriaPrima();
            double costoUnitario = materiaPrima.getCostoUnitario()/1000;
            double cantidad = mpt.getCantidad();

            // Convertir la cantidad a gramos si es menor que 1 (se asume que está en kilogramos)
            if (cantidad < 1) {
                cantidad *= 1000; // Convertir a gramos
            }

            costoProduccionTotal += cantidad * costoUnitario;
        }

        return costoProduccionTotal;
    }

    private double rendondearADos(double valor){
        // Crear un objeto DecimalFormat para redondear a 4 decimales
        DecimalFormat df = new DecimalFormat("#.####");

        // Aplicar el formato y convertir el resultado a String
        String costoPorGramoFormateado = df.format(valor);

        // Convertir el String resultante de nuevo a double si es necesario
        double costoPorGramoRedondeado = Double.parseDouble(costoPorGramoFormateado);
        return costoPorGramoRedondeado;

    }

    private boolean validarCategoriaId(int idCategoria) {
        Optional<Categoria> categoriaOptional= categoriaRepository.findById(idCategoria);
        return categoriaOptional.isPresent();

    }

}
