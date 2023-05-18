package com.example.springapp.Service;

import com.example.springapp.Queue.Queue;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    private final Queue queue = new Queue();

    public boolean createInvoice(String customerID) {
        return queue.send(customerID);
    }

    public List<String> getInvoice(int customerID) {
        String fileStoragePath = ".\\Backend\\FileStorage\\";
        String filePath = fileStoragePath + customerID + ".txt";
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            List<String> invoiceInfo = new ArrayList<>();
            invoiceInfo.add(filePath);
            return invoiceInfo;
        }
        return null;
    }

}