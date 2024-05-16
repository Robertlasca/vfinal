package com.residencia.restaurante.security.utils;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class TicketRequest {
    private String nameLocal;
    private String expedition;
    private String box;
    private String ticket;
    private String cajero;
    private String dateTime;
    private String items;
    private String subTotal;
    private String tax;
    private String total;
    private String recibo;
    private String change;

    // Getters and Setters
}
