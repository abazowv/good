package org.example.autos;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Label;
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

public class MechanicIn extends Component implements Initializable {

    @FXML
    private TableView<ServicedCar> ServiceTable;
    @FXML
    private TableColumn<ServicedCar, String> brandCol;
    @FXML
    private TableColumn<ServicedCar, String> modelCol;
    @FXML
    private TableColumn<ServicedCar, String> bodyTypeCol;
    @FXML
    private TableColumn<ServicedCar, String> transCol;
    @FXML
    private TableColumn<ServicedCar, String> serviceDateCol;
    @FXML
    private TableColumn<ServicedCar, String> reasonCol;
    @FXML
    private TableColumn<ServicedCar, String> statusCol;

    @FXML
    private Label toserviceTb;
    @FXML
    private Label inserviceTb;
    @FXML
    private Label logoutBtn;

    private ObservableList<ServicedCar> serviceCarList = FXCollections.observableArrayList();
    private Connection Con;

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
            Logger.getLogger(MechanicIn.class.getName()).log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(MechanicIn.class.getName()).log(Level.SEVERE, "Database connection failed!", ex);
        }
    }

    public void DisplayServiceCars() {
        serviceCarList.clear();

        String query = "SELECT id, brand, model, body_type, trans, service_date, reason, status, price FROM serviced_cars";

        try (PreparedStatement pst = Con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                ServicedCar serviceCar = new ServicedCar(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("body_type"),
                        rs.getString("trans"),
                        rs.getString("service_date"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getDouble("price") // Добавляем извлечение price
                );
                serviceCarList.add(serviceCar);
            }

            ServiceTable.setItems(serviceCarList);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load serviced cars from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect();

        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        bodyTypeCol.setCellValueFactory(new PropertyValueFactory<>("bodyType"));
        transCol.setCellValueFactory(new PropertyValueFactory<>("trans"));
        serviceDateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        DisplayServiceCars();
    }

    @FXML
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

    @FXML
    public void goToService(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("toservice.fxml"));
            Parent root = fxmlLoader.load();
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 834, 534);
            currentStage.setScene(newScene);
            currentStage.setTitle("In Service");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load To Service scene.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void returnBtn(ActionEvent actionEvent) {
        ServicedCar selectedCar = ServiceTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            JOptionPane.showMessageDialog(null, "Please select a car to return.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            Con.setAutoCommit(false);

            String selectQuery = "SELECT id, brand, model, body_type, trans, service_date, reason, status, price FROM serviced_cars WHERE id = ?";
            int id = 0;
            String brand = null;
            String model = null;
            String bodyType = null;
            String trans = null;
            String serviceDate = null;
            double price = 0.0;

            try (PreparedStatement selectStmt = Con.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, selectedCar.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                        brand = rs.getString("brand");
                        model = rs.getString("model");
                        bodyType = rs.getString("body_type");
                        trans = rs.getString("trans");
                        serviceDate = rs.getString("service_date");
                        price = rs.getDouble("price");
                        System.out.println("Extracted price: " + price);
                    }
                }
            }


            String insertQuery = "INSERT INTO cars (id, brand, model, body_type, trans, status, price, date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = Con.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, id);
                insertStmt.setString(2, brand);
                insertStmt.setString(3, model);
                insertStmt.setString(4, bodyType);
                insertStmt.setString(5, trans);
                insertStmt.setString(6, "Available");
                insertStmt.setDouble(7, price);
                insertStmt.setString(8, serviceDate);

                insertStmt.executeUpdate();
            }

            String deleteQuery = "DELETE FROM serviced_cars WHERE id = ?";
            try (PreparedStatement deleteStmt = Con.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
            }

            Con.commit();

            DisplayServiceCars();

            int option = JOptionPane.showConfirmDialog(null, "Do you want to return to the 'To Service' menu?", "Confirm Transition", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("toservice.fxml"));
                Parent root = fxmlLoader.load();
                MechanicTo mechanicToController = fxmlLoader.getController();
                mechanicToController.DisplayCars();
                Scene newScene = new Scene(root, 834, 534);
                currentStage.setScene(newScene);
                currentStage.setTitle("To Service");
                currentStage.show();
            }

            JOptionPane.showMessageDialog(null, "Car returned successfully.");

        } catch (SQLException | IOException e) {
            try {
                Con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while returning car: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                Con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}