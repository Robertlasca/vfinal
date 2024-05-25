package com.residencia.restaurante.security.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.print.*;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class PrintServer {
/*
    private static final int PORT = 9100;

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Servidor de impresión iniciado en el puerto " + PORT);
                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        System.out.println("Conexión entrante desde " + clientSocket.getInetAddress());

                        // Leer datos de impresión desde el cliente
                        InputStream inputStream = clientSocket.getInputStream();
                        byte[] printData = inputStream.readAllBytes();

                        // Seleccionar impresora predeterminada
                        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
                        PrintService selectedService = null;
                        for (PrintService service : printServices) {
                            if (service.getName().contains("POS-58") || service.getName().contains("USB001")) {
                                selectedService = service;
                                break;
                            }
                        }

                        if (selectedService == null) {
                            System.out.println("No se encontró la impresora POS-58 en el puerto USB001.");
                            continue;
                        }

                        // Crear un trabajo de impresión
                        DocPrintJob printJob = selectedService.createPrintJob();
                        Doc doc = new SimpleDoc(printData, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
                        printJob.print(doc, null);
                        System.out.println("Trabajo de impresión enviado a " + selectedService.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }*/
}