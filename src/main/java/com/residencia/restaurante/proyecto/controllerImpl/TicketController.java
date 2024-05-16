package com.residencia.restaurante.proyecto.controllerImpl;

import com.residencia.restaurante.proyecto.serviceImpl.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/print")
    public ResponseEntity<?> printTicket() {
        try {
            ticketService.printTicket("Plazita gourmet", "No", "Caja 1",
                    "01", "Roberto", LocalDateTime.now().toString(),
                    "Ni yose", "500", "200",
                    "1000", "2000", "3000");
            return ResponseEntity.ok("Ticket impreso con éxito");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al imprimir el ticket: " + e.getMessage());
        }
    }
}
