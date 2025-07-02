package apassignment.ticketsystem.ticketing;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import javafx.scene.control.TextField;
import java.io.IOException;

public class TicketSorterController {

    private TicketController ticketController;

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
        ticketController.filterTickets(parts -> parts[1].trim().toLowerCase().contains(keyword));
    }

    //display only open tickets
    @FXML
    private void handleOpenTickets() {
        ticketController.filterTickets(parts -> parts[2].trim().equalsIgnoreCase("Open"));
    }

    //display only inprogress tickets
    @FXML
    private void handleInProgressTickets() {
        ticketController.filterTickets(parts -> parts[2].trim().equalsIgnoreCase("In-Progress"));
    }

    //display only closed ticekts
    @FXML
    private void handleClosedTickets() {
        ticketController.filterTickets(parts -> parts[2].trim().equalsIgnoreCase("Closed"));
    }


    public void initialize() {

        //first thing it loads is all the tickets
        try {
            String userId = "CUST007"; // need to code so it is based on currently logged user
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket.fxml"));
            Node content = loader.load();

            // Set parent controller on TicketController
            TicketController ticketController = loader.getController();
            ticketController.setParent(this);
            this.ticketController = ticketController; //save refernce

            //so only tickets belong to the currently logged on customer/user is displayed
            ticketController.filterTickets(parts -> parts[6].trim().equals(userId));

            // dsiplays the tickets in list
            secondaryContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //function to load fxml file into the main content of app
    public void loadView(String fxmlFile) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            //to make sure they are parent when it loads again
            if (fxmlFile.equals("Ticket.fxml")) {
                TicketController ticketController = loader.getController();
                ticketController.setParent(this);
                this.ticketController = ticketController; //save refernce
            }

            secondaryContent.getChildren().setAll(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showTicketDetails(String id, String subject, String status, String dateCreated, String submittedBy, String description) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketDetail.fxml"));
            Node detailView = loader.load();

            // pass values for tickets
            TicketDetailController detailController = loader.getController();
            detailController.setTicketDetails(id, subject, status, dateCreated, submittedBy, description);

            // Set parent controller
            detailController.setParentController(this);

            // displays the ticket details in right panel
            secondaryContent.getChildren().setAll(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
