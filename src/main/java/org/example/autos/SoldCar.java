package org.example.autos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SoldCar {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty model;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty tax;
    private final SimpleDoubleProperty commision;
    private final SimpleStringProperty availability;

    public SoldCar(int id, String brand, String model, double price, double tax, double commission, String availability) {
        this.id = new SimpleIntegerProperty(id);
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.price = new SimpleDoubleProperty(price);
        this.tax = new SimpleDoubleProperty(tax);
        this.commision = new SimpleDoubleProperty(commission);
        this.availability = new SimpleStringProperty(availability);
    }
    public int getId() {
        return id.get();
    }

    public String getBrand() {
        return brand.get();
    }

    public String getModel() {
        return model.get();
    }

    public double getPrice() {
        return price.get();
    }

    public double getTax() {
        return tax.get();
    }

    public double getCommision() {
        return commision.get();
    }

    public String getAvailability() {
        return availability.get();
    }

}
