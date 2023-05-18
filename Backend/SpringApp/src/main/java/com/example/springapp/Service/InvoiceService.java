package com.example.springapp.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final String fileStoragePath = "..\\..\\..\\..\\..\\..\\..\\FileStorage\\";

    public boolean createInvoice(int customerID) {
        boolean requestSent = true;
        // Queue Logic
        return requestSent;
    }

    public List<String> getInvoice(int customerID) {
        List<String> invoiceInfo = new ArrayList<>();
        /*File[] files = new File(fileStoragePath).listFiles();
        for (File file : files) {
            if (file.getName().equals(customerID)) {
                invoiceInfo.add(file.getName());
            }
        }*/
        invoiceInfo.add(fileStoragePath+"1.txt");
        return invoiceInfo;
    }
}
