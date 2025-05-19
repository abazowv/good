package org.example.autos;

public class ServicedCar {
    private int id;
    private String brand;
    private String model;
    private String bodyType;
    private String trans;
    private String serviceDate;
    private String reason;
    private String status;
    private double price; // Добавляем поле price

    public ServicedCar(int id, String brand, String model, String bodyType, String trans, String serviceDate, String reason, String status, double price) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.bodyType = bodyType;
        this.trans = trans;
        this.serviceDate = serviceDate;
        this.reason = reason;
        this.status = status;
        this.price = price;
    }


    public int getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getBodyType() { return bodyType; }
    public String getTrans() { return trans; }
    public String getServiceDate() { return serviceDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public double getPrice() { return price; } // Добавляем геттер для price
}