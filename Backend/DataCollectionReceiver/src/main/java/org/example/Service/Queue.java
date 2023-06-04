package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Queue {

    private final static String CONSUME1 = "DCD_DCR";
    private final static String CONSUME2 = "SDC_DCR";
    private final static String PRODUCE = "DCR_PG";
    private final static String HOST = "localhost";
    private final static int PORT = 30003;
    private int id;

    private int expectedMessages;

    public void consumeExpectedMessages() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME1, false, false, false, null);

        System.out.println(" [*] Waiting for messages from DataCollectionDispatcher. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from DataCollectionDispatcher: '" + message + "' " + LocalTime.now());

            int count = 0;
            int id = 0;

            String[] keyValuePairs = message.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("count")) {
                        count = Integer.parseInt(value);
                    } else if (key.equals("id")) {
                        id = Integer.parseInt(value);
                    }
                }
            }

            this.expectedMessages = count;
            this.id = id;
        };

        channel.basicConsume(CONSUME1, true, deliverCallback, consumerTag -> {
        });
    }

    public void consumeActualMessages() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME2, false, false, false, null);

        System.out.println(" [*] Waiting for messages from StationDataCollector. To exit press CTRL+C");

        List<String> receivedMessages = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from StationDataCollector: '" + message + "' " + LocalTime.now());
            receivedMessages.add(message);

            // Check if the number of received messages from CONSUME2 matches the expected number
            if (receivedMessages.size() == this.expectedMessages) {
                String customerTotal = calculateTotal(receivedMessages);
                try {
                    send(customerTotal);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        channel.basicConsume(CONSUME2, true, deliverCallback, consumerTag -> {
        });
    }

    private void send(String customerData) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        String customerID = extractIdFromMessage(customerData);
        int customerKWH = extractTotalKWHFromCustomerData(customerData);
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(PRODUCE, false, false, false, null);

            channel.basicPublish("", PRODUCE, null, customerData.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [" + customerID + "] sent '" + customerKWH + "' to PDFGenerator");
        }
        inform(customerData);
    }

    private void inform(String customerData) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        String id = extractIdFromMessage(customerData);
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(PRODUCE, false, false, false, null);
            channel.basicPublish("", PRODUCE, null, id.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x" + id + "] informed PDFGenerator");
        }
    }

    private String extractIdFromMessage(String message) {
        String[] keyValuePairs = message.split("&");
        for (String keyValuePair : keyValuePairs) {
            String[] parts = keyValuePair.split("=");
            if (parts.length == 2 && parts[0].equals("id")) {
                return parts[1];
            }
        }
        return null;
    }

    private int extractTotalKWHFromCustomerData(String customerData) {
        String[] keyValuePairs = customerData.split("&");
        for (String keyValuePair : keyValuePairs) {
            String[] parts = keyValuePair.split("=");
            if (parts.length == 2 && parts[0].equals("totalKWH")) {
                return Integer.parseInt(parts[1]);
            }
        }
        return 0;
    }

    private String calculateTotal(List<String> receivedMessages) {
        int totalKWH = 0;
        String id = null;
        for (String message : receivedMessages) {
            String[] keyValuePairs = message.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("id")) {
                        id = value;
                    } else if (key.equals("kwh")) {
                        int kwh = Integer.parseInt(value);
                        totalKWH += kwh;
                    }
                }
            }
        }

        if (id != null) {
            return "id=" + id + "&totalKWH=" + totalKWH;
        }
        return null; // or handle the case where id is not found
    }

}
