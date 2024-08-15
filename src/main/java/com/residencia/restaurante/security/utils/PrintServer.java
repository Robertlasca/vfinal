package com.residencia.restaurante.security.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
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
            System.out.println(ticketContent+"Contenido");

            if(printRequest.containsKey("nombreIm")){
                System.out.println("aqui estoy ");
                String nombre = printRequest.get("nombreIm");
                if(printTicketByName(ticketContent,nombre)){
                    System.out.println("Entre");
                    return new ResponseEntity<>("Impreso correctamente", HttpStatus.OK);
                }
            }

            if(printRequest.containsKey("printerIp")){
                String printerIp = printRequest.get("printerIp");
                if(printTicketByIp(ticketContent,printerIp, 9100)){
                    System.out.println("Si se imprimio");
                    return new ResponseEntity<>("Impreso correctamente", HttpStatus.OK);
                }
            }

                return new ResponseEntity<>("Error al imprimir", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al imprimir: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean printTicketByIp(String ticketContent, String printerIp, int printerPort) {
        System.out.println("Ahora estoy aqui");
        try (Socket socket = new Socket(printerIp, printerPort)) {
            OutputStream out = socket.getOutputStream();
            out.write(ticketContent.getBytes());
            out.flush();
            System.out.println("Entre");
            // Aquí enviamos el comando de corte automático
            String cutCommand = "\n" + (char)29 + (char)86 + (char)66 + (char)0;
            out.write(cutCommand.getBytes());
            out.flush();

            return true;
        } catch (Exception e) {
            // Opcional: Puedes registrar el error en un log en lugar de imprimirlo
            // Logger.getLogger(TuClase.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Retorne false ");
            return false;
        }
    }


    private boolean printTicketByName(String ticketContent, String printerName) {
        try {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, pras);
            System.out.println("Entre1");
            PrintService selectedService = null;
            for (PrintService printService : printServices) {
                if (printService.getName().equalsIgnoreCase(printerName)) {
                    System.out.println("Entre2");
                    selectedService = printService;
                    System.out.println("Sigooo");
                    break;
                }
            }
            if (selectedService==null){
                return false;
            }

            System.out.println("pase");

            // Comando de corte automático (puede variar según el modelo de la impresora)
            String cutCommand = "\n" + (char)29 + (char)86 + (char)66 + (char)0;

            // Concatenar el comando de corte automático al contenido del ticket
            String finalContent = ticketContent + cutCommand;

            DocPrintJob job = selectedService.createPrintJob();
            Doc doc = new SimpleDoc(finalContent.getBytes(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

            return true;
        } catch (Exception e) {
            System.out.println("Entre al catch");
            return false;
        }
    }

}