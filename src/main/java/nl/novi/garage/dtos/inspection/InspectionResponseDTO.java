package nl.novi.garage.dtos.inspection;

import java.time.LocalDate;

public class InspectionResponseDTO {

    private Long id;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String carLicensePlate;
    private LocalDate date;
    private String report;
    private String status;
    private Boolean isPaid;

    // Constructors
    public InspectionResponseDTO() {
    }

    public InspectionResponseDTO(Long id, Long carId, String carBrand, String carModel, String carLicensePlate,
            LocalDate date, String report, String status, Boolean isPaid) {
        this.id = id;
        this.carId = carId;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carLicensePlate = carLicensePlate;
        this.date = date;
        this.report = report;
        this.status = status;
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public String toString() {
        return "InspectionResponseDTO{" +
                "id=" + id +
                ", carId=" + carId +
                ", carBrand='" + carBrand + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", date=" + date +
                ", report='" + report + '\'' +
                ", status='" + status + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }
}