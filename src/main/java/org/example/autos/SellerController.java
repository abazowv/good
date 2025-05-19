package org.example.autos;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.awt.*;
import java.awt.Label;
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
import javafx.scene.Scene;
import javafx.scene.Node;

public class SellerController extends Component implements Initializable{

    @FXML private Button sellBtn;

    @FXML private TableView<Car> CarsTable;
    @FXML private TableColumn<Car, String> brandCol;
    @FXML private TableColumn<Car, String> modelCol;
    @FXML private TableColumn<Car, String> bodyTypeCol;
    @FXML private TableColumn<Car, String> transCol;
    @FXML private TableColumn<Car, String> statusCol;
    @FXML private TableColumn<Car, Double> priceCol;
    @FXML private TableColumn<Car, String> dateCol;

    Connection Con;
    private ObservableList<Car> carList = FXCollections.observableArrayList();
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

    public void DisplayCars() {
        carList.clear();

        String query = "SELECT id, brand, model, body_type, trans, status, price, date FROM cars";

        try (PreparedStatement pst = Con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("body_type"),
                        rs.getString("trans"),
                        rs.getString("status"),
                        rs.getDouble("price"),
                        rs.getString("date")
                );
                carList.add(car);
            }

            CarsTable.setItems(carList);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load cars from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect();
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        bodyTypeCol.setCellValueFactory(new PropertyValueFactory<>("bodyType"));
        transCol.setCellValueFactory(new PropertyValueFactory<>("trans"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        DisplayCars();
    }

    @FXML

    public void sellBtn(ActionEvent actionEvent) {
        Car selectedCar = CarsTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            JOptionPane.showMessageDialog(null, "Please select a car to sell.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = selectedCar.getPrice();
            double tax = price * 0.01;         // 1%
            double commision = price * 0.005; // 0.5%

            String insertQuery = "INSERT INTO sold_cars (id, brand, model, price, tax, commision, availability, body_type, trans, status, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = Con.prepareStatement(insertQuery);
            insertStmt.setInt(1, selectedCar.getId());
            insertStmt.setString(2, selectedCar.getBrand());
            insertStmt.setString(3, selectedCar.getModel());
            insertStmt.setDouble(4, price);
            insertStmt.setDouble(5, tax);
            insertStmt.setDouble(6, commision);
            insertStmt.setString(7, "Sold");
            insertStmt.setString(8, selectedCar.getBodyType());
            insertStmt.setString(9, selectedCar.getTrans());
            insertStmt.setString(10, selectedCar.getStatus());
            insertStmt.setString(11, selectedCar.getDate());

            insertStmt.executeUpdate();

            String deleteQuery = "DELETE FROM cars WHERE id = ?";
            PreparedStatement deleteStmt = Con.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, selectedCar.getId());
            deleteStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Car sold successfully.");
            DisplayCars();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while selling car.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @FXML
    public void goToSoldCars(javafx.scene.input.MouseEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("soldcars.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 834, 534);
            currentStage.setScene(newScene);
            currentStage.setTitle("Sold Cars");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load Sold Cars scene.", "Error", JOptionPane.ERROR_MESSAGE);
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

