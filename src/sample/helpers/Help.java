package sample.helpers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import sample.database.MySql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * _____________________________________________________    Nabangi Michael - ICS B - 134694, 21/03/2021    _________✔*/


public class Help {
    //++++++++++    VARIABLES
    private static final String PHONE_PATTERN = "((^0[17]+)|(^[17]+)).*";

    /**
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++    METHODS
     * */
    public static void setMessageTimer(int durationInSeconds, Label label) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(durationInSeconds), ev -> label.setText("")));
        timeline.play();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) return false;

        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static boolean isPhoneNumber(String phone) {
        return phone.matches(PHONE_PATTERN);
    }

    public static void loadImage(String path, ImageView imageView) {
        File imageFile = new File(path);
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }

    public static boolean phoneExists(int phone, int id) {
        Connection connection = MySql.dbConnect();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        String query;

        if(id == 0) {
            query = "SELECT phone FROM users where phone = '" + phone + "' LIMIT 1";
        } else {
            query = "SELECT id, phone FROM users where phone = '" + phone + "' AND id != " + id + " LIMIT 1";
        }

        try {
            assert false;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }


    public static boolean validatePhone(int userId, String phone, Label lblProfileError) {
        if(phone != null && !phone.trim().isEmpty()) {
            if(!isNumeric(phone)) {
                lblProfileError.setText("Phone number has to be an integer!");
                return true;
            }

            if(!isPhoneNumber(phone)) {
                lblProfileError.setText("Invalid phone number");
                return true;
            }

            if(phone.length() < 9) {
                lblProfileError.setText("Phone must be at least 9 digits");
                return true;
            } else if(phone.length() > 10) {
                lblProfileError.setText("Phone must be at most 10 digits");
                return true;
            }

            if(phoneExists(Integer.parseInt(phone), userId)) {
                lblProfileError.setText("Phone number already in use.");
                return true;
            }
        }
        return false;
    }
}
