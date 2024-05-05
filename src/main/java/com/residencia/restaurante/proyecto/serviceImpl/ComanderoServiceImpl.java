package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.constantes.Constantes;
import com.residencia.restaurante.proyecto.dto.ProductoDto;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.*;
import com.residencia.restaurante.proyecto.service.IComanderoService;
import com.residencia.restaurante.security.model.Usuario;
import com.residencia.restaurante.security.repository.IUsuarioRepository;
import com.residencia.restaurante.security.utils.Utils;
import org.apache.tomcat.util.bcel.Const;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private IUsuarioRepository usuarioRepository;
    @Autowired
    private IMesaRepository mesaRepository;

    @Autowired
    private IProductoNormalRepository productoNormalRepository;
    @Autowired
    private IMenuRepository menuRepository;
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
               orden.setEstado("Abierto");
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
        return null;
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
            if(objetoMap.containsKey("id") && objetoMap.containsKey("isMenu")){
                Integer id= Integer.parseInt(objetoMap.get("id"));
                ProductoDto productoDto= new ProductoDto();
                if(objetoMap.get("isMenu").equalsIgnoreCase("true")){
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
                return new ResponseEntity<ProductoDto>(productoDto,HttpStatus.BAD_REQUEST);

            }
            return new ResponseEntity<ProductoDto>(new ProductoDto(),HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<ProductoDto>(new ProductoDto(),HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
