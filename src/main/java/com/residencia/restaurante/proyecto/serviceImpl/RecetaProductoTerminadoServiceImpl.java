package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IRecetaProductoTerminadoService;
import com.residencia.restaurante.security.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecetaProductoTerminadoServiceImpl implements IRecetaProductoTerminadoService {
    @Autowired
    IMateriaPrima_ProductoTerminadoRepository materiaPrimaProductoTerminadoRepository;

    @Autowired
    IProductoTerminado_MenuRepository productoTerminadoMenuRepository;
    @Autowired
    IProductoTerminadoRepository productoTerminadoRepository;

    @Autowired
    IMenuRepository menuRepository;

    @Autowired
            private IMateriaPrimaRepository materiaPrimaRepository;

    MenuServiceImpl menuServiceImp;
    @Override
    public ResponseEntity<String> eliminarIngrediente(Integer id) {
        try {
            Optional<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminadoOptional= materiaPrimaProductoTerminadoRepository.findById(id);
            if(materiaPrimaProductoTerminadoOptional.isPresent()){

                MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado=materiaPrimaProductoTerminadoOptional.get();
                Integer idProducto= materiaPrimaProductoTerminado.getProductoTerminado().getId();
                Optional<ProductoTerminado> productoTerminadoOptional= productoTerminadoRepository.findById(idProducto);

                if(productoTerminadoOptional.isPresent()){

                    ProductoTerminado productoTerminado= productoTerminadoOptional.get();
                    materiaPrimaProductoTerminadoRepository.delete(materiaPrimaProductoTerminado);
                    List<ProductoTerminado_Menu> productoTerminadoMenus= productoTerminadoMenuRepository.getAllByProductoTerminado(productoTerminado);

                    if(!productoTerminadoMenus.isEmpty()){
                        for (ProductoTerminado_Menu productoMenu:productoTerminadoMenus) {
                            Menu menu= productoMenu.getMenu();
                            menu.setCostoProduccionDirecto(menuServiceImp.calcularCostoTotalMenu(menu.getId()));
                            menuRepository.save(menu);
                        }

                    }
                    return Utils.getResponseEntity("Ingrediente eliminado correctamente.",HttpStatus.OK);
                }

            }
            return Utils.getResponseEntity("No existe el registro.",HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> editarIngredienteReceta(Map<String, String> objetoMap) {
        try {

            if(objetoMap.containsKey("id") && objetoMap.containsKey("cantidad")){
                Integer id= Integer.parseInt(objetoMap.get("id"));
                Optional<MateriaPrima_ProductoTerminado> materiaPrimaProductoTerminadoOptional= materiaPrimaProductoTerminadoRepository.findById(id);
                if(materiaPrimaProductoTerminadoOptional.isPresent()){
                    MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado= materiaPrimaProductoTerminadoOptional.get();
                    materiaPrimaProductoTerminado.setCantidad(Double.parseDouble(objetoMap.get("cantidad")));
                    materiaPrimaProductoTerminadoRepository.save(materiaPrimaProductoTerminado);

                    Integer idProducto= materiaPrimaProductoTerminado.getProductoTerminado().getId();
                    Optional<ProductoTerminado> productoTerminadoOptional= productoTerminadoRepository.findById(idProducto);

                    if(productoTerminadoOptional.isPresent()){

                        ProductoTerminado productoTerminado= productoTerminadoOptional.get();
                        List<ProductoTerminado_Menu> productoTerminadoMenus= productoTerminadoMenuRepository.getAllByProductoTerminado(productoTerminado);

                        if(!productoTerminadoMenus.isEmpty()){
                            for (ProductoTerminado_Menu productoMenu:productoTerminadoMenus) {
                                Menu menu= productoMenu.getMenu();
                                menu.setCostoProduccionDirecto(menuServiceImp.calcularCostoTotalMenu(menu.getId()));
                                menuRepository.save(menu);
                            }
                            return Utils.getResponseEntity("Receta actualizada correctamente.",HttpStatus.OK);

                        }
                        return Utils.getResponseEntity("Receta actualizada correctamente.",HttpStatus.OK);
                    }

                    return Utils.getResponseEntity("Receta actualizada correctamente.",HttpStatus.OK);

                }
                return Utils.getResponseEntity("No existe el registro.",HttpStatus.BAD_REQUEST);

            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> agregarIngredienteReceta(Map<String, String> objetoMap) {
        try {
            if(objetoMap.containsKey("idMateria") && objetoMap.containsKey("idProducto") && objetoMap.containsKey("cantidad")){
                Optional<MateriaPrima> materiaPrimaOptional= materiaPrimaRepository.findById(Integer.parseInt(objetoMap.get("idMateria")));
                Optional<ProductoTerminado> productoTerminadoOptional=productoTerminadoRepository.findById(Integer.parseInt(objetoMap.get("idProducto")));
                if(materiaPrimaOptional.isPresent() && productoTerminadoOptional.isPresent()){
                    MateriaPrima_ProductoTerminado materiaPrimaProductoTerminado= new MateriaPrima_ProductoTerminado();
                    materiaPrimaProductoTerminado.setMateriaPrima(materiaPrimaOptional.get());
                    materiaPrimaProductoTerminado.setProductoTerminado(productoTerminadoOptional.get());
                    materiaPrimaProductoTerminado.setCantidad(Double.parseDouble(objetoMap.get("cantidad")));
                    materiaPrimaProductoTerminadoRepository.save(materiaPrimaProductoTerminado);

                    ProductoTerminado productoTerminado= productoTerminadoOptional.get();
                    List<ProductoTerminado_Menu> productoTerminadoMenus= productoTerminadoMenuRepository.getAllByProductoTerminado(productoTerminado);

                    if(!productoTerminadoMenus.isEmpty()){
                        for (ProductoTerminado_Menu productoMenu:productoTerminadoMenus) {
                            Menu menu= productoMenu.getMenu();
                            menu.setCostoProduccionDirecto(menuServiceImp.calcularCostoTotalMenu(menu.getId()));
                            menuRepository.save(menu);
                        }
                        return Utils.getResponseEntity("Ingrediente agregado correctamente.",HttpStatus.OK);

                    }
                    return Utils.getResponseEntity("Ingrediente agregado correctamente.",HttpStatus.OK);


                }
                return Utils.getResponseEntity("No existen los registros.",HttpStatus.BAD_REQUEST);
            }
            return Utils.getResponseEntity(Constantes.INVALID_DATA,HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return Utils.getResponseEntity(Constantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
