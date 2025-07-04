package apassignment.ticketingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketSorterController {

    private TicketController ticketController;

    private String currentLoggedUserID;

    @FXML
    private StackPane secondaryContent;
    @FXML
    private TextField searchBox;

    //filter buttons actions
    @FXML
    private void handleMyTickets() {
        String userId = "CUST007"; // need to code so it is based on currently logged user
        ticketController.filterTickets(parts -> parts[6].trim().equals(userId));
    }

    //to filter tickets by subject title using a search bar, this is the button to intialize filter
    @FXML
    private void handleSearch() {
        String keyword = searchBox.getText().trim().toLowerCase();
        ticketController.searchFilterTickets(parts -> parts[1].trim().toLowerCase().contains(keyword), currentLoggedUserID);
    }

    //display only open tickets
    @FXML
    private void handleOpenTickets() {
        ticketController.filterTicketsStatusUser("Open", currentLoggedUserID);
    }

    //display only inprogress tickets
    @FXML
    private void handleInProgressTickets() {
        ticketController.filterTicketsStatusUser("In-Progress", currentLoggedUserID);
    }

    //display only closed ticekts belonging to the current user
    @FXML
    private void handleClosedTickets() {
        ticketController.filterTicketsStatusUser("Closed", currentLoggedUserID);
    }


    public void initialize() {
        //first thing it loads is all the tickets
        try {
            currentLoggedUserID = "CUST007"; // need to code so it is based on currently logged user
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/ticketingsystem/Ticket.fxml"));
            Node content = loader.load();

            // Set parent controller on TicketController
            TicketController ticketController = loader.getController();
            ticketController.setParent(this);
            this.ticketController = ticketController; //save refernce

            //so only tickets belong to the currently logged on customer/user is displayed
            ticketController.filterTickets(parts -> parts[6].trim().equals(currentLoggedUserID));

            // dsiplays the tickets in list
            secondaryContent.getChildren().setAll(content);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load Ticket.fxml", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ticket Loading Error");
            alert.setHeaderText("Could not load ticket view");
            alert.setContentText("An error occurred while initializing the ticket screen. Please contact support.");
            alert.showAndWait();
        }
    }

    //function to load fxml file into the main content of app
    public void loadView(String fxmlFile) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            //to make sure they are parent when it loads again
            if (fxmlFile.equals("/apassignment/fxml/ticketingsystem/Ticket.fxml")) {
                TicketController ticketController = loader.getController();
                ticketController.setParent(this);
                this.ticketController = ticketController; //save refernce
            }

            secondaryContent.getChildren().setAll(content);
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


    public void showTicketDetails(String id, String subject, String status, String dateCreated, String submittedBy, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/ticketingsystem/TicketDetail.fxml"));
            Node detailView = loader.load();

            // pass values for tickets
            TicketDetailController detailController = loader.getController();
            detailController.setTicketDetails(id, subject, status, dateCreated, submittedBy, description);

            // Set parent controller
            detailController.setParentController(this);

            // displays the ticket details in right panel
            secondaryContent.getChildren().setAll(detailView);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load TicketDetail.fxml", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ticket Details Loading Error");
            alert.setHeaderText("Could not load ticket details");
            alert.setContentText("An error occurred while initializing the ticket details. Please contact support.");
            alert.showAndWait();
        }
    }

}
