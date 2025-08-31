package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "receipt_repairs")
public class ReceiptRepairs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Receipt cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @NotNull(message = "Repair cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id", nullable = false)
    private Repair repair;

    // Constructors
    public ReceiptRepairs() {
    }

    public ReceiptRepairs(Receipt receipt, Repair repair) {
        this.receipt = receipt;
        this.repair = repair;
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

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }

    @Override
    public String toString() {
        return "ReceiptRepairs{" +
                "id=" + id +
                ", receiptId=" + (receipt != null ? receipt.getId() : null) +
                ", repairId=" + (repair != null ? repair.getId() : null) +
                '}';
    }
}