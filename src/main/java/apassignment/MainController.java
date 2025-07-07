package apassignment;

import javafx.scene.control.Alert;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    //the place where the component will be displayed to be used
    @FXML
    private StackPane mainContent;

    //Labels to become icons
    @FXML
    private Label dashboardIcon;
    @FXML
    private Label usermanagementIcon;
    @FXML
    private Label ticketIcon;
    @FXML
    private Label faqIcon;
    @FXML
    private Label chatIcon;
    @FXML
    private Label emailIcon;

    @FXML
    public void initialize() {
        //To set icons
        setIcon(dashboardIcon, FontAwesome.HOME);
        setIcon(usermanagementIcon, FontAwesome.USERS);
        setIcon(ticketIcon, FontAwesome.TICKET);
        setIcon(faqIcon, FontAwesome.QUESTION_CIRCLE);
        setIcon(chatIcon, FontAwesome.COMMENTS);
        setIcon(emailIcon, FontAwesome.ENVELOPE);

        //Set click handlers (to make the label icons into buttons)
        //dashboardIcon.setOnMouseClicked(e -> loadView("Dashboard.fxml"));
        //usermanagementIcon.setOnMouseClicked(e -> loadView("Dashboard.fxml"))
        ticketIcon.setOnMouseClicked(e -> loadView("/apassignment/fxml/ticketingsystem/TicketSorter.fxml"));
        //faqIcon.setOnMouseClicked(e -> loadView("FAQ.fxml"));
        //chatIcon.setOnMouseClicked(e -> loadView("LiveChat.fxml"));
        //emailIcon.setOnMouseClicked(e -> loadView("Email.fxml"));
    }

    //function to set label to a graphic icon from fontawesome
    private void setIcon(Label label, FontAwesome iconType) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(18);
        icon.setIconColor(Color.BLACK);
        label.setGraphic(icon);
    }

    //function to load fxml file into the main content of app
    private void loadView(String fxmlFile) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            //for debugging and making sure the ticket button works
            //if (fxmlFile.equals("TicketSorter.fxml")) {
                //System.out.println("Ticekt button pressed");
            //}

            mainContent.getChildren().setAll(content);
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load FXML view: " + fxmlFile, e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scene Load Error");
            alert.setHeaderText("Could Not Load View");
            alert.setContentText("The requested scene could not be loaded. Please try again later.");
            alert.showAndWait();

        }
    }
}
