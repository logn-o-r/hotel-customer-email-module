package apassignment.usermanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class logInController {
    @FXML private TextField name_field;
    @FXML private PasswordField psd_field;
    @FXML private Button logIn_btn;
    @FXML private Label login_lbl;

    private static final String USERS_FILE = "users.txt";
    private Map<String, String> userCredentials = new HashMap<>();

    @FXML
    private void initialize() {
        loadUserCredentials();
        logIn_btn.setOnAction(event -> handleLogin());
    }

    private void loadUserCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 5) {
                    String username = userData[0].trim();
                    String password = userData[3].trim();
                    userCredentials.put(username, password);
                }
            }
        } catch (IOException e) {
            login_lbl.setText("Error loading user database");
            e.printStackTrace();
        }
    }

    private void handleLogin() {
        String username = name_field.getText().trim();
        String password = psd_field.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            login_lbl.setText("Please enter both username and password");
            return;
        }

        if (isValidCredentials(username, password)) {
            try {
                // Load main.fxml
                Parent root = FXMLLoader.load(getClass().getResource("/apassignment/fxml/main.fxml"));
                Scene scene = new Scene(root);

                // Get current stage
                Stage stage = (Stage) logIn_btn.getScene().getWindow();

                // Set new scene
                stage.setScene(scene);
                stage.setTitle("Main Application");
                stage.show();
            } catch (IOException e) {
                login_lbl.setText("Error loading main application");
                e.printStackTrace();
            }
        } else {
            login_lbl.setText("Invalid username or password");
        }
    }

    private boolean isValidCredentials(String username, String password) {
        return userCredentials.containsKey(username) &&
                userCredentials.get(username).equals(password);
    }
}