package com.example.springapp.Controller;

import com.example.springapp.Service.InvoiceService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {
    private final InvoiceService invSer;

    public InvoiceController(InvoiceService invSer) {
        this.invSer = invSer;
    }
}
