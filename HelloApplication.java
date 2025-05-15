package com.example.demo3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        LesothoTourView tourView = new LesothoTourView();
        Scene scene = new Scene(tourView.getView(), 1200, 800);

        stage.setTitle("Lesotho Tour Viewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
