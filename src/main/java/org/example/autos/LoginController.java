package org.example.autos;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController  {

    @FXML
    private TextField NameTb;

    @FXML
    private PasswordField PasswordTb;

    @FXML
    private ComboBox<String> RoleCmb;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        RoleCmb.setItems(FXCollections.observableArrayList("Director", "Seller", "Mechanic"));
    }

    public void Connect() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/isko", "postgres", "postgres");
            System.out.println("Connected to the database successfully!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, "Database connection failed!", ex);
        }
    }

    public void handleSignInButtonAction(ActionEvent event) {
        String username = NameTb.getText().trim();
        String password = PasswordTb.getText().trim();
        String role = RoleCmb.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Validation Error", "Please fill in all fields!");
            return;
        }

        try {
            Connect();
            String sql = "SELECT * FROM users WHERE name = ? AND password = ? AND role = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            rs = pst.executeQuery();

            if (rs.next()) {
                switch (role) {
                    case "Director":
                        openWindow("director.fxml", "Director Panel");
                        break;
                    case "Seller":
                        openWindow("seller.fxml", "Seller Panel");
                        break;
                    case "Mechanic":
                        openWindow("toservice.fxml", "Mechanic Panel");
                        break;
                }
            } else {
                showAlert("Login Failed", "Invalid username, password, or role!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openWindow(String fxmlFile, String title) {
        try {

            URL resourceUrl = getClass().getResource(fxmlFile);
            if (resourceUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) RoleCmb.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open window: " + fxmlFile + ". Error: " + e.getMessage());
        }
    }
}