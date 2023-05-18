package org.example.PdfGenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

public class PDFGenerator {
    public static void main(String[] args) {


        PDDocument document = new PDDocument();
        PDPage firstPage = new PDPage();
        document.addPage(firstPage);
        try {
            document.save(".\\Backend\\Filestorage\\1.pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}