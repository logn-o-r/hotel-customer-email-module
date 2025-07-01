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
        loadView("Ticket.fxml");
    }

    //function to load fxml file into the main content of app
    private void loadView(String fxmlFile) {
        try{
            Node content = FXMLLoader.load(getClass().getResource(fxmlFile));
            secondaryContent.getChildren().setAll(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
