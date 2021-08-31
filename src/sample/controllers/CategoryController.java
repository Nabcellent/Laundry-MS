package sample.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.database.MySql;
import sample.models.Category;
import sample.models.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    GROUP PROJECT - ICS A, 21/03/2021    _________✔*/


public class CategoryController implements Initializable {
    public User auth;
    private Connection conn;
    @FXML private TextField txtId, txtTitle, txtPrice;
    @FXML private TableView<Category> tblCategories;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colTitle;
    @FXML private TableColumn<Category, Float> colPrice;
    @FXML private JFXButton btnCreate, btnUpdate, btnDelete;

    public CategoryController(User auth) {
        this.auth = auth;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::showCategories);
        this.conn = MySql.dbConnect();

        btnCreate.setOnMouseClicked(event -> createCategory());
        btnUpdate.setOnMouseClicked(event -> updateCategory());
        btnDelete.setOnMouseClicked(event -> deleteCategory());
        tblCategories.setOnMouseClicked(event -> {
            Category category = tblCategories.getSelectionModel().getSelectedItem();
            txtTitle.setText(category.getTitle());
            txtPrice.setText(String.valueOf(category.getPrice()));
            txtId.setText(String.valueOf(category.getId()));
        });
    }

    public void showCategories() {
        ObservableList<Category> CategoriesList = FXCollections.observableArrayList();
        Connection conn = MySql.dbConnect();
        String sql = "SELECT * FROM categories ORDER BY title;";

        try {
            assert conn != null;
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while(results.next()) {
                CategoriesList.add(new Category(
                        results.getInt("id"),
                        results.getString("title"),
                        results.getFloat("price")
                ));
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            tblCategories.setItems(CategoriesList);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createCategory() {
        String sql = "INSERT INTO `categories` (`title`, `price`) " +
                "VALUES ('" + txtTitle.getText() + "', " + txtPrice.getText() + ")";

        try {
            conn.createStatement().executeUpdate(sql);
            showCategories();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateCategory() {
        String sql = "UPDATE categories SET title = '" + txtTitle.getText() + "', price = " + txtPrice.getText() +
                " WHERE id = " + txtId.getText();

        try {
            conn.createStatement().executeUpdate(sql);
            showCategories();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteCategory() {
        String sql = "DELETE FROM categories WHERE id = " + txtId.getText();

        try {
            conn.createStatement().executeUpdate(sql);
            showCategories();

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
