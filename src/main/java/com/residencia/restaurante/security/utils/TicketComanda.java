package com.residencia.restaurante.security.utils;
import com.residencia.restaurante.proyecto.entity.Impresora;
import com.residencia.restaurante.proyecto.repository.IImpresoraRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class TicketComanda {

    private String contentTicket;

    public TicketComanda(String customerName, String serviceArea, String waiter, List<String> products) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());

        this.contentTicket =
                "Cliente: " + customerName + "  Área: " + serviceArea + "\n" +
                        "Fecha: " + date + "  Hora: " + time + "\n" +
                        "Mesero: " + waiter + "\n" +
                        "=============================\n" +
                        "PRODUCTOS\n" +
                        "=============================\n" +
                        String.join("\n", products) + "\n" +
                        "=============================\n";
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

}