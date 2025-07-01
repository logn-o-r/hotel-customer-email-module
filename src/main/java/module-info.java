module apassignment.ticketsystem.ticketing {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;

    opens apassignment.ticketsystem.ticketing to javafx.fxml;
    exports apassignment.ticketsystem.ticketing;
}