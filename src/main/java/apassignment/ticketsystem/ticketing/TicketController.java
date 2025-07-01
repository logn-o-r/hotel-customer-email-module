package apassignment.ticketsystem.ticketing;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    //set TicketSorterController as parent to allow the arrow button to switch scenes
    private TicketSorterController parentController;

    private void loadTicketsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) { //while loop to stop reading once file has no more lines

                //ticket data format: TicketID | Subject | Status | Description | Priority | AssignedAgentUserID | SubmittedByUserID | dateSubmitted
                //divides the lines into parts based on order of data in ticket meant to be displayed.
                String[] parts = line.split("\\|");
                if (parts.length < 8) continue; //skips the line in file if the data is not in correct format

                //data from ticket.txt displayed on ui
                String id = parts[0].trim();
                String subject = parts[1].trim();
                String status = parts[2].trim();
                String description = parts[3].trim();
                String priority = parts[4].trim(); //not displayed for customer
                String assignedAgent = parts[5].trim(); //not displayed for customer
                String submittedBy = parts[6].trim();
                String dateSubmitted = parts[7].trim();

                //create ticket row
                GridPane ticektRow = new GridPane();
                ticektRow.setHgap(10);
                ticektRow.setVgap(5);
                ticektRow.setPadding(new Insets(5));
                ticektRow.setStyle("-fx-border-color: gray; -fx-background-color: #F5F5F5;");

                //for alignment text
                ColumnConstraints col1 = new ColumnConstraints();
                col1.setMinWidth(180); // subject label
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setMinWidth(100); // status Icon
                ColumnConstraints col3 = new ColumnConstraints();
                col3.setHgrow(Priority.ALWAYS); // description label (expandable)
                ColumnConstraints col4 = new ColumnConstraints();
                col4.setMinWidth(50); // arrow to view ticket
                ticektRow.getColumnConstraints().addAll(col1, col2, col3, col4);

                //subject of ticket
                Label subjectLabel = new Label(subject);
                subjectLabel.setMaxWidth(180);
                HBox.setHgrow(subjectLabel, Priority.NEVER);

                //status of ticket as a symbol using fontawesome
                FontIcon statusIcon = getStatusIcons(status);
                statusIcon.setIconSize(16);
                statusIcon.setIconColor(Color.BLACK);
                // Wrap status in an HBox for consistent layout
                HBox statusBox = new HBox(statusIcon);
                statusBox.setAlignment(Pos.CENTER_LEFT);
                statusBox.setPrefWidth(100);

                //description of ticket
                Label descLabel = new Label(description);
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(descLabel, Priority.NEVER);

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
                viewButton.setMinWidth(50);
                HBox.setHgrow(viewButton, Priority.NEVER);

                viewButton.setOnAction(e -> {
                    if (parentController != null) {
                        parentController.showTicketDetails(
                                id,
                                subject,
                                status,
                                dateSubmitted,
                                submittedBy,
                                description
                        );
                    } else {
                        System.out.println("Parent controller is null.");
                    }
                });



                ticektRow.add(subjectLabel, 0, 0);  // Column 0
                ticektRow.add(statusBox, 1, 0);     // Column 1
                ticektRow.add(descLabel, 2, 0);     // Column 2
                ticektRow.add(viewButton, 3, 0);    // Column 3

                ticketContainer.getChildren().add(ticektRow);

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    //sets TicketSorterController as the parent to allow functions to pass
    public void setParent(TicketSorterController parent) {
        this.parentController = parent;
        System.out.println("Parent controller set!");
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