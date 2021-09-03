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
import sample.helpers.Help;
import sample.models.Category;
import sample.models.LaundryItem;
import sample.models.User;

import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    GROUP PROJECT - ICS A, 21/03/2021    _________✔*/


public class LaundryListController implements Initializable {
    public User auth;
    private Connection conn;
    private Statement statement;
    @FXML private Label lblChange, lblError;
    @FXML private TextField txtId, txtCustomer, txtWeight, txtAmountPaid, txtTotal, txtChange;
    @FXML private JFXComboBox<Category> cmbCategory;
    @FXML private JFXComboBox<String> cmbStatus;
    @FXML private TableView<LaundryItem> tblLaundryList;
    @FXML private TableColumn<LaundryItem, Integer> colId;
    @FXML private TableColumn<LaundryItem, String> colCustomer, colCategory, colStatus;
    @FXML private TableColumn<LaundryItem, Float> colWeight, colTotalAmount;
    @FXML private TableColumn<LaundryItem, Date> colDate;
    @FXML private JFXButton btnCreate, btnUpdate, btnDelete, btnReset;
    private int categoryId;
    private String customerInput, statusInput;
    private float weightInput, amountPaidInput, unitPrice, totalPrice, change;

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
        btnReset.setOnMouseClicked(event -> {
            txtId.clear(); txtCustomer.clear(); txtWeight.clear();
            txtAmountPaid.clear();txtTotal.clear();txtChange.clear();

            cmbCategory.getSelectionModel().clearSelection();
            cmbStatus.getSelectionModel().clearSelection();

            btnCreate.setDisable(false); btnUpdate.setDisable(true); btnDelete.setDisable(true);
        });
        tblLaundryList.setOnMouseClicked(event -> {
            btnCreate.setDisable(true); btnUpdate.setDisable(false); btnDelete.setDisable(false);

            LaundryItem laundryItem = tblLaundryList.getSelectionModel().getSelectedItem();
            txtCustomer.setText(laundryItem.getCustomer());
            txtWeight.setText(String.valueOf(laundryItem.getWeight()));
            txtAmountPaid.setText(String.valueOf(laundryItem.getAmountPaid()));
            txtId.setText(String.valueOf(laundryItem.getId()));

            ObservableList<Category> newCats = FXCollections.observableArrayList();
            ObservableList<String> newStatuses = FXCollections.observableArrayList();

            cmbCategory.getItems().forEach((cat) -> {
                if (cat.getId() == laundryItem.getCategoryId()) {
                    newCats.add(0, cat);
                } else {
                    newCats.add(cat);
                }
            });

            cmbStatus.getItems().forEach((status) -> {
                if (Objects.equals(status, laundryItem.getStatus())) {
                    newStatuses.add(0, status);
                } else {
                    newStatuses.add(status);
                }
            });

            cmbCategory.setItems(newCats);
            cmbCategory.getSelectionModel().selectFirst();
            cmbStatus.setItems(newStatuses);
            cmbStatus.getSelectionModel().selectFirst();

            txtTotal.setText(String.valueOf(laundryItem.getTotalAmount()));
            txtChange.setText(String.valueOf(laundryItem.getAmountPaid() - laundryItem.getTotalAmount()));
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
        String sql = "SELECT ll.*, li.weight, li.unit_price, lc.title, lc.id as catId, lc.price FROM laundry_list ll " +
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
                        results.getInt("catId"),
                        results.getString("customer_name"),
                        results.getString("title"),
                        results.getFloat("price"),
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

    boolean isValid() {
        Help.setMessageTimer(5, lblError);

        this.customerInput = txtCustomer.getText();

        if(customerInput.equals("") || txtWeight.getText().equals("") || cmbCategory.getSelectionModel().isEmpty() || cmbStatus.getSelectionModel().isEmpty()) {
            lblError.setText("Please fill in all required fields");
            return false;
        }

        if(!Help.isNumeric(txtWeight.getText())) {
            lblError.setText("Weight must be numeric");
            return false;
        }

        if(!txtAmountPaid.getText().isEmpty() && !Help.isNumeric(txtAmountPaid.getText())){
            lblError.setText("Amount paid must be numeric or left blank if no amount has been paid");
            return false;
        } else {
            this.amountPaidInput = Objects.equals(txtAmountPaid.getText(), "") ? 0 : Float.parseFloat(txtAmountPaid.getText());
        }

        this.categoryId = cmbCategory.valueProperty().get().getId();
        this.statusInput = cmbStatus.getValue();
        this.weightInput = Float.parseFloat(txtWeight.getText());
        this.unitPrice = cmbCategory.valueProperty().get().getPrice();
        this.totalPrice = this.weightInput * this.unitPrice;

        txtTotal.setText(String.valueOf(this.weightInput * this.unitPrice));
        txtChange.setText(String.valueOf(this.amountPaidInput - totalPrice));

        lblError.setText("");
        return true;
    }

    private void createLaundry() {
        if(isValid()) {
            String sql = "INSERT INTO `laundry_list` (`customer_name`, `total`, `amount_paid`, `date_created`) " +
                    "VALUES ('" + this.customerInput + "', " + this.totalPrice + ", " + this.amountPaidInput + ", NOW())";

            try {
                statement = conn.createStatement();
                statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                ResultSet result = statement.getGeneratedKeys();

                if (result.next()) {
                    int laundryId = result.getInt(1);

                    sql = "INSERT INTO laundry_items(laundry_id, category_id, weight, unit_price) " +
                            "VALUES (" + laundryId + ", " + this.categoryId + ", " + this.weightInput + ", " + this.unitPrice + ")";

                    conn.createStatement().executeUpdate(sql);
                    showLaundryItems();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void updateLaundry() {
        if(isValid()) {
            String sql = "UPDATE laundry_list SET customer_name = '" + this.customerInput + "', " +
                    "status = '" + this.statusInput + "', total = '" + this.totalPrice + "', paid = " + (this.amountPaidInput >= this.totalPrice) + " " +
                    "WHERE id = " + txtId.getText();

            try {
                statement = conn.createStatement();
                statement.executeUpdate(sql);

                sql = "UPDATE laundry_items SET category_id = " + this.categoryId + ", weight = " + this.weightInput + ", unit_price = " + this.unitPrice + " " +
                        "WHERE laundry_id = " + txtId.getText();

                conn.createStatement().executeUpdate(sql);
                showLaundryItems();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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

    static class CategoryCell extends ListCell<Category> {
        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);

            setText(item == null ? "" : item.title);
        }
    }
}
