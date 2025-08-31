package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "repairs")
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Car cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @NotNull(message = "Date cannot be null")
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank(message = "Status cannot be blank")
    @Size(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    @Column(nullable = false)
    private String status;

    @NotBlank(message = "Report cannot be blank")
    @Size(min = 10, max = 2000, message = "Report must be between 10 and 2000 characters")
    @Column(nullable = false, length = 2000)
    private String report;

    @NotNull(message = "Payment status cannot be null")
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;

    // Constructors
    public Repair() {
    }

    public Repair(Car car, LocalDate date, String status, String report, Boolean isPaid) {
        this.car = car;
        this.date = date;
        this.status = status;
        this.report = report;
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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
        return "Repair{" +
                "id=" + id +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", report='" + report + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }
}