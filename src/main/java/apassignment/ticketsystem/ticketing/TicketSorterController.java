package apassignment.ticketsystem.ticketing;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class TicketSorterController {

    @FXML
    private StackPane secondaryContent;

    public void initialize() {

        //first thing it loads is all the tickets
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Ticket.fxml"));
            Node content = loader.load();

            // Set parent controller on TicketController
            TicketController ticketController = loader.getController();
            ticketController.setParent(this);

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
