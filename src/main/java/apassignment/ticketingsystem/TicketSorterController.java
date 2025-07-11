package apassignment.ticketingsystem;

import apassignment.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketSorterController {

    private TicketController ticketController;

    private String currentLoggedUserID = UserSession.getCurrentUserID();

    @FXML
    private StackPane secondaryContent;
    @FXML
    private TextField searchBox;

    @FXML
    private Button myTicketButtom;
    @FXML
    private Button myTicketButtomAgent;
    @FXML
    private Button unclaimedTicketsButton;

    //filter buttons actions
    @FXML
    private void handleMyTickets() {
        if (currentLoggedUserID.startsWith("ADM")) {
            ticketController.filterTickets(parts -> true); // show all tickets
        } else if (currentLoggedUserID.startsWith("CUST")) {
            ticketController.filterTickets(parts -> parts[6].trim().equals(currentLoggedUserID)); // submittedByUserID
        } else if (currentLoggedUserID.startsWith("AGT")) {
            ticketController.filterTickets(parts -> parts[5].trim().equals(currentLoggedUserID)); // assignedAgentUserID
        }
    }

    @FXML
    private void handleMyTicketsAgent() {
        ticketController.filterTickets(parts -> true); // show all tickets
    }

    @FXML
    private void handleUnclaimedTickets() {
        ticketController.searchFilterTickets(parts -> parts[5].trim().equals("Unassigned"), "close"); // shows unclaimed tickets/tickets with no assigned agent
    }

    //to filter tickets by subject title using a search bar, this is the button to intialize filter
    @FXML
    private void handleSearch() {
        String keyword = searchBox.getText().trim().toLowerCase();
        if (currentLoggedUserID.startsWith("ADM")) {
            ticketController.filterTickets(parts -> parts[1].trim().toLowerCase().contains(keyword));
        }
        else {
            ticketController.searchFilterTickets(parts -> parts[1].trim().toLowerCase().contains(keyword), currentLoggedUserID);
        }
    }

    //display only open tickets
    @FXML
    private void handleOpenTickets() {
        if (currentLoggedUserID.startsWith("ADM")) {
            ticketController.filterTicketsStatusUser("Open", null); }
        else {
            ticketController.filterTicketsStatusUser("Open", currentLoggedUserID);
        }
    }

    //display only inprogress tickets
    @FXML
    private void handleInProgressTickets() {
        if (currentLoggedUserID.startsWith("ADM")) {
            ticketController.filterTicketsStatusUser("In-Progress", null); }
        else {
            ticketController.filterTicketsStatusUser("In-Progress", currentLoggedUserID);
        }
    }

    //display only closed ticekts belonging to the current user
    @FXML
    private void handleClosedTickets() {
        if (currentLoggedUserID.startsWith("ADM")) {
            ticketController.filterTicketsStatusUser("Closed", null); }
        else {
            ticketController.filterTicketsStatusUser("Closed", currentLoggedUserID);
        }
    }


    public void initialize() {
        //first thing it loads is all the tickets
        try {
            currentLoggedUserID = UserSession.getCurrentUserID();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/ticketingsystem/Ticket.fxml"));
            Node content = loader.load();

            // Set parent controller on TicketController
            TicketController ticketController = loader.getController();
            ticketController.setParent(this);
            this.ticketController = ticketController; //save refernce

            //so only tickets belong to the currently logged on customer/user is displayed
            if (currentLoggedUserID.startsWith("ADM")) {
                unclaimedTicketsButton.setVisible(true);
                unclaimedTicketsButton.setDisable(false);
                myTicketButtom.setText("All Tickets");
                ticketController.filterTickets(parts -> true); // show all tickets
            } else if (currentLoggedUserID.startsWith("CUST")) {
                ticketController.filterTickets(parts -> parts[6].trim().equals(currentLoggedUserID)); // submittedByUserID
            } else if (currentLoggedUserID.startsWith("AGT")) {
                unclaimedTicketsButton.setVisible(true);
                unclaimedTicketsButton.setDisable(false);
                myTicketButtomAgent.setVisible(true);
                myTicketButtomAgent.setDisable(false);
                ticketController.filterTickets(parts -> parts[5].trim().equals(currentLoggedUserID)); // assignedAgentUserID
            }

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

                if (currentLoggedUserID.startsWith("ADM")) {
                    ticketController.filterTickets(parts -> true); // show all tickets
                } else if (currentLoggedUserID.startsWith("CUST")) {
                    ticketController.filterTickets(parts -> parts[6].trim().equals(currentLoggedUserID)); // submittedByUserID
                } else if (currentLoggedUserID.startsWith("AGT")) {
                    ticketController.filterTickets(parts -> parts[5].trim().equals(currentLoggedUserID)); // assignedAgentUserID
                }

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


    public void showTicketDetails(String id, String subject, String status, String dateCreated, String priority, String assignedAgent, String submittedBy, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/ticketingsystem/TicketDetail.fxml"));
            Node detailView = loader.load();

            // pass values for tickets
            TicketDetailController detailController = loader.getController();
            detailController.setTicketDetails(id, subject, status, dateCreated, priority, assignedAgent, submittedBy, description);

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
