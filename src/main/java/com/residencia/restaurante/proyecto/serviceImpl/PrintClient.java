package com.residencia.restaurante.proyecto.serviceImpl;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.Socket;

@Service
public class PrintClient {


    private String serverAddress="192.168.5.102";


    private int port=9100;

    public void sendPrintJob(byte[] printData) throws Exception {
        try (Socket socket = new Socket(serverAddress, port)) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(printData);
            outputStream.flush();
            System.out.println("Datos de impresión enviados al servidor.");
        }
    }
}