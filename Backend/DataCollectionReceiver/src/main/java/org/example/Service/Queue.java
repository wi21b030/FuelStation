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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Queue {

    private final static String CONSUME1 = "DCD_DCR";
    private final static String CONSUME2 = "SDC_DCR";
    private final static String PRODUCE = "DCR_PG";
    private final static String HOST = "localhost";
    private final static int PORT = 30003;
    private int expectedCount;
    private int receivedCount = 0;
    private CountDownLatch dataCollectionLatch;

    private static ConnectionFactory factory;

    public Queue() {
        factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        dataCollectionLatch = new CountDownLatch(1);
    }

    public void consumeExpectedMessages() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME1, false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from DataCollectionDispatcher: '" + message + "' " + LocalTime.now());

            int customerID;

            String[] keyValuePairs = message.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("count")) {
                        expectedCount = Integer.parseInt(value);
                    } else if (key.equals("id")) {
                        customerID = Integer.parseInt(value);
                    }
                }
            }

            dataCollectionLatch.countDown();
            try {
                consumeActualMessages();// Signal that data collection is complete
            } catch (TimeoutException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(CONSUME1, true, deliverCallback, consumerTag -> {
        });
    }

    public void consumeActualMessages() throws IOException, TimeoutException, InterruptedException {
        dataCollectionLatch.await(); // Wait for data collection to complete

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME2, false, false, false, null);

        List<String> receivedMessages = new ArrayList<>();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received from StationDataCollector: '" + message + "' " + LocalTime.now());
            receivedMessages.add(message);
            receivedCount++;

            if (expectedCount == receivedCount) {
                System.out.println("got this " + receivedMessages);
                try {
                    System.out.println("calculating " + receivedMessages);
                    String customerTotal = calculateTotal(receivedMessages);
                    send(customerTotal);
                    receivedMessages.clear();
                    receivedCount = 0;
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        channel.basicConsume(CONSUME2, true, deliverCallback, consumerTag -> {
        });
    }

    private void send(String customerData) throws IOException, TimeoutException {
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            System.out.println("Publishing " + customerData);
            channel.queueDeclare(PRODUCE, false, false, false, null);
            channel.basicPublish("", PRODUCE, null, customerData.getBytes(StandardCharsets.UTF_8));
            System.out.println(" Sent: '" + customerData + "' to PDFGenerator");
        }
    }

    public String calculateTotal(List<String> receivedMessages) {
        float totalKWH = 0;
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
                        String cleanedValue = value.replaceAll(",", ".");
                        totalKWH += Float.valueOf(cleanedValue);
                    }
                }
            }
        }
        if (id != null) {
            System.out.println(totalKWH);
            return "id=" + id + "&totalKWH=" + totalKWH;
        }
        return null;
    }
}

