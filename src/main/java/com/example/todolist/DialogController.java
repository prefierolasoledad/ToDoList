package com.example.todolist;

import com.example.todolist.dataModel.ToDoData;
import com.example.todolist.dataModel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    protected ToDoItem processResult(){
        String shortDescription=shortDescriptionField.getText().trim();
        String details=detailsArea.getText().trim();
        LocalDate deadlineValue=deadlinePicker.getValue();
        ToDoItem newItem=new ToDoItem(shortDescription,details,deadlineValue);
        ToDoData.getInstance().addTodoItem(newItem);
        return newItem;
    }
    @FXML
    public void setValue(ToDoItem item){
        shortDescriptionField.setText(item.getShortDescription());
        detailsArea.setText(item.getDetails());
        deadlinePicker.setValue(item.getDeadline());
    }
    @FXML
    public ToDoItem updateItem(ToDoItem item){
        ToDoItem item2=ToDoData.getInstance().updateTodoItem(item);
        item2.setShortDescription(shortDescriptionField.getText().trim());
        item2.setDetails(detailsArea.getText().trim());
        item2.setDeadline(deadlinePicker.getValue());
        ToDoData.getInstance().deleteTodoItem(item);
        ToDoData.getInstance().addTodoItem(item2);
        return item2;

    }
}
