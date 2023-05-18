package org.example;

import com.rabbitmq.client.ConnectionFactory;
import org.example.Service.Database;
import org.example.Service.Queue;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException {
        Database db = new Database();
        Queue queue = new Queue();

        DataCollectionDispatcher dcd = new DataCollectionDispatcher(db, queue);

        List<Station> stations = dcd.getDatabase();
        ConnectionFactory factory = new ConnectionFactory();
        dcd.wait(stations, factory);
    }
}