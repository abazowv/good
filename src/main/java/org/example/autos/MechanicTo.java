package org.example.autos;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Node;

public class MechanicTo implements Initializable {

    @FXML private Button serviceBtn;
    @FXML private Button salaryBtn;
    @FXML private Label toserviceTb;
    @FXML private Label inserviceTb;
    @FXML private TableView<Car> CarsTable;
    @FXML private TableColumn<Car, String> brandCol;
    @FXML private TableColumn<Car, String> modelCol;
    @FXML private TableColumn<Car, String> bodyTypeCol;
    @FXML private TableColumn<Car, String> transCol;
    @FXML private TableColumn<Car, String> statusCol;
    @FXML private TableColumn<Car, Double> priceCol;
    @FXML private TableColumn<Car, String> dateCol;
    @FXML private TextField reasonTb;
    @FXML private TextField serviceTb;

    Connection Con;
    private ObservableList<Car> carList = FXCollections.observableArrayList();
    private double totalSalary = 0.0; // Переменная для хранения зарплаты
    private static final int MECHANIC_ID = 1; // ID механика (для примера)

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
            Logger.getLogger(MechanicTo.class.getName()).log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(MechanicTo.class.getName()).log(Level.SEVERE, "Database connection failed!", ex);
        }
    }

    private void loadSalary() {
        String query = "SELECT total_salary FROM mechanic_salary WHERE mechanic_id = ?";
        try (PreparedStatement pst = Con.prepareStatement(query)) {
            pst.setInt(1, MECHANIC_ID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    totalSalary = rs.getDouble("total_salary");
                    System.out.println("Loaded salary: " + totalSalary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load salary from database: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateSalary() {
        String query = "UPDATE mechanic_salary SET total_salary = ? WHERE mechanic_id = ?";
        try (PreparedStatement pst = Con.prepareStatement(query)) {
            pst.setDouble(1, totalSalary);
            pst.setInt(2, MECHANIC_ID);
            pst.executeUpdate();
            System.out.println("Updated salary in database: " + totalSalary);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update salary in database: " + e.getMessage());
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load cars from database: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect();
        brandCol.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        modelCol.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
        bodyTypeCol.setCellValueFactory(cellData -> cellData.getValue().bodyTypeProperty());
        transCol.setCellValueFactory(cellData -> cellData.getValue().transProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        DisplayCars();
        loadSalary();
    }

    @FXML
    public void goToinService(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("inservice.fxml"));
            Parent root = fxmlLoader.load();
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 834, 534);
            currentStage.setScene(newScene);
            currentStage.setTitle("In Service");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load In Service scene: " + e.getMessage());
            alert.showAndWait();
        }
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load Login scene: " + e.getMessage());
            alert.showAndWait();
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
            currentStage.setTitle("To Service");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load To Service scene: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void Reset() {
        reasonTb.setText("");
        serviceTb.setText("");
    }

    @FXML
    public void serviceBtn(ActionEvent actionEvent) {
        Car selectedCar = CarsTable.getSelectionModel().getSelectedItem();

        if (selectedCar != null) {
            try {

                Con.setAutoCommit(false);


                String deleteQuery = "DELETE FROM cars WHERE id = ?";
                try (PreparedStatement deleteStmt = Con.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, selectedCar.getId());
                    int rowsDeleted = deleteStmt.executeUpdate();
                    if (rowsDeleted == 0) {
                        throw new SQLException("No car found with id " + selectedCar.getId());
                    }
                }


                String insertQuery = "INSERT INTO serviced_cars (brand, model, body_type, trans, service_date, reason, status, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
                int newId = 0;
                try (PreparedStatement insertStmt = Con.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, selectedCar.getBrand());
                    insertStmt.setString(2, selectedCar.getModel());
                    insertStmt.setString(3, selectedCar.getBodyType());
                    insertStmt.setString(4, selectedCar.getTrans());
                    insertStmt.setString(5, serviceTb.getText());
                    insertStmt.setString(6, reasonTb.getText());
                    insertStmt.setString(7, "In Service");
                    insertStmt.setDouble(8, selectedCar.getPrice());

                    try (ResultSet rs = insertStmt.executeQuery()) {
                        if (rs.next()) {
                            newId = rs.getInt("id");
                        }
                    }
                }


                totalSalary += 100.0;
                updateSalary();


                Con.commit();


                carList.remove(selectedCar);
                CarsTable.refresh();
                Reset();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Car serviced successfully! New ID in serviced_cars: " + newId);
                alert.showAndWait();

            } catch (SQLException e) {
                try {
                    Con.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error during service operation: " + e.getMessage());
                alert.showAndWait();
            } finally {
                try {
                    Con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a car for service.");
            alert.showAndWait();
        }
    }

    @FXML
    public void salaryBtn(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mechanic Salary");
        alert.setHeaderText("Your Total Salary");
        alert.setContentText("You have earned: $" + totalSalary + "\n(Each car serviced adds $100 to your salary)");
        alert.showAndWait();
    }
}