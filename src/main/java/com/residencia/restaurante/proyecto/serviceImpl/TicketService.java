package com.residencia.restaurante.proyecto.serviceImpl;

import com.residencia.restaurante.proyecto.repository.IImpresoraRepository;
import com.residencia.restaurante.security.utils.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private IImpresoraRepository impresoraRepository;

    public void printTicket(String nameLocal, String expedition, String box, String ticket, String cajero, String dateTime, String items, String subTotal, String tax, String total, String recibo, String change) {


        Ticket ticke = new Ticket(nameLocal, expedition, box, ticket, cajero, dateTime, items, subTotal, tax, total, recibo, change);
        ticke.print();
    }
}
