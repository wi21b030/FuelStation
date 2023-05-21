package org.example.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Station;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Queue {

    private final static String CONSUME = "SA_DCD";
    private final static String PRODUCE1 = "DCD_SDC";
    private final static String PRODUCE2 = "DCD_DCR";
    private final static String HOST = "localhost";
    private final static int PORT1 = 30003;
    //private final static int PORT2 = 30083;

    private int id;

    public void receive(List<Station> stations) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT1);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CONSUME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' " + LocalTime.now());
            this.id = Integer.parseInt(message);

            try {
                send(stations);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(CONSUME, true, deliverCallback, consumerTag -> {});
    }

    private void send(List<Station> stations) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT1);

        int i = 0;
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(PRODUCE1, false, false, false, null);
            for (Station s : stations) {
                String message = "db_url=" + s.getUrl() + "&id=" + this.id;
                channel.basicPublish("", PRODUCE1, null, message.getBytes(StandardCharsets.UTF_8));
                
                //channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                i++;
                System.out.println(" [" + i + "] sent '" + this.id + "' to Station Data Collector");
            }
        }
        inform(i);
    }

    private static void inform(int i) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT1);
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(PRODUCE2, false, false, false, null);

            String length = String.valueOf(i);
            channel.basicPublish("", PRODUCE2, null, length.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x" + i + "] informed Data Collection Receiver");
        }
    }
}
