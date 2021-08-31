package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sample.helpers.Frame;
import sample.helpers.Help;
import sample.models.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    GROUP PROJECT - ICS A, 21/03/2021    _________✔*/


public class LoginController implements Initializable {
    @FXML private Label lblErrorMessage, lblLoginMessage;
    @FXML private Button btnLogin, btnCancel;
    @FXML private ImageView brandingImageView, lockImageView;
    @FXML private TextField txtUsername;
    @FXML private PasswordField pwdPassword;

    //========================================    Initialize Frame
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Help.loadImage("src/sample/Public/images/login.jpg", brandingImageView);
        Help.loadImage("src/sample/Public/images/e56b841924ac729935e858cb59535fb7.png", lockImageView);
    }

    //========================================    Event Handlers
    @FXML
    private void btnLoginOnAction() {
        if(isValidForm()) {
            User auth = new User(txtUsername.getText(), pwdPassword.getText()).authenticate();

            Frame.indexFrame(auth);
            Stage stage = (Stage)btnLogin.getScene().getWindow();
            stage.close();
        } else {
            Help.setMessageTimer(7, lblErrorMessage);
        }
    }

    @FXML
    private void btnCancelOnAction() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }



    //========================================    Form Actions
    private boolean isValidForm() {
        String username = txtUsername.getText();
        String password = pwdPassword.getText();

        if(username.isEmpty() || password.isEmpty()) {
            lblErrorMessage.setText("Please fill in all fields!");
            return false;
        }

        return true;
    }


    /*  SETTER  */
    public void setLblLoginMessage(String message) {
        lblLoginMessage.setText(message);
        Help.setMessageTimer(4, lblLoginMessage);
    }
}