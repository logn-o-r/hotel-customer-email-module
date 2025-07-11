package apassignment.ticketingsystem;

import apassignment.util.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketController {

    //place to put the ticket hbox into
    @FXML
    private VBox ticketContainer;

    //to load the new ticket form into
    @FXML
    private StackPane mainContent;

    //for setting the new ticket button and form
    @FXML
    private VBox secondaryContent;
    private boolean isNewTicketFormOpen = false;

    @FXML
    Button newTicketButton;

    @FXML
    private Label assignedAgentLabel;
    @FXML
    private Label priorityLabel;

    private String currentUserID = UserSession.getCurrentUserID();

    @FXML
    public void initialize() {
        String currentUserID = UserSession.getCurrentUserID();
        loadTicketsFromFile("ticket.txt");
        if (currentUserID.startsWith("ADM") || currentUserID.startsWith("AGT")) {
            newTicketButton.setVisible(false); // hides new ticket button for agents and admin
        }
    }

    //set TicketSorterController as parent to allow the arrow button to switch scenes
    private TicketSorterController parentController;

    //used to filter displayed tickets by status
    private List<String[]> allTicketData = new ArrayList<>(); //to store parsed lines

    public void loadTicketsFromFile(String filename) {
        //to store in case the ticket needs to be modified on display (inconsistency prevention)
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) { //while loop to stop reading once file has no more lines

                //ticket data format: TicketID | Subject | Status | Description | Priority | AssignedAgentUserID | SubmittedByUserID | dateSubmitted
                //divides the lines into parts based on order of data in ticket meant to be displayed.
                String[] parts = line.split("\\|");
                if (parts.length < 8) continue; //skips the line in file if the data is not in correct format
                allTicketData.add(parts); // store ticket data for filtering

                //data from ticket.txt displayed on ui
                String id = parts[0].trim();
                String subject = parts[1].trim();
                String status = parts[2].trim();
                String description = parts[3].trim();
                String priority = parts[4].trim(); //not displayed for customer
                String assignedAgent = parts[5].trim(); //not displayed for customer
                String submittedBy = parts[6].trim();
                String dateSubmitted = parts[7].trim();

                String currentStatus = status; //for when status is changed
                //check if open ticket has a response, as to change it to in-progress
                if (status.equalsIgnoreCase("Open") && hasResponse(id)) {
                    currentStatus = "In-Progress";
                    parts[2] = currentStatus;
                }
                String updatedLine = String.join(" | ", Arrays.stream(parts).map(String::trim).toArray(String[]::new));
                updatedLines.add(updatedLine);
                //to stop an error as status needs to be 'effectively final'
                String finalStatus = currentStatus;

                //create ticket row
                GridPane ticektRow = new GridPane();
                ticektRow.setPrefWidth(640); // Fixed width for row
                ticektRow.setMaxWidth(640);
                ticektRow.setMinWidth(640);
                ticektRow.setHgap(10);
                ticektRow.setVgap(5);
                ticektRow.setPadding(new Insets(5));
                ticektRow.setStyle("-fx-border-color: gray; -fx-background-color: #FFFFFF;");

                //for alignment text
                ColumnConstraints col1 = new ColumnConstraints(120);// subject label
                ColumnConstraints col2 = new ColumnConstraints(40);// status Icon
                ColumnConstraints col3 = new ColumnConstraints();
                col3.setHgrow(Priority.ALWAYS); // description label (expandable)
                ColumnConstraints col4 = new ColumnConstraints(180); //priority + assigned agent + arrow
                col4.setMinWidth(50); // arrow to view ticket
                ticektRow.getColumnConstraints().addAll(col1, col2, col3, col4);

                //subject of ticket
                Label subjectLabel = new Label(subject);
                subjectLabel.setMaxWidth(120);
                subjectLabel.setWrapText(false);
                subjectLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

                //status of ticket as a symbol using fontawesome
                FontIcon statusIcon = getStatusIcons(finalStatus);
                HBox statusBox = new HBox(statusIcon); //wraps icon in a hbox for a consistant layout
                statusBox.setAlignment(Pos.CENTER);
                statusBox.setMinWidth(30);

                //description of ticket
                Label descLabel = new Label(description);
                descLabel.setWrapText(false);
                descLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
                descLabel.setTooltip(new Tooltip(description));

                // right side : (priorty + assigned agent) + view button
                HBox rightBox = new HBox(5);
                rightBox.setAlignment(Pos.CENTER_RIGHT);

                //show priority and assigned agent only if agent or admin
                if (currentUserID.startsWith("ADM") || currentUserID.startsWith("AGT")) {
                    //header for priority and assigned agent
                    assignedAgentLabel.setVisible(true);
                    priorityLabel.setVisible(true);

                    //to stop description from overruning
                    descLabel.setMaxWidth(200);

                    // Priority
                    FontIcon priorityIcon = getPriorityIcon(priority);
                    HBox priorityBox = new HBox(priorityIcon);
                    priorityBox.setAlignment(Pos.CENTER_LEFT);
                    priorityBox.setMinWidth(60);

                    // Assigned agent, using button as a box
                    Button agentBtn = new Button(assignedAgent);
                    agentBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                    agentBtn.setMinWidth(80);

                    rightBox.getChildren().addAll(priorityBox, agentBtn);
                }

                // ticekt view button
                Button viewButton = new Button();
                FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
                arrowIcon.setIconSize(16);
                arrowIcon.setIconColor(Color.BLACK);
                viewButton.setGraphic(arrowIcon);
                viewButton.setMinWidth(30);
                rightBox.getChildren().add(viewButton);

                viewButton.setOnAction(e -> {
                    if (parentController != null) {
                        parentController.showTicketDetails(
                                id,
                                subject,
                                status,
                                dateSubmitted,
                                priority,
                                assignedAgent,
                                submittedBy,
                                description
                        );
                    } else {
                        System.out.println("Parent controller is null.");
                    }
                });

                ticektRow.add(subjectLabel, 0, 0);  // column 0
                ticektRow.add(statusBox, 1, 0);     // column 1
                ticektRow.add(descLabel, 2, 0);     // column 2
                ticektRow.add(rightBox, 3, 0);      // column 4


                ticketContainer.getChildren().add(ticektRow);
            }

        }
        catch (IOException e) {
            //to show user there was an error when reading or loading tickets from file
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to read and load from ticket.txt", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Read and Load Tickets");
            alert.setContentText("An error occurred while reading and loading the tickets. Please try again later.");
            alert.showAndWait();

        }
        //to overwrite and update ticket file with updated info
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ticket.txt"))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to update ticket.txt", e);
        }

    }

    //for making sure status is assigned properly as open tickets are tickets without a response
    //while tickets with responses are considered in-progress
    private boolean hasResponse(String ticketID) {
        try (BufferedReader br = new BufferedReader(new FileReader("response.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].trim().equals(ticketID)) {
                    return true; // ticket has a response
                }
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to read from response.txt", e);
        }
        return false; // ticket has no response attched
    }


    //used to filter displayed tickets by status
    public void filterTickets(Predicate<String[]> filterCondition) {
        ticketContainer.getChildren().clear();

        for (String[] parts : allTicketData) {
            if (parts.length < 8) continue;

            String id = parts[0].trim();
            String subject = parts[1].trim();
            String status = parts[2].trim();
            String description = parts[3].trim();
            String priority = parts[4].trim();
            String assignedAgent = parts[5].trim();
            String submittedBy = parts[6].trim();
            String dateSubmitted = parts[7].trim();

            //put filers condition
            if (!filterCondition.test(parts)) { continue; }

            //ticket row builder from loadTicketsFromFile
            //create ticket row
            GridPane ticektRow = new GridPane();
            ticektRow.setPrefWidth(640); // Fixed width for row
            ticektRow.setMaxWidth(640);
            ticektRow.setMinWidth(640);
            ticektRow.setHgap(10);
            ticektRow.setVgap(5);
            ticektRow.setPadding(new Insets(5));
            ticektRow.setStyle("-fx-border-color: gray; -fx-background-color: #FFFFFF;");

            //for alignment text
            ColumnConstraints col1 = new ColumnConstraints(120);// subject label
            ColumnConstraints col2 = new ColumnConstraints(40);// status Icon
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHgrow(Priority.ALWAYS); // description label (expandable)
            ColumnConstraints col4 = new ColumnConstraints(180); //priority + assigned agent + arrow
            col4.setMinWidth(50); // arrow to view ticket
            ticektRow.getColumnConstraints().addAll(col1, col2, col3, col4);

            //subject of ticket
            Label subjectLabel = new Label(subject);
            subjectLabel.setMaxWidth(120);
            subjectLabel.setWrapText(false);
            subjectLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

            //status of ticket as a symbol using fontawesome
            FontIcon statusIcon = getStatusIcons(status);
            HBox statusBox = new HBox(statusIcon); //wraps icon in a hbox for a consistant layout
            statusBox.setAlignment(Pos.CENTER);
            statusBox.setMinWidth(30);

            //description of ticket
            Label descLabel = new Label(description);
            descLabel.setWrapText(false);
            descLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            descLabel.setTooltip(new Tooltip(description));

            // right side : (priorty + assigned agent) + view button
            HBox rightBox = new HBox(5);
            rightBox.setAlignment(Pos.CENTER_RIGHT);

            //show priority and assigned agent only if agent or admin
            if (currentUserID.startsWith("ADM") || currentUserID.startsWith("AGT")) {
                //header for priority and assigned agent
                assignedAgentLabel.setVisible(true);
                priorityLabel.setVisible(true);

                //to stop description from overruning
                descLabel.setMaxWidth(200);

                // Priority
                FontIcon priorityIcon = getPriorityIcon(priority);
                HBox priorityBox = new HBox(priorityIcon);
                priorityBox.setAlignment(Pos.CENTER_LEFT);
                priorityBox.setMinWidth(60);

                // Assigned agent, using button as a box
                Button agentBtn = new Button(assignedAgent);
                agentBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                agentBtn.setMinWidth(80);

                rightBox.getChildren().addAll(priorityBox, agentBtn);
            }

            // ticekt view button
            Button viewButton = new Button();
            FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
            arrowIcon.setIconSize(16);
            arrowIcon.setIconColor(Color.BLACK);
            viewButton.setGraphic(arrowIcon);
            viewButton.setMinWidth(30);
            rightBox.getChildren().add(viewButton);

            viewButton.setOnAction(e -> {
                if (parentController != null) {
                    parentController.showTicketDetails(
                            id,
                            subject,
                            status,
                            dateSubmitted,
                            priority,
                            assignedAgent,
                            submittedBy,
                            description
                    );
                } else {
                    System.out.println("Parent controller is null.");
                }
            });

            ticektRow.add(subjectLabel, 0, 0);  // column 0
            ticektRow.add(statusBox, 1, 0);     // column 1
            ticektRow.add(descLabel, 2, 0);     // column 2
            ticektRow.add(rightBox, 3, 0);      // column 4


            ticketContainer.getChildren().add(ticektRow);
        }
    }

    //to filter by status and userID
    public void filterTicketsStatusUser(String statusFilter, String userIdFilter) {
        //same code as filterTicekts
        ticketContainer.getChildren().clear();

        for (String[] parts : allTicketData) {
            if (parts.length < 8) continue;

            String id = parts[0].trim();
            String subject = parts[1].trim();
            String status = parts[2].trim();
            String description = parts[3].trim();
            String priority = parts[4].trim();
            String assignedAgent = parts[5].trim();
            String submittedBy = parts[6].trim();
            String dateSubmitted = parts[7].trim();

            //filters by status
            if (!status.equalsIgnoreCase(statusFilter)) continue;

            //to filter by which is the current logged-in user
            // if user is customer it shows by who submitted the ticket
            if (userIdFilter != null && userIdFilter.startsWith("CUST")) {
                if (!submittedBy.equals(userIdFilter)) continue;
            }
            else if (userIdFilter != null && userIdFilter.startsWith("AGT")) {
                if (!assignedAgent.equals(userIdFilter)) continue;
            }
            //ticket row builder from loadTicketsFromFile
            //create ticket row
            GridPane ticektRow = new GridPane();
            ticektRow.setPrefWidth(640); // Fixed width for row
            ticektRow.setMaxWidth(640);
            ticektRow.setMinWidth(640);
            ticektRow.setHgap(10);
            ticektRow.setVgap(5);
            ticektRow.setPadding(new Insets(5));
            ticektRow.setStyle("-fx-border-color: gray; -fx-background-color: #FFFFFF;");

            //for alignment text
            ColumnConstraints col1 = new ColumnConstraints(120);// subject label
            ColumnConstraints col2 = new ColumnConstraints(40);// status Icon
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHgrow(Priority.ALWAYS); // description label (expandable)
            ColumnConstraints col4 = new ColumnConstraints(180); //priority + assigned agent + arrow
            col4.setMinWidth(50); // arrow to view ticket
            ticektRow.getColumnConstraints().addAll(col1, col2, col3, col4);

            //subject of ticket
            Label subjectLabel = new Label(subject);
            subjectLabel.setMaxWidth(120);
            subjectLabel.setWrapText(false);
            subjectLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

            //status of ticket as a symbol using fontawesome
            FontIcon statusIcon = getStatusIcons(status);
            HBox statusBox = new HBox(statusIcon); //wraps icon in a hbox for a consistant layout
            statusBox.setAlignment(Pos.CENTER);
            statusBox.setMinWidth(30);

            //description of ticket
            Label descLabel = new Label(description);
            descLabel.setWrapText(false);
            descLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            descLabel.setTooltip(new Tooltip(description));

            // right side : (priorty + assigned agent) + view button
            HBox rightBox = new HBox(5);
            rightBox.setAlignment(Pos.CENTER_RIGHT);

            //show priority and assigned agent only if agent or admin
            if (currentUserID.startsWith("ADM") || currentUserID.startsWith("AGT")) {
                //header for priority and assigned agent
                assignedAgentLabel.setVisible(true);
                priorityLabel.setVisible(true);

                //to stop description from overruning
                descLabel.setMaxWidth(200);

                // Priority
                FontIcon priorityIcon = getPriorityIcon(priority);
                HBox priorityBox = new HBox(priorityIcon);
                priorityBox.setAlignment(Pos.CENTER_LEFT);
                priorityBox.setMinWidth(60);

                // Assigned agent, using button as a box
                Button agentBtn = new Button(assignedAgent);
                agentBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                agentBtn.setMinWidth(80);

                rightBox.getChildren().addAll(priorityBox, agentBtn);
            }

            // ticekt view button
            Button viewButton = new Button();
            FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
            arrowIcon.setIconSize(16);
            arrowIcon.setIconColor(Color.BLACK);
            viewButton.setGraphic(arrowIcon);
            viewButton.setMinWidth(30);
            rightBox.getChildren().add(viewButton);

            viewButton.setOnAction(e -> {
                if (parentController != null) {
                    parentController.showTicketDetails(
                            id,
                            subject,
                            status,
                            dateSubmitted,
                            priority,
                            assignedAgent,
                            submittedBy,
                            description
                    );
                } else {
                    System.out.println("Parent controller is null.");
                }
            });

            ticektRow.add(subjectLabel, 0, 0);  // column 0
            ticektRow.add(statusBox, 1, 0);     // column 1
            ticektRow.add(descLabel, 2, 0);     // column 2
            ticektRow.add(rightBox, 3, 0);      // column 4


            ticketContainer.getChildren().add(ticektRow);
        }
    }

    //used to filter displayed tickets by status
    public void searchFilterTickets(Predicate<String[]> filterCondition, String userIdFilter) {
        ticketContainer.getChildren().clear();

        for (String[] parts : allTicketData) {
            if (parts.length < 8) continue;

            String id = parts[0].trim();
            String subject = parts[1].trim();
            String status = parts[2].trim();
            String description = parts[3].trim();
            String priority = parts[4].trim();
            String assignedAgent = parts[5].trim();
            String submittedBy = parts[6].trim();
            String dateSubmitted = parts[7].trim();

            //put filers condition
            if (!filterCondition.test(parts)) continue;

            //to filter by which is the current logged-in user
            // if user is customer it shows by who submitted the ticket
            if (userIdFilter != null && userIdFilter.startsWith("CUST")) {
                if (!submittedBy.equals(userIdFilter)) continue;
            }
            else if (userIdFilter != null && userIdFilter.startsWith("AGT")) {
                if (!assignedAgent.equals(userIdFilter)) continue;
            }

            if (userIdFilter != null && userIdFilter.equals("close")) {
                if (status.equals("Closed")) continue;
            }

            //ticket row builder from loadTicketsFromFile
            //create ticket row
            GridPane ticektRow = new GridPane();
            ticektRow.setPrefWidth(640); // Fixed width for row
            ticektRow.setMaxWidth(640);
            ticektRow.setMinWidth(640);
            ticektRow.setHgap(10);
            ticektRow.setVgap(5);
            ticektRow.setPadding(new Insets(5));
            ticektRow.setStyle("-fx-border-color: gray; -fx-background-color: #FFFFFF;");

            //for alignment text
            ColumnConstraints col1 = new ColumnConstraints(120);// subject label
            ColumnConstraints col2 = new ColumnConstraints(40);// status Icon
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHgrow(Priority.ALWAYS); // description label (expandable)
            ColumnConstraints col4 = new ColumnConstraints(180); //priority + assigned agent + arrow
            col4.setMinWidth(50); // arrow to view ticket
            ticektRow.getColumnConstraints().addAll(col1, col2, col3, col4);

            //subject of ticket
            Label subjectLabel = new Label(subject);
            subjectLabel.setMaxWidth(120);
            subjectLabel.setWrapText(false);
            subjectLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

            //status of ticket as a symbol using fontawesome
            FontIcon statusIcon = getStatusIcons(status);
            HBox statusBox = new HBox(statusIcon); //wraps icon in a hbox for a consistant layout
            statusBox.setAlignment(Pos.CENTER);
            statusBox.setMinWidth(30);

            //description of ticket
            Label descLabel = new Label(description);
            descLabel.setWrapText(false);
            descLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            descLabel.setTooltip(new Tooltip(description));

            // right side : (priorty + assigned agent) + view button
            HBox rightBox = new HBox(5);
            rightBox.setAlignment(Pos.CENTER_RIGHT);

            //show priority and assigned agent only if agent or admin
            if (currentUserID.startsWith("ADM") || currentUserID.startsWith("AGT")) {
                //header for priority and assigned agent
                assignedAgentLabel.setVisible(true);
                priorityLabel.setVisible(true);

                //to stop description from overruning
                descLabel.setMaxWidth(200);

                // Priority
                FontIcon priorityIcon = getPriorityIcon(priority);
                HBox priorityBox = new HBox(priorityIcon);
                priorityBox.setAlignment(Pos.CENTER_LEFT);
                priorityBox.setMinWidth(60);

                // Assigned agent, using button as a box
                Button agentBtn = new Button(assignedAgent);
                agentBtn.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                agentBtn.setMinWidth(80);

                rightBox.getChildren().addAll(priorityBox, agentBtn);
            }

            // ticekt view button
            Button viewButton = new Button();
            FontIcon arrowIcon = new FontIcon(FontAwesome.ARROW_RIGHT);
            arrowIcon.setIconSize(16);
            arrowIcon.setIconColor(Color.BLACK);
            viewButton.setGraphic(arrowIcon);
            viewButton.setMinWidth(30);
            rightBox.getChildren().add(viewButton);

            viewButton.setOnAction(e -> {
                if (parentController != null) {
                    parentController.showTicketDetails(
                            id,
                            subject,
                            status,
                            dateSubmitted,
                            priority,
                            assignedAgent,
                            submittedBy,
                            description
                    );
                } else {
                    System.out.println("Parent controller is null.");
                }
            });

            ticektRow.add(subjectLabel, 0, 0);  // column 0
            ticektRow.add(statusBox, 1, 0);     // column 1
            ticektRow.add(descLabel, 2, 0);     // column 2
            ticektRow.add(rightBox, 3, 0);      // column 4


            ticketContainer.getChildren().add(ticektRow);
        }
    }

    //sets TicketSorterController as the parent to allow functions to pass
    public void setParent(TicketSorterController parent) {
        this.parentController = parent;
        //System.out.println("Parent controller set!");
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
        iconView.setIconSize(16);
        return iconView;
    }

    private FontIcon getPriorityIcon(String priority) {
        FontIcon iconView;
        switch (priority.trim().toLowerCase()) {
            case "high":
                iconView = new FontIcon(FontAwesome.EXCLAMATION_CIRCLE);
                iconView.setIconColor(Color.RED);
                break;
            case "medium":
                iconView = new FontIcon(FontAwesome.EXCLAMATION_CIRCLE);
                iconView.setIconColor(Color.ORANGE);
                break;
            case "low":
                iconView = new FontIcon(FontAwesome.EXCLAMATION_CIRCLE);
                iconView.setIconColor(Color.GREEN);
                break;
            default:
                iconView = new FontIcon(FontAwesome.QUESTION_CIRCLE);
                iconView.setIconColor(Color.BLUE);
                break;
        }
        iconView.setIconSize(16);
        return iconView;
    }

    //opens the new ticket creation form
    @FXML
    private void handleNewTicketButton() {
        if (isNewTicketFormOpen) return; // prevents duplicates from opening if button is clicked multiple

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/apassignment/fxml/ticketingsystem/newTicketForm.fxml"));
            Parent formRoot = loader.load();

            // Get controller to set necessary references if needed
            newTicketController formController = loader.getController();
            formController.setParentController(this);
            formController.setSubmittedByUserID(currentUserID);
            formController.setOnCloseCallback(() -> isNewTicketFormOpen = false); // Optional clean-up

            Stage formStage = new Stage();
            formStage.setTitle("Create New Ticket Form");
            formStage.setScene(new Scene(formRoot));
            formStage.setResizable(false);
            formStage.initModality(Modality.APPLICATION_MODAL); // to block interaction with main window
            formStage.show();

            isNewTicketFormOpen = true;

            // When closed manually
            formStage.setOnHidden(e -> isNewTicketFormOpen = false);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to run newTicketForm.fxml.", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scene Load Error");
            alert.setHeaderText("Could Not Open New Ticket Form");
            alert.setContentText("An error occurred while trying to open the ticket form. Please try again later.");
            alert.showAndWait();
        }
    }


    //to clsoe the new ticket form
    public void closeNewTicketForm() {
        secondaryContent.getChildren().clear();
        isNewTicketFormOpen = false;
    }

    //to refresh ticket list
    public void reloadTickets() {
        // re-reads the ticket.txt file to display updated ticket list
        List<String[]> newData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("ticket.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    newData.add(parts);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to read and load from ticket.txt", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Read and Load Tickets");
            alert.setContentText("An error occurred while fetching lastest ticket data. Please try again later.");
            alert.showAndWait();
        }

        allTicketData = newData;
        filterTickets(parts -> parts[6].trim().equals(currentUserID));
    }


    //function to load fxml file into the main content of app
//    private void loadView() {
//        try{
//            Node content = FXMLLoader.load(getClass().getResource(/apassignment/fxml/ticketingsystem/newTicket.fxml));
//            mainContent.getChildren().setAll(content);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}