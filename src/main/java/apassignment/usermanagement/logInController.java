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

//for recording which user is currently logged in
//to make an easily accesable varible to get teh userID of the current logged in User
import apassignment.util.UserSession;

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
                    String userID = userData[1].trim();
                    String email = userData[2].trim();
                    String password = userData[3].trim();

                    String value = password + "|" + userID;

                    // store using both username and email as keys
                    userCredentials.put(username, value);
                    userCredentials.put(email, value);
                }
            }
        } catch (IOException e) {
            login_lbl.setText("Error loading user database");
            e.printStackTrace();
        }
    }

    private void handleLogin() {
        String usernameEmail = name_field.getText().trim();
        String password = psd_field.getText().trim();

        if (usernameEmail.isEmpty() || password.isEmpty()) {
            login_lbl.setText("Please enter both username and password");
            return;
        }

        if (isValidCredentials(usernameEmail, password)) {
            //extracts userID from map and stores it as current session
            String userID = userCredentials.get(usernameEmail).split("\\|")[1];
            UserSession.setCurrentUserID(userID);

            try {
                // Load appropriate main view based on userID prefix
                Parent root;
                if (userID.startsWith("ADM")) { //opens only admin side of app
                    root = FXMLLoader.load(getClass().getResource("/apassignment/fxml/AdminMain.fxml"));
                } else {
                    root = FXMLLoader.load(getClass().getResource("/apassignment/fxml/Main.fxml"));
                }

                Scene scene = new Scene(root);
                Stage stage = (Stage) logIn_btn.getScene().getWindow();
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

    private boolean isValidCredentials(String input, String password) {
        if (userCredentials.containsKey(input)) {
            String[] data = userCredentials.get(input).split("\\|");
            String storedPassword = data[0];
            return storedPassword.equals(password);
        }
        return false;
    }
}