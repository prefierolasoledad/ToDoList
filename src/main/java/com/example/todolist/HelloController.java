package com.example.todolist;

import com.example.todolist.dataModel.ToDoData;
import com.example.todolist.dataModel.ToDoItem;
import com.itextpdf.text.DocumentException;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

public class HelloController {
    @FXML
    private ListView<ToDoItem> toDoListView;
    @FXML
    private TextArea itemsDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList;
    private final DateTimeFormatter df=DateTimeFormatter.ofPattern("MMMM dd,yyyy");
    @FXML
    public void initialize(){
        listContextMenu=new ContextMenu();
        MenuItem deleteMenuItem=new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });
        MenuItem updateMenu=new MenuItem("Edit");
        updateMenu.setOnAction(actionEvent -> {
            ToDoItem item=toDoListView.getSelectionModel().getSelectedItem();
            updateItem(item);
        });

        listContextMenu.getItems().addAll(updateMenu,deleteMenuItem);
        toDoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, toDoItem, t1) -> {
            if(t1!=null){
                ToDoItem item=toDoListView.getSelectionModel().getSelectedItem();
                itemsDetailsTextArea.setText(item.getDetails());

                deadlineLabel.setText(df.format(item.getDeadline()));
            }
        });
        filteredList=new FilteredList<>(ToDoData.getInstance().getTodoItems(), toDoItem -> true);
        SortedList<ToDoItem> sortedList=new SortedList<>(filteredList, Comparator.comparing(ToDoItem::getDeadline));
        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();
        toDoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {

                ListCell<ToDoItem> cell= new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean b) {
                        super.updateItem(toDoItem, b);
                        if(b){
                            setText(null);
                        }else{
                            setText(toDoItem.getShortDescription());
                            if(toDoItem.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                            }else if(toDoItem.getDeadline().isBefore(LocalDate.now())){
                                setTextFill(Color.RED);
                            }else if(toDoItem.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BROWN);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty)->{
                            if(isNowEmpty){
                                cell.setContextMenu(null);
                            }else{
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
    }
    @FXML
    public void showNewDialog(){
        Dialog<ButtonType> dialog =new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("ADD NEW TODO ITEM");
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch(IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Optional<ButtonType> result=dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            DialogController controller=fxmlLoader.getController();
            toDoListView.getSelectionModel().select(controller.processResult());
        }else{
            dialog.close();
        }
    }
    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        ToDoItem selectedItem=toDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }
    @FXML
    public void onClickListView(){
        ToDoItem toDoItem=toDoListView.getSelectionModel().getSelectedItem();
        itemsDetailsTextArea.setText(toDoItem.getDetails());
        deadlineLabel.setText(df.format(toDoItem.getDeadline()));
    }
    @FXML
    public void updateItem(ToDoItem item){
        Dialog<ButtonType> dialog =new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("UPDATE A TODO ITEM");

        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch(IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
        DialogController controller=fxmlLoader.getController();
        controller.setValue(item);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        Optional<ButtonType> result=dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update Todo Item");
            alert.setHeaderText("UpdateItem : "+ item.getShortDescription());
            alert.setContentText("Are you sure ?");
            Optional<ButtonType> result2=alert.showAndWait();
            if(result2.isPresent() && result2.get()==ButtonType.OK){
                toDoListView.getSelectionModel().select(controller.updateItem(item));
            }else{
                alert.close();
            }
        }else{
            dialog.close();
        }
    }
    @FXML
    public void deleteItem(ToDoItem item){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete Item : "+ item.getShortDescription());
        alert.setContentText("Are you sure ?");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            ToDoData.getInstance().deleteTodoItem(item);
        }else{
            alert.close();
        }
    }
    @FXML
    public void handleFilterButton(){
        ToDoItem selectedItem=toDoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()){
            filterToggleButton.setText("All Items");
            filteredList.setPredicate(toDoItem -> (toDoItem.getDeadline().equals(LocalDate.now())));
            if(filteredList.isEmpty()){
                itemsDetailsTextArea.clear();
                deadlineLabel.setText("");
            }else if(filteredList.contains(selectedItem)){
                toDoListView.getSelectionModel().select(selectedItem);
            }else{
                toDoListView.getSelectionModel().selectFirst();
            }
        }else{
            filterToggleButton.setText("Today's Items");
            filteredList.setPredicate(toDoItem -> true);
            toDoListView.getSelectionModel().select(selectedItem);
        }
    }
    @FXML
    public void saveTheFile() throws IOException, DocumentException {
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Save Data");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text","*.txt"),new FileChooser.ExtensionFilter("PDF","*.pdf"));
        File file=chooser.showSaveDialog(mainBorderPane.getScene().getWindow());
        if(file!=null && chooser.getSelectedExtensionFilter().getExtensions().equals(new FileChooser.ExtensionFilter("Text","*.txt").getExtensions())){
            ToDoData.getInstance().saveAsText(file.getPath());
        }else if(file!=null && chooser.getSelectedExtensionFilter().getExtensions().equals(new FileChooser.ExtensionFilter("PDF","*.pdf").getExtensions())){
            ToDoData.getInstance().saveAsPDF(file.getPath());
        }
    }
    @FXML
    public void handleExit(){
        Platform.exit();
    }
}