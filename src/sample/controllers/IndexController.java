package sample.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.helpers.Frame;
import sample.helpers.Help;
import sample.models.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    GROUP PROJECT - ICS A, 21/03/2021    _________✔*/

public class IndexController implements Initializable {
    public User auth;
    @FXML private VBox navButtons, navIcons;
    @FXML private Label indexTitle;
    @FXML private ImageView imgMenuBtn;
    @FXML private ImageView imgHome, imgLaundryList, imgCategories, imgLogout, imgUsers, imgExitBtn;
    @FXML private JFXButton btnLogout, btnHome, btnLaundryList, btnCategories, btnUsers;
    @FXML private BorderPane mainPane;
    @FXML private AnchorPane navList, navOverlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navOverlay.setVisible(false);
        navOverlay.setOnMouseClicked(event -> closeNav());
        imgExitBtn.setOnMouseClicked(event -> System.exit(0));

        Platform.runLater(() -> {
            if(!auth.getType()) {
                navIcons.getChildren().remove(imgUsers);
                navButtons.getChildren().remove(btnUsers);
            }

            indexTitle.setText((auth.getTypeName() + " ~ " + auth.getName()));
            showPane("home", new HomeController(this.auth));
        });

        setImages();
        navTransition();
        navIconOnClick();
    }


    @FXML private void btnHomeAction() { showPane("home", new HomeController(this.auth)); }
    @FXML private void btnLaundryListAction() {
        showPane("laundry_list", new LaundryListController(this.auth));
    }
    @FXML private void btnCategoriesActions() { showPane("categories", new CategoryController(this.auth)); }
    @FXML private void btnUsersAction() {
        showPane("users", new UserController(this.auth));
    }
    @FXML private void btnLogoutAction() {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
        Frame.loginFrame();
    }



    private void navTransition() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(.5), navOverlay);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(.5), navList);
        translateTransition.setByX(-600);
        translateTransition.play();

        imgMenuBtn.setOnMouseClicked(event -> {
            navOverlay.setVisible(true);

            if(navList.getBoundsInParent().getMinX() == -560.0) {
                FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(.5), navOverlay);
                fadeTransition1.setFromValue(0);
                fadeTransition1.setToValue(0.15);
                fadeTransition1.play();
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(.5), navList);
                translateTransition1.setByX(+600);
                translateTransition1.play();
            } else if(navList.getBoundsInParent().getMinX() == 40) {
                closeNav();
            }
        });
    }

    private void showPane(String page, Object controller) {
        Pane view = Frame.loadFXML(page, controller);
        mainPane.setCenter(view);

        if(navList.getBoundsInParent().getMinX() == 40) {
            closeNav();
        }
    }

    private void closeNav() {
        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(.5), navOverlay);
        fadeTransition1.setFromValue(.15);
        fadeTransition1.setToValue(0);
        fadeTransition1.play();

        fadeTransition1.setOnFinished(event1 -> navOverlay.setVisible(false));

        TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(.5), navList);
        translateTransition1.setByX(-600);
        translateTransition1.play();
    }

    public void navIconOnClick() {
        imgHome.setOnMouseClicked(event -> showPane("home", new HomeController(this.auth)));
        imgLaundryList.setOnMouseClicked(event -> showPane("laundry_list", new LaundryListController(this.auth)));
        imgCategories.setOnMouseClicked(event -> showPane("categories", new CategoryController(this.auth)));
        imgUsers.setOnMouseClicked(event -> showPane("users", new UserController(this.auth)));
        imgLogout.setOnMouseClicked(event -> {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.close();
            Frame.loginFrame();
        });
    }

    public void setImages() {
        Help.loadImage("src/sample/Public/images/Nav/icons8-menu-48.png", imgMenuBtn);

        Help.loadImage("src/sample/Public/images/Nav/icons8-home-48.png", imgHome);
        Help.loadImage("src/sample/Public/images/Nav/icons8-todo-list.gif", imgLaundryList);
        Help.loadImage("src/sample/Public/images/Nav/icons8-checklist.gif", imgCategories);
        Help.loadImage("src/sample/Public/images/Nav/icons8-life-cycle.gif", imgUsers);
        Help.loadImage("src/sample/Public/images/Nav/icons8-power-off-button.gif", imgLogout);
    }

    /**
     * Gets the authenticated user and sets the auth attribute.
     *
     */
    public void setAuth(User auth) {
        this.auth = auth;
    }
}