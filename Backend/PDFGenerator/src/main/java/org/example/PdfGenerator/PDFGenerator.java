package org.example.PdfGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.Data.Customer;
import org.example.Service.Database;
import org.example.Service.Queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PDFGenerator {

    public static void main(String[] args) {
        Queue queue = new Queue();

        listen(queue);
    }

    private static void listen(Queue queue) {
        try {
            queue.receive();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}