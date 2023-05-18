package com.example.javafxapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;


public class RequestController {
    @FXML
    private Button gatherDataButton;
    @FXML
    private Button downloadInvoiceButton;
    @FXML
    private TextField customerIdField1;
    @FXML
    private TextField customerIdField2;
    @FXML
    private Label response;
    @FXML
    private TextArea responseCreationInvoice;


    @FXML
    public void initialize() {
        gatherDataButton.setOnAction(event -> gatherData(customerIdField1.getText()));
        downloadInvoiceButton.setOnAction(event -> downloadInvoice(customerIdField2.getText()));

    }

    @FXML
    private void gatherData(String id) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                .POST(HttpRequest.BodyPublishers.ofString(id))
                .build();

        try {
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> Platform.runLater(() -> response.setText(body)))
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void downloadInvoice(String id) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:8080/invoices/%s", id)))
                .GET()
                .build();

        try {
            client.sendAsync(getRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> Platform.runLater(() -> responseCreationInvoice.setText(body)))
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}