package com.residencia.restaurante.security.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

@RestController
@RequestMapping("/print")
public class PrintServer {

    @PostMapping
    public ResponseEntity<String> print(@RequestBody Map<String, String> printRequest) {
        try {
            String ticketContent = printRequest.get("ticketContent");
            String printerIp = printRequest.get("printerIp");

            // Lógica para imprimir el ticket
            boolean success = printTicket(ticketContent, printerIp, 9100);

            if (success) {
                return new ResponseEntity<>("Impreso correctamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error al imprimir", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al imprimir: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean printTicket(String ticketContent, String printerIp, int printerPort) {
        try (Socket socket = new Socket(printerIp, printerPort)) {
            OutputStream out = socket.getOutputStream();
            out.write(ticketContent.getBytes());
            out.flush();



            // Aquí enviamos el comando de corte automático
            // Esto puede variar dependiendo del modelo y fabricante de la impresora
            String cutCommand = "\n" + (char)29 + (char)86 + (char)66 + (char)0;
            out.write(cutCommand.getBytes());

            out.flush();

            // Cerrar el OutputStream y el socket
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}