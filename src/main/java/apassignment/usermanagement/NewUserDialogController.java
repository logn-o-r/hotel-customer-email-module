package apassignment.usermanagement;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewUserDialogController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    private void initialize() {
        // Initialize with default values
        roleComboBox.getItems().addAll("User", "Admin", "Guest");
        roleComboBox.getSelectionModel().selectFirst();
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getRole() {
        return roleComboBox.getValue();
    }
}