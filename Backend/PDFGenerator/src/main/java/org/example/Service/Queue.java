package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.Data.Customer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Queue {

    private final static String CONSUME = "DCR_PG";
    private final static String HOST = "localhost";
    private final static int PORT = 30003;
    private double kwh;
    private int id;
    private String message;
    private static ConnectionFactory factory;

    public Queue() {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
    }

    public void receive() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' " + LocalTime.now());
            this.message = message;
/*
            String[] keyValuePairs = message.split("&");
            String[] parts = null;
            for (String s : keyValuePairs)
                parts = s.split("=");
            assert parts != null;
            this.id = Integer.parseInt(parts[1]);
            this.kwh = Double.parseDouble(parts[3]);

            System.out.println(id + kwh);*/

            String pattern = "id=(\\d+)&kwh=(\\d.+)";

            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(message);

            if (matcher.find()) {
                String id = matcher.group(1);
                String kwh = matcher.group(2);
                System.out.println("Extracted id: " + id);
                System.out.println("Extracted kwh: " + kwh);
            } else {
                System.out.println("Numbers not found.");
            }
            generate(getKwh(), Database.select(getId()), getId());

        };

        channel.basicConsume(CONSUME, true, deliverCallback, consumerTag -> {});
    }

    private static void generate(double kwh, Customer select, int id) {
        PDDocument document = new PDDocument();

        PDPage firstPage = new PDPage();
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(document, firstPage);

            contentStream.beginText();
            contentStream.setFont( font, 12 );
            contentStream.lineTo( 100, 700 );
            contentStream.showText( "Receipt\n\nID: " + id + "kwH: " + kwh + "\nPrice: "+ kwh * 2.5 + " €");
            contentStream.endText();
            contentStream.close();

            //document.addPage(firstPage);
            document.save(".\\Backend\\FileStorage\\"+id+".pdf");
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double getKwh() {
        return kwh;
    }

    public int getId() {
        return id;
    }
}
