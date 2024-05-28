package com.residencia.restaurante.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.util.Map;

@RestController
@RequestMapping("/print")
public class PrintServer {

    @PostMapping
    public ResponseEntity<String> print(@RequestBody Map<String, String> printRequest) {
        try {
            //String ticketContent = printRequest.get("ticketContent");

            String printerName = printRequest.get("printerName");
            String usbPort = printRequest.get("usbPort");


            System.out.println(printRequest.get("ticketContent"));
            // Lógica para imprimir el ticket
            boolean success = printTicket(printRequest.get("ticketContent"), printerName, usbPort);

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

    private boolean printTicket(String ticketContent, String printerName, String usbPort) {
        try {
            // Buscar la impresora por nombre o puerto USB
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService selectedService = null;

            for (PrintService service : printServices) {
                if (service.getName().equalsIgnoreCase(printerName) || service.getName().contains(usbPort)) {
                    selectedService = service;
                    break;
                }
            }

            if (selectedService == null) {
                System.out.println("No se encontró la impresora con el nombre especificado o el puerto USB.");
                return false;
            }

            // Configurar el trabajo de impresión
            DocPrintJob job = selectedService.createPrintJob();
            byte[] bytes = ticketContent.getBytes();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

            // Crear un listener para esperar hasta que la impresión se complete
            PrintJobWatcher pjw = new PrintJobWatcher(job);
            job.print(doc, pras);
            pjw.waitForDone();
            return true;

        } catch (PrintException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Clase auxiliar para esperar a que el trabajo de impresión se complete
    class PrintJobWatcher {
        private boolean done = false;

        PrintJobWatcher(DocPrintJob job) {
            job.addPrintJobListener(new PrintJobAdapter() {
                @Override
                public void printJobCompleted(PrintJobEvent pje) {
                    allDone();
                }

                @Override
                public void printJobFailed(PrintJobEvent pje) {
                    allDone();
                }

                @Override
                public void printJobCanceled(PrintJobEvent pje) {
                    allDone();
                }

                @Override
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    allDone();
                }

                private void allDone() {
                    synchronized (PrintJobWatcher.this) {
                        done = true;
                        PrintJobWatcher.this.notify();
                    }
                }
            });
        }

        public synchronized void waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
