
module org.taskmanager.taskmanager {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens org.taskmanager.taskmanager to javafx.fxml;
    opens org.taskmanager.taskmanager.controller to javafx.fxml;
    opens org.taskmanager.taskmanager.model to com.fasterxml.jackson.databind;
    exports org.taskmanager.taskmanager;
}