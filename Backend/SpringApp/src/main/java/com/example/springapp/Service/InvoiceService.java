package com.example.springapp.Service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final String fileStoragePath = "..\\..\\..\\..\\..\\..\\..\\FileStorage\\";
    public boolean createInvoice(int customerID) {
        boolean requestSent = false;
        // Queue Logic
        return requestSent;
    }

    public List<String> getInvoice(int customerID) {
        List<String> invoiceInfo = new ArrayList<String>();
        invoiceInfo.add(fileStoragePath+"1.txt");
        invoiceInfo.add("18.05.23");
        return invoiceInfo;
    }
}
