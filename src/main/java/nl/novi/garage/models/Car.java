package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank(message = "Brand cannot be blank")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Model cannot be blank")
    @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
    @Column(nullable = false)
    private String model;

    @NotBlank(message = "License plate cannot be blank")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "License plate must contain only uppercase letters, digits, and hyphens")
    @Size(min = 4, max = 10, message = "License plate must be between 4 and 10 characters")
    @Column(name = "license_plate", unique = true, nullable = false)
    private String licensePlate;

    // Constructors
    public Car() {
    }

    public Car(Customer customer, String brand, String model, String licensePlate) {
        this.customer = customer;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }
}