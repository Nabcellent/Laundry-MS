package sample.models;

import sample.database.MySql;

import javax.swing.*;
import java.sql.*;

public class User {
    private int id, phone;
    private boolean type;
    private String name, username, password, typeName;

    public User(int id, String name, String username, int phone, boolean type) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.type = type;
        this.phone = phone;
        this.typeName = type ? "Admin" : "Staff";
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User authenticate() {
        Connection link = MySql.dbConnect();

        String verifyLogin = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "' LIMIT 1";

        try {
            assert link != null;
            Statement statement = link.createStatement();
            ResultSet qryResult = statement.executeQuery(verifyLogin);

            if (qryResult.next()) {
                return new User(
                        qryResult.getInt("id"),
                        qryResult.getString("name"),
                        qryResult.getString("username"),
                        qryResult.getInt("phone"),
                        qryResult.getBoolean("type")
                );
            } else {
                throw new Exception("Invalid Credentials");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return new User(username, password);
        }
    }

    public boolean changePassword(String newPass, JFrame parentComponent) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection link = MySql.dbConnect()) {
            PreparedStatement statement = link.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newPass);
            statement.setInt(2, this.id);

            return statement.executeUpdate() == 1;
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(parentComponent, e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    public boolean checkCurrentPassword(String password) {
        String sql = "SELECT password FROM users WHERE id = " + this.id + " LIMIT 1";

        try (Connection link = MySql.dbConnect()) {
            Statement statement = link.createStatement();
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                return result.getString("password").equals(password);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public boolean getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phone=" + phone +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
