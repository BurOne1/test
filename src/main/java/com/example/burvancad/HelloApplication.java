package com.example.burvancad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Label label = new Label();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1146, 773);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try{
            File file = new File("dotsTest.txt");
            if(!file.exists()){
                file.createNewFile();
            } else
                System.out.print("Good 222 ");

            if(!file.createNewFile()) {
                System.out.print("Good \n");
            } else {
                System.out.print("Error 222");
            }

            // write
            PrintWriter pw = new PrintWriter(file);
            pw.println("8");
            pw.println("0.0" + " " + "0.0" + " " + "0.0");
            pw.println("1.0" + " " + "0.0" + " " + "0.0");
            pw.println("0.0" + " " + "1.0" + " " + "0.0");
            pw.println("1.0" + " " + "1.0" + " " + "0.0");
            pw.println("0.0" + " " + "0.0" + " " + "1.0");
            pw.println("1.0" + " " + "0.0" + " " + "1.0");
            pw.println("1.0" + " " + "1.0" + " " + "1.0");
            pw.println("0.0" + " " + "1.0" + " " + "1.0");
            pw.println("12");

//            pw.println("0" + " " + "1" + " " + "3" + " " + "2" + " " + "0");
//            pw.println("0" + " " + "4" + " " + "5" + " " + "1" + " " + "0");
//            pw.println("4" + " " + "5" + " " + "6" + " " + "7" + " " + "4");
            //square
            pw.println("0" + " " + "1");
            pw.println("1" + " " + "3");
            pw.println("3" + " " + "2");
            pw.println("2" + " " + "0");
            pw.println("0" + " " + "4");
            pw.println("4" + " " + "5");
            pw.println("5" + " " + "6");
            pw.println("5" + " " + "1");
            pw.println("6" + " " + "3");
            pw.println("6" + " " + "7");
            pw.println("7" + " " + "2");
            pw.println("7" + " " + "4");


            pw.close();


        } catch (IOException e) {
            System.out.print("Error: " + e);
        }
        launch();
    }
}