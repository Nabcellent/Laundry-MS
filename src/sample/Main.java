package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.helpers.Frame;

/**
 * _____________________________________________________    GROUP PROJECT - ICS A, 21/03/2021    _________✔*/

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Frame.loginFrame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * TOPICS USED
 *
 * GUI
 * JDBC
 * ERROR HANDLING
 * THREADS
*/