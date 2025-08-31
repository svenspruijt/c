package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "receipt_inspections")
public class ReceiptInspections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Receipt cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @NotNull(message = "Inspection cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private Inspection inspection;

    // Constructors
    public ReceiptInspections() {
    }

    public ReceiptInspections(Receipt receipt, Inspection inspection) {
        this.receipt = receipt;
        this.inspection = inspection;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    @Override
    public String toString() {
        return "ReceiptInspections{" +
                "id=" + id +
                ", receiptId=" + (receipt != null ? receipt.getId() : null) +
                ", inspectionId=" + (inspection != null ? inspection.getId() : null) +
                '}';
    }
}