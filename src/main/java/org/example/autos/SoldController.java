package org.example.autos;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.awt.*;
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
import javax.swing.*;
import javafx.scene.Node;


public class SoldController extends Component implements Initializable{
    @FXML private Button returnBtn;

    @FXML private TableView<SoldCar> SoldTable;
    @FXML private TableColumn<SoldCar, String> brandCol;
    @FXML private TableColumn<SoldCar, String> modelCol;
    @FXML private TableColumn<SoldCar, Double> priceCol;
    @FXML private TableColumn<SoldCar, Double> taxCol;
    @FXML private TableColumn<SoldCar, Double> commCol;
    @FXML private TableColumn<SoldCar, String> avaiCol;

    Connection Con;
    private ObservableList<SoldCar> soldcarList = FXCollections.observableArrayList();
    public void Connect() {
        try {

            Class.forName("org.postgresql.Driver");


            Con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/isko",
                    "postgres",
                    "postgres"
            );

            System.out.println("Connected to the database successfully!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DirectorController.class.getName()).log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(DirectorController.class.getName()).log(Level.SEVERE, "Database connection failed!", ex);
        }
    }


    public void DisplaySoldCars() {
        soldcarList.clear();

        String query = "SELECT id, brand, model, body_type, trans, price, tax, commision, availability FROM sold_cars";

        try (PreparedStatement pst = Con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                SoldCar soldCar = new SoldCar(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getDouble("price"),
                        rs.getDouble("tax"),
                        rs.getDouble("commision"),
                        rs.getString("availability")
                );
                soldcarList.add(soldCar);
            }


            SoldTable.setItems(soldcarList);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load sold cars from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect();
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        taxCol.setCellValueFactory(new PropertyValueFactory<>("tax"));
        commCol.setCellValueFactory(new PropertyValueFactory<>("commision"));
        avaiCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        DisplaySoldCars();
    }
    public void returnBtn(ActionEvent actionEvent) {
        SoldCar selectedCar = SoldTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            JOptionPane.showMessageDialog(null, "Please select a car to return.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            String selectQuery = "SELECT body_type, trans, date, commision, tax FROM sold_cars WHERE id = ?";
            PreparedStatement selectStmt = Con.prepareStatement(selectQuery);
            selectStmt.setInt(1, selectedCar.getId());
            ResultSet rs = selectStmt.executeQuery();

            String bodyType = null;
            String trans = null;
            String date = null;
            double commission = 0.0;
            double tax = 0.0;

            if (rs.next()) {
                bodyType = rs.getString("body_type");
                trans = rs.getString("trans");
                date = rs.getString("date");  // Извлекаем дату из базы данных
                commission = rs.getDouble("commision"); // Извлекаем комиссию
                tax = rs.getDouble("tax"); // Извлекаем налог
            }


            if (commission == 0.0) {
                commission = selectedCar.getPrice() * 0.01;
            }


            if (tax == 0.0) {
                tax = selectedCar.getPrice() * 0.005;
            }


            String insertQuery = "INSERT INTO cars (id, brand, model, body_type, trans, status, price, commision, tax, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = Con.prepareStatement(insertQuery);
            insertStmt.setInt(1, selectedCar.getId());
            insertStmt.setString(2, selectedCar.getBrand());
            insertStmt.setString(3, selectedCar.getModel());
            insertStmt.setString(4, bodyType);
            insertStmt.setString(5, trans);
            insertStmt.setString(6, "Available");
            insertStmt.setDouble(7, selectedCar.getPrice());
            insertStmt.setDouble(8, commission);
            insertStmt.setDouble(9, tax);
            insertStmt.setString(10, date);


            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Car inserted successfully into the cars table.");
            } else {
                System.out.println("No rows inserted.");
            }


            String deleteQuery = "DELETE FROM sold_cars WHERE id = ?";
            PreparedStatement deleteStmt = Con.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, selectedCar.getId());
            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Car deleted successfully from sold_cars table.");
            } else {
                System.out.println("No rows deleted.");
            }


            DisplaySoldCars();

            JOptionPane.showMessageDialog(null, "Car returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while returning car.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void goToSelldCars(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Seller.fxml"));
            Parent root = fxmlLoader.load();
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 878, 592);
            currentStage.setScene(newScene);
            currentStage.setTitle("Seller");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load Seller scene.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void reportBtn(ActionEvent actionEvent) {


            String query = "SELECT COUNT(*) AS total_cars, SUM(commision) AS total_commission, SUM(price) AS total_sales FROM sold_cars";

            try (PreparedStatement pst = Con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    int totalCars = rs.getInt("total_cars");
                    double totalCommission = rs.getDouble("total_commission");
                    double totalSales = rs.getDouble("total_sales");


                    String reportMessage = "Sales Report:\n\n" +
                            "Total Cars Sold: " + totalCars + "\n" +
                            "Total Commission: $" + String.format("%.2f", totalCommission) + "\n" +
                            "Total Sales: $" + String.format("%.2f", totalSales);


                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sales Report");
                    alert.setHeaderText(null);
                    alert.setContentText(reportMessage);


                    alert.showAndWait();
                }

            } catch (SQLException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error while generating report");
                alert.setContentText("An error occurred while generating the sales report.");
                alert.showAndWait();
            }
        }


    public void goToLogin(MouseEvent mouseEvent) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();


            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();


            Scene newScene = new Scene(root, 525, 428);
            currentStage.setScene(newScene);
            currentStage.setTitle("Login");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Failed to load Login scene.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}


