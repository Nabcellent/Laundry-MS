package sample.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controllers.IndexController;
import sample.models.User;

import java.util.Objects;


/**
 * _____________________________________________________    Nabangi Michael - ICS B - 134694, 21/03/2021    _________✔*/


public class Frame {
    //==========================================    Login Frame
    public Frame(){}

    public static void loginFrame() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Frame.class.getResource("../views/login.fxml")));
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root, 1000, 700));
            loginStage.show();
        } catch(Exception e) {
            e.getCause();
            e.printStackTrace();
        }
    }

    //==========================================    Index Frame
    public static void indexFrame(User auth) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Frame.class.getResource("../views/index.fxml")));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(loader.load(), 1300, 700));

            IndexController controller = loader.getController();
            controller.setAuth(auth);

            stage.show();
        } catch(Exception e) {
            e.getCause();
            e.printStackTrace();
        }
    }

    public static Pane loadFXML(String fileName, Object controller) {
        Pane view = null;

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Frame.class.getResource("../views/" + fileName + ".fxml")));
            loader.setController(controller);

            view = loader.load();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error fetching " + fileName + ".fxml... Please check Loader.");
        }

        return view;
    }
}
