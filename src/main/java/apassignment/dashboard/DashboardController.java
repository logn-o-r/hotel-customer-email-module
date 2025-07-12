package apassignment.dashboard;

import apassignment.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label currentLoggedUserLabel;
    @FXML
    private Label userIDLoggedLabel;
    @FXML
    private Button logOutButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String userID = UserSession.getCurrentUserID();
        String username = getUsernameByUserID(userID);
        currentLoggedUserLabel.setText("Current Logged User: " + username);
        userIDLoggedLabel.setText("UserID: " + userID);
    }

    //retrives the username attached to the userID
    public String getUsernameByUserID(String userID) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].trim().equals(userID)) {
                    return parts[0].trim(); // returns the username
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown"; // default if userID is not found
    }

    @FXML
    public void handleLogOut(ActionEvent event) {
        // clear session
        UserSession.clear();

        // return to login.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/usermanagement/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) logOutButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
