package nl.novi.garage.dtos.car;

public class CarResponseDTO {

    private Long id;
    private Long customerId;
    private String customerName;
    private String brand;
    private String model;
    private String licensePlate;

    // Constructors
    public CarResponseDTO() {
    }

    public CarResponseDTO(Long id, Long customerId, String brand, String model, String licensePlate) {
        this.id = id;
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    public CarResponseDTO(Long id, Long customerId, String customerName, String brand, String model,
            String licensePlate) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
        return "CarResponseDTO{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }
}