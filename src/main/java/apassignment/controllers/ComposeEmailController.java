package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class ComposeEmailController {

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;
    @FXML private Button sendBtn;

    private final String EMAIL_FILE = "emails.txt";
    private final String SENDER_EMAIL = "admin@hotel.com";

    @FXML
    public void initialize() {
        sendBtn.setOnAction(e -> sendEmail());
    }

    private void sendEmail() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText().trim();
        String date = LocalDate.now().toString();

        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMAIL_FILE, true))) {
            writer.write(String.join("|", to, SENDER_EMAIL, subject, body, date));
            writer.newLine();
            showAlert("Email sent successfully!");

            // Close the compose window
            Stage stage = (Stage) sendBtn.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            showAlert("Failed to send email. Please try again.");
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ✅ This fixes the onAction="#handleClose" error in FXML
    @FXML
    private void handleClose() {
        Stage stage = (Stage) sendBtn.getScene().getWindow();
        stage.close();
    }
}
