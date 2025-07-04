package apassignment.ticketingsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketDetailController implements Initializable {

    @FXML
    private Label ticektSubjectTitle;
    @FXML
    private HBox statusContainer;
    @FXML
    private Label dateCreated;
    @FXML
    private Label userSubmitted;
    @FXML
    private VBox descriptionContainer;
    @FXML
    private VBox responseContainer;
    @FXML
    private Button backButton;
    @FXML
    private Button ticketCloseButton;

    @FXML
    private VBox responseTextBox;
    @FXML
    private TextArea responseText;
    @FXML
    private Button addResponseButton;

    private String ticketID;

    private String ticketStatus;

    private TicketSorterController parentController;

    private Pane parentContainer;


    public void setTicketDetails(String id, String subject, String status, String createdDate, String userId, String description) {

        this.ticketID = id;
        //sets the title of ticekt to the ticket's subject
        ticektSubjectTitle.setText(subject);

        this.ticketStatus = status;
        //sets the status symbol next to its symbol
        FontIcon statusIcon = getStatusIcons(status);
        statusIcon.setIconSize(16);
        statusIcon.setIconColor(Color.WHITE);
        Label statusLabel = new Label(" " + status);
        statusLabel.setFont(new Font(16));
        statusLabel.setTextFill(Color.WHITE);
        statusContainer.getChildren().addAll(statusIcon, statusLabel);

        //sets the date it was created
        dateCreated.setText("Created on : " + createdDate);

        //sets the user who created the ticket
        userSubmitted.setText("Submitted by : " + userId);

        //sets the description of ticket
        Text desx = new Text(description);
        desx.setWrappingWidth(500);
        descriptionContainer.getChildren().add(desx);

        //if ticket is closed, no button to close the ticket
        if(status.equalsIgnoreCase("Closed") && (ticketCloseButton != null)) {
            ticketCloseButton.setVisible(false);
            ticketCloseButton.setManaged(false);
        }

        //load responses to ticket
        loadResponsesFromFile();

    }

    //loads data from response.txt
    private void loadResponsesFromFile() {
        responseContainer.getChildren().clear();

        String lastUser = "";
        try (BufferedReader br = new BufferedReader(new FileReader("response.txt"))) {
            //response data structure: ticketID | responderID | response | dateResponded
            //follows similar structure to loading ticket data
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 4) continue;
                //breaks the line into parts of the response data
                String responseTicketId = parts[0].trim();
                String responder = parts[1].trim();
                String response = parts[2].trim();
                String date = parts[3].trim();

                if (!responseTicketId.equals(ticketID)) continue;

                lastUser = responder;

                // Builds the response card, makes them purple like in UI Wireframe
                VBox card = new VBox();
                card.setStyle("-fx-background-color: #D3BFFF; -fx-padding: 10; -fx-spacing: 5; -fx-background-radius: 10;");

                Label authorLabel = new Label(responder);
                authorLabel.setStyle("-fx-font-weight: bold");

                Text messageText = new Text(response);
                messageText.setWrappingWidth(500);

                Label dateLabel = new Label("Responded on : " + date);
                dateLabel.setStyle("-fx-font-size: 10");

                card.getChildren().addAll(authorLabel, messageText, dateLabel);
                responseContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to load responses from file.", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Read Error");
            alert.setHeaderText("Unable to Read and Load Ticket Responses");
            alert.setContentText("There was a problem reading the responses. Please try again later.");
            alert.showAndWait();

        }

        // Check if last user who responded was an agent
        if (lastUser.startsWith("AGT") && (ticketCloseButton != null)) {
            addResponseButton.setVisible(true);
            addResponseButton.setManaged(true);
        } else {
            addResponseButton.setVisible(false);
            addResponseButton.setManaged(false);
        }
    }

    //function for 'add response' button, to display the response textbox n submit button
    @FXML
    private void showResponseInputnButton() {
        responseTextBox.setVisible(true);
        responseTextBox.setManaged(true);
    }

    @FXML
    private void handleSubmitResponse() {
        String content = responseText.getText().trim();
        if (content.isEmpty()) return;

        String responderID = "CUST001"; // note: replace with currently logged in userID
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("response.txt", true))) {
            writer.write("\n"+ticketID + " | " + responderID + " | " + content + " | " + date);
            writer.newLine();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to submit response.", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Submission Failed");
            alert.setHeaderText("Could not submit the response");
            alert.setContentText("An error occurred while submitting the response. Please try again later.");
            alert.showAndWait();
        }

        responseText.clear();
        responseTextBox.setVisible(false);
        responseTextBox.setManaged(false);

        loadResponsesFromFile(); // refreshs the list
    }

    //turns makes the corresponding icon for the status of ticket
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

    public void setParentController(TicketSorterController parentController) {
        this.parentController = parentController;
    }

    //event for close ticket button
    @FXML
    private void handleCloseTicket() {
        //confirmation prompt for input validation and user statisfaction
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Close Ticket");
        confirmation.setHeaderText("Are you sure you want to close this ticket?");
        confirmation.setContentText("This action cannot be undone.");

        ButtonType yesButton = new ButtonType("Yes"); //sets ticket to close
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE); //clsoes alert

        confirmation.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            closeTicketAndSave(); //updates ticket status to close
        }
    }

    private void closeTicketAndSave() {
        this.ticketStatus = "Closed"; // Update status
        updateTicketStatusInFile(ticketID, "Closed"); //method to update ticket status

        ticketCloseButton.setDisable(true); //removes close ticket button

        //refresh ticket details
        parentController.showTicketDetails(
                ticketID,
                ticektSubjectTitle.getText(),
                "Closed",
                dateCreated.getText().replace("Created on : ", ""),
                userSubmitted.getText().replace("Submitted by : ", ""),
                ((Text) descriptionContainer.getChildren().get(0)).getText()
        );
    }

    private void updateTicketStatusInFile(String ticketId, String newStatus) {
        Path path = Paths.get("ticket.txt");
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8 && parts[0].trim().equals(ticketId)) {
                    parts[2] = newStatus; // update ticket status
                    line = String.join(" | ", parts); // writes the new status
                }
                updatedLines.add(line);
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to read current ticket status.", e);
        }

        try {
            Files.write(path, updatedLines); //updates ticket.txt file
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to update and save new ticket status.", e);
        }
    }

    //to allow TicketDetailsController to have access to parent container
    public void setParentContainer(Pane parent) {
        this.parentContainer = parent;
    }


    //button to go back to ticket row view
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set icon for back button
        backButton.setText("");
        FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_LEFT);
        arrowIcon.setIconSize(16);
        arrowIcon.setIconColor(Color.BLACK);
        backButton.setGraphic(arrowIcon);
        backButton.setMinWidth(50);
        HBox.setHgrow(backButton, Priority.NEVER);

        backButton.setOnAction(event -> {
            parentController.loadView("/apassignment/fxml/ticketingsystem/Ticket.fxml");
        });
    }

}
