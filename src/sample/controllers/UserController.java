package sample.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.database.MySql;
import sample.models.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    Nabangi Michael - ICS B - 134694, 21/03/2021    _________✔*/


public class UserController implements Initializable {
    public User auth;
    private Connection conn;
    @FXML private TextField txtId, txtName, txtUsername, txtPhone;
    @FXML private JFXComboBox<UserType> cmbUserType;
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, Integer> colId, colPhone;
    @FXML private TableColumn<User, String> colName, colUsername, colUserType;
    @FXML private JFXButton btnCreate, btnUpdate, btnDelete;

    public UserController(User auth) {
        this.auth = auth;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::showUsers);
        this.conn = MySql.dbConnect();

        ObservableList<UserType> categoryList = FXCollections.observableArrayList();
        categoryList.add(new UserType(true, "Admin"));
        categoryList.add(new UserType(false, "Staff"));

        cmbUserType.setItems(categoryList);
        cmbUserType.setCellFactory(lv -> new UserTypeCell());
        cmbUserType.setButtonCell(new UserTypeCell());

        btnCreate.setOnMouseClicked(event -> createUser());
        btnUpdate.setOnMouseClicked(event -> updateUser());
        btnDelete.setOnMouseClicked(event -> deleteUser());
        tblUsers.setOnMouseClicked(event -> {
            User user = tblUsers.getSelectionModel().getSelectedItem();
            txtId.setText(String.valueOf(user.getId()));
            txtName.setText(user.getName());
            txtUsername.setText(user.getUsername());
            txtPhone.setText(String.valueOf(user.getPhone()));

            ObservableList<UserType> type = FXCollections.observableArrayList();

            if(user.getType()) {
                type.add(new UserType(true, "Admin"));
                type.add(new UserType(false, "Staff"));
            } else {
                type.add(new UserType(false, "Staff"));
                type.add(new UserType(true, "Admin"));
            }

            cmbUserType.setItems(type);
            cmbUserType.getSelectionModel().selectFirst();
        });
    }

    public void showUsers() {
        ObservableList<User> UsersList = FXCollections.observableArrayList();
        Connection conn = MySql.dbConnect();
        String sql = "SELECT * FROM users ORDER BY name, id;";

        try {
            assert conn != null;
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while(results.next()) {
                UsersList.add(new User(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getString("username"),
                        results.getInt("phone"),
                        results.getBoolean("type")
                ));
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            colUserType.setCellValueFactory(new PropertyValueFactory<>("typeName"));

            tblUsers.setItems(UsersList);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createUser() {
        int phone = Objects.equals(txtPhone.getText(), "") ? 0 : Integer.parseInt(txtPhone.getText());
        boolean isAdmin = cmbUserType.valueProperty().get().isAdmin();

        String sql = "INSERT INTO `users` (`name`, `username`, `phone`, `password`, `type`) " +
                "VALUES ('" + txtName.getText() + "', '" + txtUsername.getText() + "', " + phone + ", 'LMS', " + isAdmin + ")";

        try {
            conn.createStatement().executeUpdate(sql);
            showUsers();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void updateUser() {
        int phone = Objects.equals(txtPhone.getText(), "") ? 0 : Integer.parseInt(txtPhone.getText());
        boolean isAdmin = cmbUserType.valueProperty().get().isAdmin();

        String sql = "UPDATE users SET name = '" + txtName.getText() + "', " +
                "username = '" + txtUsername.getText() + "', phone = " + phone + ", type = " + isAdmin + " " +
                "WHERE id = " + txtId.getText();

        try {
            conn.createStatement().executeUpdate(sql);
            showUsers();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteUser() {
        String sql = "DELETE FROM users WHERE id = " + txtId.getText();

        try {
            conn.createStatement().executeUpdate(sql);
            showUsers();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static class UserType {
        public String title;
        public boolean isAdmin;

        public UserType(boolean isAdmin, String title) {
            this.isAdmin = isAdmin;
            this.title = title;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setAdmin(boolean admin) {
            isAdmin = admin;
        }
    }

    static class UserTypeCell extends ListCell<UserType> {
        @Override
        protected void updateItem(UserType item, boolean empty) {
            super.updateItem(item, empty);

            setText(item == null ? "" : item.title);
        }
    }
}
