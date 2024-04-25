package com.residencia.restaurante.proyecto.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.IngredienteProductoTerminado;
import com.residencia.restaurante.proyecto.dto.ProductoTerminadoDto;
import com.residencia.restaurante.proyecto.dto.RecetaDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IProductoTerminadoService;
import com.residencia.restaurante.proyecto.wrapper.IngredientesProductoTerminadoWrapper;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoTerminadoServiceImpl implements IProductoTerminadoService {
    @Autowired
    private IProductoTerminadoRepository productoTerminadoRepository;

    @Autowired
    private IMateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private IInventarioRepository inventarioRepository;

    @Autowired
    private ICategoriaRepository categoriaRepository;
    @Autowired
    private  UploadFileService uploadFileService;

    @Autowired
    private IMateriaPrima_ProductoTerminadoRepository materiaPrimaProductoTerminadoRepository;


    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerNoActivos() {
        return null;
    }

    @Override
    public ResponseEntity<List<ProductoTerminadoDto>> obtenerTodos() {
        try {
            List<ProductoTerminado> listaMenor= productoTerminadoRepository.findProductoTerminadoByStockActualMenorAlMinimo();

            List<ProductoTerminado> listMayor= productoTerminadoRepository.findProductoTerminadoByStockActualMayorAlMaximo();

            List<ProductoTerminado> listSuficiente= productoTerminadoRepository.findProductoTerminadoByStockActualEntreMinimoYMaximo();

            List<ProductoTerminadoDto> terminadoDtoList= new ArrayList<>();
            double costo=0;

            for (ProductoTerminado productoTerminado: listaMenor) {

                ProductoTerminadoDto productoTerminadoDto= new ProductoTerminadoDto();
                List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados= materiaPrimaProductoTerminadoRepository.getAllByProductoTerminado(productoTerminado);
                productoTerminadoDto.setProductoTerminado(productoTerminado);
                if(productoTerminado.isVisibilidad()){
                    productoTerminadoDto.setDisponibilidad("Visible");
                }else {
                    productoTerminadoDto.setDisponibilidad("No visible");
                }
                productoTerminadoDto.setCostoProduccion(calcularCosto(productoTerminado));

                productoTerminadoDto.setEstado("Insuficiente");
                terminadoDtoList.add(productoTerminadoDto);


            }

            for (ProductoTerminado productoTerminado: listMayor) {

                ProductoTerminadoDto productoTerminadoDto= new ProductoTerminadoDto();
                productoTerminadoDto.setProductoTerminado(productoTerminado);
                if(productoTerminado.isVisibilidad()){
                    productoTerminadoDto.setDisponibilidad("Visible");
                }else {
                    productoTerminadoDto.setDisponibilidad("No visible");
                }
                productoTerminadoDto.setCostoProduccion(calcularCosto(productoTerminado));


                productoTerminadoDto.setEstado("Excedido");
                terminadoDtoList.add(productoTerminadoDto);


            }

            for (ProductoTerminado productoTerminado: listSuficiente) {

                ProductoTerminadoDto productoTerminadoDto= new ProductoTerminadoDto();
                productoTerminadoDto.setProductoTerminado(productoTerminado);
                if(productoTerminado.isVisibilidad()){
                    productoTerminadoDto.setDisponibilidad("Visible");
                }else {
                    productoTerminadoDto.setDisponibilidad("No visible");
                }
                productoTerminadoDto.setCostoProduccion(calcularCosto(productoTerminado));

                productoTerminadoDto.setEstado("Suficiente");
                terminadoDtoList.add(productoTerminadoDto);


            }

            return new ResponseEntity<List<ProductoTerminadoDto>>(terminadoDtoList,HttpStatus.OK);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<ProductoTerminadoDto>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);



    }

    private double calcularCosto(ProductoTerminado productoTerminado) {
        List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados= materiaPrimaProductoTerminadoRepository.getAllByProductoTerminado(productoTerminado);
        double costoTotal=0;
        if(!materiaPrimaProductoTerminados.isEmpty()){
            for (MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado:materiaPrimaProductoTerminados) {
                MateriaPrima materiaPrima= materiaPrimaProductoTerminado.getInventario().getMateriaPrima();
                double costo= calcularCostoProduccion(materiaPrima.getCostoUnitario());
                costoTotal=costoTotal+rendondearADos((materiaPrimaProductoTerminado.getCantidad()*1000)*costo);




            }
        }
        return rendondearADos(costoTotal);

    }

    @Override
    public ResponseEntity<String> agregar(String nombre, String unidadMedida, String descripcion, double stockMax, double stockMin, MultipartFile file, int idCategoria, String materias) {
        try {
            if(!productoTerminadoRepository.existsByNombreLikeIgnoreCase(nombre)){


            if(validarCategoriaId(idCategoria)){
                ProductoTerminado productoTerminado= new ProductoTerminado();
                Optional<Categoria> categoriaOptional= categoriaRepository.findById(idCategoria);
                categoriaOptional.ifPresent(productoTerminado::setCategoria);

                productoTerminado.setNombre(nombre);
                productoTerminado.setDescripcion(descripcion);
                productoTerminado.setUnidadMedida(unidadMedida);
                productoTerminado.setVisibilidad(true);
                productoTerminado.setStockMax(stockMax);
                productoTerminado.setStockMin(stockMin);
                productoTerminado.setStockActual(0);

                String nombreImagen=uploadFileService.guardarImagen(file);
                productoTerminado.setImagen(nombreImagen);

                productoTerminadoRepository.save(productoTerminado);

                //Asignar materias primas
                ObjectMapper objectMapper=new ObjectMapper();
                try {
                    List<IngredientesProductoTerminadoWrapper> ingredientesProductoTerminadoWrapperList=objectMapper.readValue(materias, new TypeReference<List<IngredientesProductoTerminadoWrapper>>() {});
                    if(!ingredientesProductoTerminadoWrapperList.isEmpty()){
                        for(IngredientesProductoTerminadoWrapper ingrediente: ingredientesProductoTerminadoWrapperList){
                            MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado= new MateriaPrima_ProductoTerminado();
                            materiaPrimaProductoTerminado.setCantidad(ingrediente.getCantidad());
                            materiaPrimaProductoTerminado.setProductoTerminado(productoTerminado);

                            Optional<MateriaPrima> materiaPrimaOptional= materiaPrimaRepository.findById(ingrediente.getIdMateriaPrima());
                            Optional<Inventario> inventarioOptional=inventarioRepository.findById(ingrediente.getIdMateriaPrima());

                            if(inventarioOptional.isPresent()){
                                Inventario inventario= inventarioOptional.get();
                                materiaPrimaProductoTerminado.setInventario(inventario);
                                //materiaPrimaProductoTerminado.setMateriaPrima(materiaPrima);
                            }

                            materiaPrimaProductoTerminadoRepository.save(materiaPrimaProductoTerminado);

                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                return Utils.getResponseEntity("Producto terminado guardado correctamente.",HttpStatus.OK);


            }
            return Utils.getResponseEntity("Error al asignar la categoría.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity("Ya existe un producto con este nombre.",HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validarCategoriaId(int idCategoria) {
        Optional<Categoria> categoriaOptional= categoriaRepository.findById(idCategoria);
        return categoriaOptional.isPresent();

    }

    @Override
    public ResponseEntity<IngredienteProductoTerminado> obtenerMateriaPrimaId(Integer id) {
        try {
            Optional<Inventario> inventarioOptional= inventarioRepository.findById(id);
            if(inventarioOptional.isPresent()){

                MateriaPrima materiaPrima= inventarioOptional.get().getMateriaPrima();
                IngredienteProductoTerminado ingredienteProductoTerminado= new IngredienteProductoTerminado();
                //Se debe calcular el costo de produccion por gramo
                double precio= materiaPrima.getCostoUnitario();

                ingredienteProductoTerminado.setId(inventarioOptional.get().getId());
                ingredienteProductoTerminado.setNombre(materiaPrima.getNombre());
                ingredienteProductoTerminado.setUnidadMedida(materiaPrima.getUnidadMedida());
                ingredienteProductoTerminado.setCostoProduccion(calcularCostoProduccion(precio));
                return new ResponseEntity<IngredienteProductoTerminado>(ingredienteProductoTerminado,HttpStatus.BAD_REQUEST);

            }
            return new ResponseEntity<IngredienteProductoTerminado>(new IngredienteProductoTerminado(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<IngredienteProductoTerminado>(new IngredienteProductoTerminado(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoTerminado> obtenerProductoTerminado(Integer id) {
        try {
            Optional<ProductoTerminado> productoTerminadoOptional= productoTerminadoRepository.findById(id);
            if(productoTerminadoOptional.isPresent()){
                ProductoTerminado productoTerminado= new ProductoTerminado();
                return new ResponseEntity<ProductoTerminado>(productoTerminado,HttpStatus.OK);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ProductoTerminado>(new ProductoTerminado(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<RecetaDTO>> obtenerReceta(Integer id) {
        try {
            Optional<ProductoTerminado> optionalProductoTerminado= productoTerminadoRepository.findById(id);
            System.out.println("Este es el costo de produccion final."+calcularCostoProduccionTotal(id));
            List<RecetaDTO> recetaDTOS= new ArrayList<>();
            if(optionalProductoTerminado.isPresent()){
                ProductoTerminado productoTerminado=optionalProductoTerminado.get();
                List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados= materiaPrimaProductoTerminadoRepository.getAllByProductoTerminado(productoTerminado);
                if(!materiaPrimaProductoTerminados.isEmpty()){
                    for (MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado:materiaPrimaProductoTerminados) {
                        MateriaPrima materiaPrima= materiaPrimaProductoTerminado.getInventario().getMateriaPrima();
                        double costo= calcularCostoProduccion(materiaPrima.getCostoUnitario());
                        RecetaDTO recetaDTO= new RecetaDTO();
                        recetaDTO.setEsIngrediente(true);
                        recetaDTO.setId(materiaPrima.getId());
                        recetaDTO.setUnidadMedida(materiaPrima.getUnidadMedida());
                        recetaDTO.setNombre(materiaPrima.getNombre());
                        recetaDTO.setCantidad(materiaPrimaProductoTerminado.getCantidad());
                        //Costo de produccion ejemplo que la materia prima ocupe 0.200
                        recetaDTO.setCostoProduccion(rendondearADos((materiaPrimaProductoTerminado.getCantidad()*1000)*costo));

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
    public ResponseEntity<String> eliminar(Integer id) {
        try {
            Optional<ProductoTerminado> productoTerminadoOptional= productoTerminadoRepository.findById(id);
            if(productoTerminadoOptional.isPresent()){
                List<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminados=materiaPrimaProductoTerminadoRepository.getAllByProductoTerminado(productoTerminadoOptional.get());
                materiaPrimaProductoTerminadoRepository.deleteAll(materiaPrimaProductoTerminados);
                productoTerminadoRepository.delete(productoTerminadoOptional.get());
                return Utils.getResponseEntity("Producto terminado elimnado correctamente",HttpStatus.OK);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
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

    private double rendondearADos(double valor){
        // Crear un objeto DecimalFormat para redondear a 4 decimales
        DecimalFormat df = new DecimalFormat("#.####");

        // Aplicar el formato y convertir el resultado a String
        String costoPorGramoFormateado = df.format(valor);

        // Convertir el String resultante de nuevo a double si es necesario
        double costoPorGramoRedondeado = Double.parseDouble(costoPorGramoFormateado);
        return costoPorGramoRedondeado;

    }

    // Método para calcular el costo de producción total de un producto terminado
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
}
