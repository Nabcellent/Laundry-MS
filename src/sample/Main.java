package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.helpers.Frame;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Frame.loginFrame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
