package com.example.todolist.dataModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ToDoData{
    private static final ToDoData instance=new ToDoData();
    private static final String filename="ToDoItem.txt";
    private ObservableList<ToDoItem> todoItems;
    private final DateTimeFormatter formatter;
    public static ToDoData getInstance(){
        return instance;
    }
    private ToDoData(){
        formatter=DateTimeFormatter.ofPattern(("yyyy-MM-dd"));
    }

    public ObservableList<ToDoItem> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(ToDoItem toDoItem){
        todoItems.add(toDoItem);
    }

    public void loadTodoItems() throws IOException {
        todoItems= FXCollections.observableArrayList();
        Path path= Paths.get(filename);
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String input;
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];
                LocalDate date = LocalDate.parse(dateString,formatter);
                ToDoItem toDoItem = new ToDoItem(shortDescription, details, date);
                todoItems.add(toDoItem);
            }
        }
    }
    public void storeTodoItems() throws IOException {
        Path path=Paths.get(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (ToDoItem item : todoItems) {
                bw.write(String.format("%S\t%s\t%s", item.getShortDescription(), item.getDetails(), item.getDeadline()));
                bw.newLine();
            }
        }
    }
    public void saveAsText(String filePath) throws IOException {
        Path path=Paths.get(filePath);
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            for (ToDoItem item : todoItems) {
                bw.write(String.format("%S\t%s\t%s", item.getShortDescription(), item.getDetails(), item.getDeadline()));
                bw.newLine();
            }
        }
    }
    public void saveAsPDF(String filePath) throws DocumentException, FileNotFoundException {
        Document document =new Document();
        PdfWriter writer=PdfWriter.getInstance(document,new FileOutputStream(filePath));
        document.open();
        for(ToDoItem item:todoItems){
            document.add(new Paragraph(String.format("%S {} %s {} %s\n", item.getShortDescription(), item.getDetails(), item.getDeadline())));
        }
        document.close();
        writer.close();
    }
    public ToDoItem updateTodoItem(ToDoItem item){
        if(todoItems.contains(item)){
            return todoItems.get(todoItems.indexOf(item));
        }
        return null;
    }
    public void deleteTodoItem(ToDoItem item){
        todoItems.remove(item);
    }
}