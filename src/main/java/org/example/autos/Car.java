package org.example.autos;

import javafx.beans.property.*;

public class Car {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty model;
    private final SimpleStringProperty bodyType;
    private final SimpleStringProperty trans;
    private final SimpleStringProperty status;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty date;

    public Car(int id, String brand, String model, String bodyType, String trans, String status, double price, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.bodyType = new SimpleStringProperty(bodyType);
        this.trans = new SimpleStringProperty(trans);
        this.status = new SimpleStringProperty(status);
        this.price = new SimpleDoubleProperty(price);
        this.date = new SimpleStringProperty(date);
    }


    public int getId() { return id.get(); }
    public String getBrand() { return brand.get(); }
    public String getModel() { return model.get(); }
    public String getBodyType() { return bodyType.get(); }
    public String getTrans() { return trans.get(); }
    public String getStatus() { return status.get(); }
    public double getPrice() { return price.get(); }
    public String getDate() { return date.get(); }


    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty brandProperty() { return brand; }
    public SimpleStringProperty modelProperty() { return model; }
    public SimpleStringProperty bodyTypeProperty() { return bodyType; }
    public SimpleStringProperty transProperty() { return trans; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleStringProperty dateProperty() { return date; }
}
