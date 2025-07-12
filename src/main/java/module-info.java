module apassignment.ticketsystem.ticketing {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires java.desktop;
    requires java.logging;

    opens apassignment to javafx.fxml;
    exports apassignment;

    opens apassignment.dashboard to javafx.fxml;
    opens apassignment.ticketingsystem to javafx.fxml;
    opens apassignment.usermanagement to javafx.fxml;

    exports apassignment.dashboard;
    exports apassignment.ticketingsystem;
    exports apassignment.usermanagement;

}