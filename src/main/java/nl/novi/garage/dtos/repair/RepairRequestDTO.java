package nl.novi.garage.dtos.repair;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RepairRequestDTO {

    @NotNull(message = "Car ID cannot be null")
    private Long carId;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotBlank(message = "Status cannot be blank")
    @Size(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status;

    @NotBlank(message = "Report cannot be blank")
    @Size(min = 10, max = 2000, message = "Report must be between 10 and 2000 characters")
    private String report;

    @NotNull(message = "Payment status cannot be null")
    private Boolean isPaid;

    // Constructors
    public RepairRequestDTO() {
    }

    public RepairRequestDTO(Long carId, LocalDate date, String status, String report, Boolean isPaid) {
        this.carId = carId;
        this.date = date;
        this.status = status;
        this.report = report;
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    @Override
    public String toString() {
        return "RepairRequestDTO{" +
                "carId=" + carId +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", report='" + report + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }
}