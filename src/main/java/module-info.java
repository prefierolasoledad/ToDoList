module com.example.todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf;

    opens com.example.todolist to javafx.fxml;
    exports com.example.todolist;
}