package com.residencia.restaurante.proyecto.service;

import java.io.ByteArrayInputStream;
import java.util.Map;

public interface IReporteService {
    public ByteArrayInputStream generarReporteProductosAgotandose(Map<String,String> objetoMap);
}
