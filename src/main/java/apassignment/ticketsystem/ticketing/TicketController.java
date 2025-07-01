package apassignment.ticketsystem.ticketing;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TicketController {

    //place to put the ticket hbox into
    @FXML
    private VBox ticketContainer;

    //to load the new ticket form into
    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {
        loadTicketsFromFile("ticket.txt");
    }

    private void loadTicketsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) { //while loop to stop reading once file has no more lines
                String[] parts = line.split("\\|");

                if (parts.length < 7) continue; //skips the line in file if the data is not in correct format

                //ticket data format: TicketID | Subject | Status | Description | Priority | AssignedAgentUserID | SubmittedByUserID
                //divides the lines into parts based on order of data in ticket meant to be displayed.

                //data from ticket.txt displayed on ui
                String subject = parts[1].trim();
                String status = parts[2].trim();
                String description = parts[3].trim();

                //create ticket row
                HBox ticketBox = new HBox(10);
                ticketBox.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-background-color: #F5F5F5;");
                ticketBox.setAlignment(Pos.CENTER_LEFT);

                //subject of ticket
                Label subjectLabel = new Label(subject);
                subjectLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(subjectLabel, Priority.ALWAYS);

                //status of ticket as a symbol using fontawesome
                FontIcon statusIcon = getStatusIcons(status);
                statusIcon.setIconSize(16);
                statusIcon.setIconColor(Color.BLACK);
                // Wrap status in an HBox for consistent layout
                HBox statusBox = new HBox(statusIcon);
                statusBox.setAlignment(Pos.CENTER);
                statusBox.setPrefWidth(50);

                //description of ticket
                Label descLabel = new Label(description);
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(descLabel, Priority.ALWAYS);

                //space to push arrow button to the far right
                Region space = new Region();
                HBox.setHgrow(space, Priority.ALWAYS);

                //to create the arrow icon for the 'view the ticket' button
                FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
                arrowIcon.setIconSize(16);
                arrowIcon.setIconColor(Color.BLACK);
                //Creating the button for the arrow
                Button viewButton = new Button();
                viewButton.setGraphic(arrowIcon);
                viewButton.setMinWidth(40);
                HBox.setHgrow(viewButton, Priority.NEVER);

                ticketBox.getChildren().addAll(subjectLabel, statusIcon, descLabel, space, viewButton);
                ticketContainer.getChildren().add(ticketBox);

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // to make so that the status is displayed as a symbol instead of text
    private FontIcon getStatusIcons(String status) {
        FontIcon iconView;
        switch (status.toLowerCase()) {
            case "open":
                iconView = new FontIcon(FontAwesome.CIRCLE);
                iconView.setIconColor(Color.BLUE);
                break;
            case "in-progress":
                iconView = new FontIcon(FontAwesome.COG);
                iconView.setIconColor(Color.ORANGE);
                break;
            case "closed":
                iconView = new FontIcon(FontAwesome.CHECK_CIRCLE);
                iconView.setIconColor(Color.GREEN);
                break;
            default:
                iconView = new FontIcon(FontAwesome.QUESTION_CIRCLE);
                iconView.setIconColor(Color.RED);
                break;
        }
        return iconView;
    }

    //function to load fxml file into the main content of app
//    private void loadView() {
//        try{
//            Node content = FXMLLoader.load(getClass().getResource(newTicket.fxml));
//            mainContent.getChildren().setAll(content);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}