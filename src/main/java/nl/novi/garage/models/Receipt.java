package nl.novi.garage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Total excluding VAT cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total excluding VAT must be non-negative")
    @Column(name = "total_excl_vat", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalExclVat;

    @NotNull(message = "VAT cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "VAT must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vat;

    @NotNull(message = "Total including VAT cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total including VAT must be non-negative")
    @Column(name = "total_incl_vat", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalInclVat;

    @NotNull(message = "Payment status cannot be null")
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid;

    @NotNull(message = "Created date cannot be null")
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReceiptInspections> receiptInspections;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReceiptRepairs> receiptRepairs;

    // Constructors
    public Receipt() {
    }

    public Receipt(Customer customer, BigDecimal totalExclVat, BigDecimal vat, BigDecimal totalInclVat, Boolean isPaid,
            LocalDate createdDate) {
        this.customer = customer;
        this.totalExclVat = totalExclVat;
        this.vat = vat;
        this.totalInclVat = totalInclVat;
        this.isPaid = isPaid;
        this.createdDate = createdDate;
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

    public BigDecimal getTotalExclVat() {
        return totalExclVat;
    }

    public void setTotalExclVat(BigDecimal totalExclVat) {
        this.totalExclVat = totalExclVat;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getTotalInclVat() {
        return totalInclVat;
    }

    public void setTotalInclVat(BigDecimal totalInclVat) {
        this.totalInclVat = totalInclVat;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<ReceiptInspections> getReceiptInspections() {
        return receiptInspections;
    }

    public void setReceiptInspections(List<ReceiptInspections> receiptInspections) {
        this.receiptInspections = receiptInspections;
    }

    public List<ReceiptRepairs> getReceiptRepairs() {
        return receiptRepairs;
    }

    public void setReceiptRepairs(List<ReceiptRepairs> receiptRepairs) {
        this.receiptRepairs = receiptRepairs;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", totalExclVat=" + totalExclVat +
                ", vat=" + vat +
                ", totalInclVat=" + totalInclVat +
                ", isPaid=" + isPaid +
                ", createdDate=" + createdDate +
                '}';
    }
}