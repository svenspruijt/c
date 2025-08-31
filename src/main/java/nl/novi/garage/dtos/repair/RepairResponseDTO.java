package nl.novi.garage.dtos.repair;

import java.time.LocalDate;
import java.util.List;

public class RepairResponseDTO {

    private Long id;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String carLicensePlate;
    private LocalDate date;
    private String status;
    private String report;
    private Boolean isPaid;
    private List<RepairActionItemDTO> actions;
    private List<RepairPartItemDTO> parts;
    private List<RepairCustomActionItemDTO> customActions;

    // Constructors
    public RepairResponseDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public List<RepairActionItemDTO> getActions() {
        return actions;
    }

    public void setActions(List<RepairActionItemDTO> actions) {
        this.actions = actions;
    }

    public List<RepairPartItemDTO> getParts() {
        return parts;
    }

    public void setParts(List<RepairPartItemDTO> parts) {
        this.parts = parts;
    }

    public List<RepairCustomActionItemDTO> getCustomActions() {
        return customActions;
    }

    public void setCustomActions(List<RepairCustomActionItemDTO> customActions) {
        this.customActions = customActions;
    }

    @Override
    public String toString() {
        return "RepairResponseDTO{" +
                "id=" + id +
                ", carId=" + carId +
                ", carBrand='" + carBrand + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", report='" + report + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }
}