package com.example.todolist;

import com.example.todolist.dataModel.ToDoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        Scene scene = new Scene(fxmlLoader.load(), 900, 500);
        stage.setTitle("To Do List");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
try{
            ToDoData.getInstance().storeTodoItems();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init(){
        try{
            ToDoData.getInstance().loadTodoItems();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}