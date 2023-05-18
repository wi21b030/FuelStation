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
        boolean requestSent = false;
        // Queue Logic
        return requestSent;
    }

    public List<String> getInvoice(int customerID) {
        List<String> invoiceInfo = new ArrayList<>();
        Path filePath = Path.of(fileStoragePath + "1.txt");
        BasicFileAttributes fileAttributes = null;
        try {
            fileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileTime creationTime = fileAttributes.creationTime();
        LocalDateTime creationDateTime = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
        invoiceInfo.add(fileStoragePath + "1.txt");
        invoiceInfo.add(String.valueOf(creationDateTime));
        return invoiceInfo;
    }
}
