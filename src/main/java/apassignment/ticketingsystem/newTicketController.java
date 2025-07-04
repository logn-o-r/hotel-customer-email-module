package apassignment.ticketingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class newTicketController {

    @FXML
    private TextField ticketSubjectInput;
    @FXML
    private TextArea ticketDescInput;

    @FXML
    private AnchorPane ticketFormRoot;

    private TicketController parentController;
    private String submittedByUserID;
    private Runnable onCloseCallback;

    public void setParentController(TicketController parent) {
        this.parentController = parent;
    }

    public void setSubmittedByUserID(String userID) {
        this.submittedByUserID = userID;
    }

    @FXML
    private void handleSubmitTicket() {
        String subject = ticketSubjectInput.getText().trim();
        String description = ticketDescInput.getText().trim();

        if (subject.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Both the ticket subject and description must be filled before submitting the ticket");
            alert.showAndWait();
            return;
        }

        String ticketID = generateUniqueTicketID();
        String status = "Open";
        String priority = "Low";
        String assignedAgent = "null";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String ticketLine = ticketID + " | " + subject+ " | " + status + " | " + description + " | " + priority + " | " + assignedAgent + " | " + submittedByUserID + " | " + date;

        //saves the new ticket in ticket.txt as a new line
        try {
            Files.write(Paths.get("ticket.txt"), Collections.singletonList(ticketLine), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            parentController.reloadTickets();
        } catch (IOException e) {
            //top inform user of error
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "File writing error", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Submission Error");
            alert.setHeaderText("Unable to Submit Ticket");
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();

        }

        closeForm(); // close panel
    }

    //for the cancel button to close the form
    @FXML
    private void handleCancel() {
        closeForm();
    }

    //creates a new unique id for the ticket when it is created
    //by reading all ticket id and creating a new number following the TCK000 format
    private String generateUniqueTicketID() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("ticket.txt"));
            int maxId = lines.stream()
                    .map(line -> line.split("\\|")[0].trim().replace("TCK", ""))
                    .filter(id -> id.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);
            return String.format("TCK%03d", maxId + 1);
        } catch (IOException e) {
            return "TCK001";
        }
    }


    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    // to close the form when it's submiited or close/cancel button pressed
    public void closeForm() {
        if (onCloseCallback != null) onCloseCallback.run();
        ((Stage) ticketFormRoot.getScene().getWindow()).close();
    }
}

