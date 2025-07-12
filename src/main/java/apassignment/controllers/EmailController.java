package com.hotel.controller;

import com.hotel.model.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public class EmailController {

    @FXML private ListView<Email> emailListView;
    @FXML private TextField toField, fromField, subjectField, dateField;
    @FXML private TextArea bodyArea;
    @FXML private Button composeBtn, deleteBtn, replyBtn, forwardBtn;
    @FXML private TextField searchField;

    private ObservableList<Email> emailList = FXCollections.observableArrayList();
    private final String EMAIL_FILE = "emails.txt";

    @FXML
    public void initialize() {
        loadEmailsFromFile();
        emailListView.setItems(emailList);

        emailListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty || email == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox();
                    container.setSpacing(4);
                    container.setStyle(
                            "-fx-background-color: #d4fcd4;" +  // light green background
                                    "-fx-border-color: #ccc;" +
                                    "-fx-border-radius: 8;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-padding: 10;" +
                                    "-fx-font-size: 13px;"
                    );

                    Label subject = new Label(email.getSubject());
                    subject.setStyle("-fx-font-weight: bold;");
                    Label sender = new Label("From: " + email.getFrom());
                    Label date = new Label(email.getDate());
                    date.setStyle("-fx-text-fill: grey; -fx-font-size: 11px;");
                    container.getChildren().addAll(subject, sender, date);
                    setGraphic(container);
                }
            }
        });

        emailListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) showEmailDetails(newVal);
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmailList(newValue.trim().toLowerCase());
        });

        composeBtn.setOnAction(e -> openComposeWindow());
        deleteBtn.setOnAction(e -> deleteSelectedEmail());
        replyBtn.setOnAction(e -> replyToEmail());
        forwardBtn.setOnAction(e -> forwardEmail());
    }

    private void filterEmailList(String query) {
        if (query.isEmpty()) {
            emailListView.setItems(emailList);
            return;
        }

        ObservableList<Email> filtered = emailList.filtered(email ->
                email.getSubject().toLowerCase().contains(query) ||
                        email.getFrom().toLowerCase().contains(query) ||
                        email.getBody().toLowerCase().contains(query)
        );

        emailListView.setItems(filtered);
    }

    private void loadEmailsFromFile() {
        emailList.clear();
        Path path = Paths.get(EMAIL_FILE);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    emailList.add(new Email(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load emails.");
        }
    }

    private void saveEmailsToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(EMAIL_FILE))) {
            for (Email email : emailList) {
                writer.write(String.join("|",
                        email.getTo(),
                        email.getFrom(),
                        email.getSubject(),
                        email.getBody(),
                        email.getDate()));
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save emails.");
        }
    }

    private void showEmailDetails(Email email) {
        toField.setText(email.getTo());
        fromField.setText(email.getFrom());
        subjectField.setText(email.getSubject());
        dateField.setText(email.getDate());
        bodyArea.setText(email.getBody());
    }

    private void deleteSelectedEmail() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            emailList.remove(selected);
            saveEmailsToFile();
            clearFields();
        } else {
            showAlert("No Selection", "Please select an email to delete.");
        }
    }

    private void replyToEmail() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email reply = new Email(
                    selected.getFrom(),
                    selected.getTo(),
                    "RE: " + selected.getSubject(),
                    "Thanks for your message.",
                    LocalDate.now().toString()
            );
            emailList.add(reply);
            saveEmailsToFile();
        } else {
            showAlert("No Email Selected", "Please select an email to reply.");
        }
    }

    private void forwardEmail() {
        Email selected = emailListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email forward = new Email(
                    "someone@forward.com",
                    selected.getFrom(),
                    "FWD: " + selected.getSubject(),
                    selected.getBody(),
                    LocalDate.now().toString()
            );
            emailList.add(forward);
            saveEmailsToFile();
        } else {
            showAlert("No Email Selected", "Please select an email to forward.");
        }
    }

    private void openComposeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/compose_email.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Compose Email");
            stage.setScene(new Scene(root, 900, 600));  // ✅ Match main window size
            stage.showAndWait();

            loadEmailsFromFile();
            emailListView.setItems(emailList);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to open compose window.");
        }
    }

    private void clearFields() {
        toField.clear();
        fromField.clear();
        subjectField.clear();
        dateField.clear();
        bodyArea.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
