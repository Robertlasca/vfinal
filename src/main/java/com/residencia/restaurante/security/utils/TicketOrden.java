package com.residencia.restaurante.security.utils;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TicketOrden {

    private String contentTicket;

    public TicketOrden(String folio, String cashierBox, String cashier, String customerName, List<String[]> products,
                         String subtotal, String discount, String numOfProducts, String total, String received, String change,
                         String paymentMethod, String paymentAmount) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());

        StringBuilder productsStringBuilder = new StringBuilder();
        for (String[] product : products) {
            String quantity = product[0];
            String productName = product[1];
            String amount = product[2];
            productsStringBuilder.append(String.format("%-10s %-10s %5s\n", quantity, productName, amount));
        }

        this.contentTicket =
                "Plazita Gourmet\n" +
                        "Priv Miguel Hidalgo 16 COLONIA Primera Seccion\n" +
                        "Municipio San Pablo Etla C.P 68258\n\n" +
                        String.format("Fecha: %-40s Hora: %s\n", date, time) +
                        String.format("Folio: %-40s Caja: %s\n", folio, cashierBox) +
                        String.format("Cajero: %s\n", cashier) +
                        String.format("Cliente: %s\n\n", customerName) +
                        "=============================\n" +
                        "PRODUCTOS\n" +
                        "=============================\n" +
                        String.format("%-10s %-10s %5s\n", "Cant", "Nombre", "Importe") +
                        "------------------------------\n" +
                        productsStringBuilder.toString() +
                        "=============================\n" +
                        String.format("Subtotal: %s\n", subtotal) +
                        String.format("Descuento: %s\n", discount) +
                        String.format("N° de productos: %s\n\n", numOfProducts) +
                        String.format("Total: %s\n", total) +
                        String.format("Recibido: %s\n", received) +
                        String.format("Cambio: %s\n\n", change) +
                        String.format("Pagado con: %s  Cantidad: %s\n\n", paymentMethod, paymentAmount) +
                        "No es un comprobante con valor fiscal.\n" +
                        "¡Gracias por su compra!\n";
    }

    public void print(PrintService selectedService) {
        try {
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            byte[] bytes = this.contentTicket.getBytes();
            Doc doc = new SimpleDoc(bytes, flavor, null);
            DocPrintJob job = selectedService.createPrintJob();
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            job.print(doc, pras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        // Example usage
        List<String[]> products = List.of(
                new String[]{"2", "Burger", "5.99"},
                new String[]{"1", "Fries", "2.99"},
                new String[]{"3", "Soda", "1.50"}
        );

        TicketOrden ticket = new TicketOrden(
                "12345", "Caja 1", "John Doe", "Jane Smith", products,
                "10.48", "0.00", "3", "10.48", "20.00", "9.52",
                "Cash", "20.00"
        );

        // Assuming you have a print service selected
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        ticket.print(printService);
    }
}
