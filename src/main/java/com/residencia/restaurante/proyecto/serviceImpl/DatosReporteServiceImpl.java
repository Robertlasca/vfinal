package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.dto.TotalVentasDTO;
import com.residencia.restaurante.proyecto.dto.VentasDTO;
import com.residencia.restaurante.proyecto.entity.*;
import com.residencia.restaurante.proyecto.repository.IAlmacenRepository;
import com.residencia.restaurante.proyecto.repository.IDetalleOrden_MenuRepository;
import com.residencia.restaurante.proyecto.repository.IInventarioRepository;
import com.residencia.restaurante.proyecto.repository.IVentaRepository;
import com.residencia.restaurante.proyecto.service.IDatosReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

@Service
public class DatosReporteServiceImpl implements IDatosReporteService {
    @Autowired
    private IInventarioRepository inventarioRepository;

    @Autowired
    private IAlmacenRepository almacenRepository;

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IDetalleOrden_MenuRepository detalleOrdenMenuRepository;

    @Override
    public ResponseEntity<List<Inventario>> obtenerInventarioAgotadoXAlmacen(Integer id) {

        try {
            Optional<Almacen> almacenOptional=almacenRepository.findById(id);
            if(almacenOptional.isPresent()){
                List<Inventario> inventarios=inventarioRepository.inventariosInsuficientePorAlmacen(almacenOptional.get().getId());
                return new ResponseEntity<List<Inventario>>(inventarios,HttpStatus.OK);
            }
            return new ResponseEntity<List<Inventario>>(new ArrayList<>(), HttpStatus.BAD_REQUEST);


        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<List<Inventario>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> obtenerDatosVentasXDia(String dia) {
        try {
            List<VentasDTO> ventasDTOS=new ArrayList<>();
            ZoneId zoneIdMexico = ZoneId.of("America/Mexico_City");

            LocalDate today = LocalDate.now(zoneIdMexico);
            List<Venta> ventaList=ventaRepository.findVentasPorDia(LocalDate.parse(dia));

            if(!ventaList.isEmpty()){
                TotalVentasDTO totalVentasDTO=new TotalVentasDTO();
                for (Venta venta:ventaList
                     ) {
                    VentasDTO ventasDTO= new VentasDTO();
                    ventasDTO.setId(venta.getId());
                    ventasDTO.setCliente(venta.getOrden().getNombreCliente());
                    ventasDTO.setComentario(venta.getComentario());
                    ventasDTO.setMesa(venta.getOrden().getMesa().getNombre());
                    ventasDTO.setEstado(venta.getEstado());
                    ventasDTO.setAreaServicio(venta.getOrden().getMesa().getAreaServicio().getNombre());
                    ventasDTO.setUsuario(venta.getUsuario().getNombre());
                    ventasDTO.setDescuento(venta.getDescuento());
                    ventasDTO.setFechaHora(venta.getFechaHoraConsolidacion());
                    ventasDTO.setFechaHoraApertura(venta.getOrden().getFechaHoraApertura());
                    ventasDTO.setTotalPagar(venta.getTotalPagar());
                    ventasDTO.setSubTotal(venta.getSubTotal());
                    ventasDTO.setOrdenId(venta.getOrden().getId());
                    ventasDTO.setHora(String.valueOf(venta.getOrden().getFechaHoraApertura().getHour()));
                    ventasDTOS.add(ventasDTO);
                }
                totalVentasDTO.setLista(ventasDTOS);
                String platilloMasVendido= obtenerPlatilloMasVendido(ventaList);
                double totalVentas= ventaList.stream().mapToDouble(Venta::getTotalPagar).sum();
                totalVentasDTO.setPlatilloMasVendido(platilloMasVendido);
                totalVentasDTO.setMontoTotal(totalVentas);
                return new ResponseEntity<TotalVentasDTO>(totalVentasDTO,HttpStatus.OK);

            }

            return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> obtenerDatosVentasXMes(String diaInicio, String diaFin) {
        try {
            List<VentasDTO> ventasDTOS=new ArrayList<>();
            ZoneId zoneIdMexico = ZoneId.of("America/Mexico_City");

            LocalDate today = LocalDate.now(zoneIdMexico);
            System.out.println(diaInicio);
            System.out.println(diaFin);
            List<Venta> ventaList=ventaRepository.findByFechaBetween(LocalDate.parse(diaInicio),LocalDate.parse(diaFin));

            if(!ventaList.isEmpty()){
                TotalVentasDTO totalVentasDTO=new TotalVentasDTO();
                for (Venta venta:ventaList
                ) {
                    VentasDTO ventasDTO= new VentasDTO();
                    ventasDTO.setId(venta.getId());
                    ventasDTO.setCliente(venta.getOrden().getNombreCliente());
                    ventasDTO.setComentario(venta.getComentario());
                    ventasDTO.setMesa(venta.getOrden().getMesa().getNombre());
                    ventasDTO.setEstado(venta.getEstado());
                    ventasDTO.setAreaServicio(venta.getOrden().getMesa().getAreaServicio().getNombre());
                    ventasDTO.setUsuario(venta.getUsuario().getNombre());
                    ventasDTO.setDescuento(venta.getDescuento());
                    ventasDTO.setFechaHora(venta.getFechaHoraConsolidacion());
                    ventasDTO.setFechaHoraApertura(venta.getOrden().getFechaHoraApertura());
                    ventasDTO.setTotalPagar(venta.getTotalPagar());
                    ventasDTO.setSubTotal(venta.getSubTotal());
                    ventasDTO.setOrdenId(venta.getOrden().getId());
                    ventasDTO.setHora(String.valueOf(venta.getOrden().getFechaHoraApertura().getHour()));
                    ventasDTOS.add(ventasDTO);
                }
                totalVentasDTO.setLista(ventasDTOS);
                String platilloMasVendido= obtenerPlatilloMasVendido(ventaList);
                double totalVentas= ventaList.stream().mapToDouble(Venta::getTotalPagar).sum();
                totalVentasDTO.setPlatilloMasVendido(platilloMasVendido);
                totalVentasDTO.setMontoTotal(totalVentas);
                return new ResponseEntity<TotalVentasDTO>(totalVentasDTO,HttpStatus.OK);

            }

            return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TotalVentasDTO> obterDatosVentasMes(String mes) {

        try {
            YearMonth yearMonth = YearMonth.parse(mes);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            System.out.println("Wnte");
            List<Venta> ventaList = ventaRepository.findByFechaBetween(startDate, endDate);
            List<VentasDTO> ventasDTOS=new ArrayList<>();
            if(!ventaList.isEmpty()){
                TotalVentasDTO totalVentasDTO=new TotalVentasDTO();
                for (Venta venta:ventaList
                ) {
                    VentasDTO ventasDTO= new VentasDTO();
                    ventasDTO.setId(venta.getId());
                    ventasDTO.setCliente(venta.getOrden().getNombreCliente());
                    ventasDTO.setComentario(venta.getComentario());
                    ventasDTO.setMesa(venta.getOrden().getMesa().getNombre());
                    ventasDTO.setEstado(venta.getEstado());
                    ventasDTO.setAreaServicio(venta.getOrden().getMesa().getAreaServicio().getNombre());
                    ventasDTO.setUsuario(venta.getUsuario().getNombre());
                    ventasDTO.setDescuento(venta.getDescuento());
                    ventasDTO.setFechaHora(venta.getFechaHoraConsolidacion());
                    ventasDTO.setFechaHoraApertura(venta.getOrden().getFechaHoraApertura());
                    ventasDTO.setTotalPagar(venta.getTotalPagar());
                    ventasDTO.setSubTotal(venta.getSubTotal());
                    ventasDTO.setOrdenId(venta.getOrden().getId());
                    ventasDTO.setHora(String.valueOf(venta.getOrden().getFechaHoraApertura().getHour()));
                    ventasDTOS.add(ventasDTO);
                }
                totalVentasDTO.setLista(ventasDTOS);
                String platilloMasVendido= obtenerPlatilloMasVendido(ventaList);
                double totalVentas= ventaList.stream().mapToDouble(Venta::getTotalPagar).sum();
                totalVentasDTO.setPlatilloMasVendido(platilloMasVendido);
                totalVentasDTO.setMontoTotal(totalVentas);
                return new ResponseEntity<TotalVentasDTO>(totalVentasDTO,HttpStatus.OK);

            }

            return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<TotalVentasDTO>(new TotalVentasDTO(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String obtenerPlatilloMasVendido(List<Venta> ventaList) {
        if(!ventaList.isEmpty()){
            Map<String,Integer> conteoPlatillos= new HashMap<>();
            for (Venta venta:ventaList) {
                Orden orden= venta.getOrden();
                List<DetalleOrdenMenu> list= detalleOrdenMenuRepository.getAllByOrden(orden);
                for (DetalleOrdenMenu detalleOrdenMenu:list) {
                    String nombrePlatillo= detalleOrdenMenu.getNombreMenu();
                    if(conteoPlatillos.containsKey(nombrePlatillo)){
                        conteoPlatillos.put(nombrePlatillo,conteoPlatillos.get(nombrePlatillo)+detalleOrdenMenu.getCantidad());

                    }else {
                        conteoPlatillos.put(nombrePlatillo,detalleOrdenMenu.getCantidad());
                    }

                }



            }

            String platilloMasVendio=null;
            int cantidadMasVendida=0;

            for (Map.Entry<String,Integer> entry: conteoPlatillos.entrySet()) {
                if(entry.getValue()>cantidadMasVendida){
                    platilloMasVendio=entry.getKey();
                    cantidadMasVendida=entry.getValue();
                }
            }

            if(platilloMasVendio!=null){
                return platilloMasVendio;
            }

        }
        return "Ninguno";
    }
}
