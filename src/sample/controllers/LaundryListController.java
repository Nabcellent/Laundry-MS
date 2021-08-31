package sample.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.database.MySql;
import sample.models.Category;
import sample.models.LaundryItem;
import sample.models.User;

import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    Nabangi Michael - ICS B - 134694, 21/03/2021    _________✔
 */


public class LaundryListController implements Initializable {
    public User auth;
    private Connection conn;
    private Statement statement;
    @FXML
    private Label lblChange;
    @FXML
    private TextField txtId, txtCustomer, txtRemarks, txtWeight, txtAmountPaid, txtTotalAmount, txtChange;
    @FXML
    private JFXComboBox<Category> cmbCategory;
    @FXML
    private JFXComboBox<String> cmbStatus;
    @FXML
    private TableView<LaundryItem> tblLaundryList;
    @FXML
    private TableColumn<LaundryItem, Integer> colId;
    @FXML
    private TableColumn<LaundryItem, String> colCustomer, colCategory, colStatus;
    @FXML
    private TableColumn<LaundryItem, Float> colWeight, colTotalAmount;
    @FXML
    private TableColumn<LaundryItem, Date> colDate;
    @FXML
    private JFXButton btnCreate, btnUpdate, btnDelete;

    public LaundryListController(User auth) {
        this.auth = auth;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::showLaundryItems);
        this.conn = MySql.dbConnect();

        populateComboBoxes();

        btnCreate.setOnMouseClicked(event -> createLaundry());
        btnUpdate.setOnMouseClicked(event -> updateLaundry());
        btnDelete.setOnMouseClicked(event -> deleteLaundry());
        tblLaundryList.setOnMouseClicked(event -> {
            LaundryItem laundryItem = tblLaundryList.getSelectionModel().getSelectedItem();
            txtCustomer.setText(laundryItem.getCustomer());
            txtRemarks.setText(laundryItem.getRemarks());
            txtWeight.setText(String.valueOf(laundryItem.getWeight()));
            txtAmountPaid.setText(String.valueOf(laundryItem.getAmountPaid()));
            txtId.setText(String.valueOf(laundryItem.getId()));
            txtTotalAmount.setText(String.valueOf(laundryItem.getTotalAmount()));

            setChange(laundryItem.getAmountPaid() - laundryItem.getTotalAmount());
        });
    }

    private void populateComboBoxes() {
        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        String categoriesSql = "SELECT * FROM categories";

        try {
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(categoriesSql);

            while (rs.next()) {
                categoryList.add(new Category(rs.getInt("id"), rs.getString("title"), rs.getFloat("price")));
            }

            cmbCategory.setItems(categoryList);
            cmbCategory.setCellFactory(lv -> new CategoryCell());
            cmbCategory.setButtonCell(new CategoryCell());
            cmbStatus.getItems().addAll("Pending", "Ongoing", "Ready", "Claimed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showLaundryItems() {
        ObservableList<LaundryItem> LaundryItemsList = FXCollections.observableArrayList();
        Connection conn = MySql.dbConnect();
        String sql = "SELECT ll.*, li.weight, li.unit_price, lc.title FROM laundry_list ll " +
                "INNER JOIN laundry_items li ON ll.id = li.laundry_id " +
                "INNER JOIN categories lc on li.category_id = lc.id " +
                "ORDER BY status, id;";

        try {
            assert conn != null;
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while (results.next()) {
                LaundryItemsList.add(new LaundryItem(
                        results.getInt("id"),
                        results.getString("customer_name"),
                        results.getString("title"),
                        results.getFloat("weight"),
                        results.getFloat("amount_paid"),
                        results.getFloat("total"),
                        results.getString("status"),
                        results.getDate("date_created")
                ));
            }

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
            colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));

            tblLaundryList.setItems(LaundryItemsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLaundry() {
        if (Objects.equals(txtRemarks.getText(), "")) txtRemarks.setText("none");

        float weight = Float.parseFloat(txtWeight.getText()),
                amountPaid = Objects.equals(txtAmountPaid.getText(), "") ? 0 : Float.parseFloat(txtAmountPaid.getText()),
                unitPrice = cmbCategory.valueProperty().get().getPrice(),
                totalPrice = weight * unitPrice;

        String sql = "INSERT INTO `laundry_list` (`customer_name`, `total`, `amount_paid`, `remarks`, `date_created`) " +
                "VALUES ('" + txtCustomer.getText() + "', " + totalPrice + ", " + txtAmountPaid.getText() + ", '" + txtRemarks.getText() + "', NOW())";

        try {
            statement = conn.createStatement();
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet result = statement.getGeneratedKeys();

            if (result.next()) {
                int categoryId = cmbCategory.valueProperty().get().getId();
                int laundryId = result.getInt(1);

                sql = "INSERT INTO laundry_items(laundry_id, category_id, weight, unit_price) " +
                        "VALUES (" + laundryId + ", " + categoryId + ", " + weight + ", " + unitPrice + ")";

                conn.createStatement().executeUpdate(sql);
                showLaundryItems();
                setChange(amountPaid - totalPrice);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateLaundry() {
        if (Objects.equals(txtRemarks.getText(), "")) txtRemarks.setText("none");

        float weight = Float.parseFloat(txtWeight.getText()),
                amountPaid = Objects.equals(txtAmountPaid.getText(), "") ? 0 : Float.parseFloat(txtAmountPaid.getText()),
                unitPrice = cmbCategory.valueProperty().get().getPrice(),
                totalPrice = weight * unitPrice;
        int categoryId = cmbCategory.valueProperty().get().getId();

        String sql = "UPDATE laundry_list SET customer_name = '" + txtCustomer.getText() + "', " +
                "status = '" + cmbStatus.getValue() + "', total = '" + totalPrice + "', paid = " + (amountPaid >= totalPrice) + " " +
                "WHERE id = " + txtId.getText();

        try {
            statement = conn.createStatement();
            statement.executeUpdate(sql);

            sql = "UPDATE laundry_items SET category_id = " + categoryId + ", weight = " + weight + ", unit_price = " + unitPrice + " " +
                    "WHERE laundry_id = " + txtId.getText();

            conn.createStatement().executeUpdate(sql);
            showLaundryItems();
            setChange(amountPaid - totalPrice);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteLaundry() {
        String sql = "DELETE FROM laundry_list WHERE id = " + txtId.getText();

        try {
            conn.createStatement().executeUpdate(sql);
            showLaundryItems();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void setChange(float change) {
        if (change > 0) {
            lblChange.setVisible(true);
            txtChange.setText(String.valueOf(change));
            txtChange.setVisible(true);
        } else {
            lblChange.setVisible(false);
            txtChange.setVisible(false);
        }
    }

    static class CategoryCell extends ListCell<Category> {
        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);

            setText(item == null ? "" : item.title);
        }
    }
}
