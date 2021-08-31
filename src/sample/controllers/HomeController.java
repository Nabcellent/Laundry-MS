package sample.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import sample.database.MySql;
import sample.helpers.Help;
import sample.models.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * _____________________________________________________    Nabangi Michael - ICS B - 134694, 21/03/2021    _________✔*/


public class HomeController implements Initializable {
    public User auth;
    @FXML private ImageView homePaneImg;
    @FXML private Label lblGreeting, lblClaimedLaundry, lblCustomers, lblProfit;

    LocalTime currentTime = LocalTime.now();
    LocalTime morningStart = LocalTime.of(0, 1);
    LocalTime afternoonStart = LocalTime.of(12, 1);
    LocalTime eveningStart = LocalTime.of(18, 1);

    public HomeController(User auth) {
        this.auth = auth;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Help.loadImage("src/sample/Public/images/photo-1580175486077-959357d7c72e.jpeg", homePaneImg);

        Platform.runLater(() -> lblGreeting.setText("Good " + timePeriod() + "! " + capitalize(this.auth.getName())));

        setStatistics();
    }

    private void setStatistics() {
        String sqlClaimedLaundry = "SELECT COUNT(*) as count FROM laundry_list WHERE status = 'Claimed'";
        String sqlCustomers = "SELECT COUNT(*) as count FROM laundry_list WHERE cast(date_created as Date) = cast(NOW() as Date)";
        String sqlProfit = "SELECT SUM(amount_paid) as profit FROM laundry_list WHERE cast(date_created as Date) = cast(NOW() as Date)";

        try {
            Connection conn = MySql.dbConnect();

            ResultSet rsClaimedLaundry = Objects.requireNonNull(conn).createStatement().executeQuery(sqlClaimedLaundry);
            ResultSet rsCustomers = Objects.requireNonNull(conn).createStatement().executeQuery(sqlCustomers);
            ResultSet rsProfit = Objects.requireNonNull(conn).createStatement().executeQuery(sqlProfit);

            if (rsClaimedLaundry.next() && rsCustomers.next() && rsProfit.next()) {
                lblClaimedLaundry.setText(String.valueOf(rsClaimedLaundry.getInt("count")));
                lblCustomers.setText(String.valueOf(rsCustomers.getInt("count")));
                lblProfit.setText(String.valueOf(rsProfit.getInt("profit")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String timePeriod() {
        if(currentTime.isAfter(morningStart) && currentTime.isBefore(afternoonStart)) {
            return "Morning";
        } else if(currentTime.isAfter(afternoonStart) && currentTime.isBefore(eveningStart)) {
            return "Afternoon";
        }

        return "Evening";
    }

    private String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
