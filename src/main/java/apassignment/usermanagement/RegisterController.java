package apassignment.usermanagement;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RegisterController {

    @FXML private TextField username_field;
    @FXML private TextField email_field;
    @FXML private PasswordField psd_field;
    @FXML private PasswordField confirm_field;
    @FXML private Label msg_lbl;

    @FXML
    private void initialize() {
        msg_lbl.setText("");
    }

    @FXML
    private void registerUser() {
        String username = username_field.getText().trim();
        String email = email_field.getText().trim();
        String password = psd_field.getText();
        String confirmPassword = confirm_field.getText();

        // Simple validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            msg_lbl.setText("Please fill all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            msg_lbl.setText("Passwords don't match");
            return;
        }

        if (password.length() < 8) {
            msg_lbl.setText("Password must be 8+ characters");
            return;
        }

        String role = "Customer";
        String userId = generateUniqueUserID();
        // Save data in format: userId,username,email,password,role
        try {
            FileWriter writer = new FileWriter("users.txt", true);


            // Simple string concatenation instead of String.format()
            writer.write(username + "," + userId + "," + email + "," + password + "," + role + "\n");
            writer.close();

            msg_lbl.setText("Registered! ID: " + userId);
            username_field.clear();
            email_field.clear();
            psd_field.clear();
            confirm_field.clear();

        } catch (IOException e) {
            msg_lbl.setText("Error saving data");
        }
    }
    private String generateUniqueUserID() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("users.txt"));
            int maxId = lines.stream()
                    .map(line -> line.split("\\|")[0].trim().replace("TCK", ""))
                    .filter(id -> id.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);
            return String.format("CUST%03d", maxId + 1);
        } catch (IOException e) {
            return "CUST001";
        }
    }
}