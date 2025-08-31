package nl.novi.garage.dtos.receipt;

import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.dtos.repair.RepairResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReceiptResponseDTO {

    private Long id;
    private CustomerResponseDTO customer;
    private BigDecimal totalExclVat;
    private BigDecimal vat;
    private BigDecimal totalInclVat;
    private Boolean isPaid;
    private LocalDate createdDate;
    private List<InspectionResponseDTO> inspections;
    private List<RepairResponseDTO> repairs;

    // Constructors
    public ReceiptResponseDTO() {
    }

    public ReceiptResponseDTO(Long id, CustomerResponseDTO customer, BigDecimal totalExclVat, BigDecimal vat,
            BigDecimal totalInclVat, Boolean isPaid, LocalDate createdDate) {
        this.id = id;
        this.customer = customer;
        this.totalExclVat = totalExclVat;
        this.vat = vat;
        this.totalInclVat = totalInclVat;
        this.isPaid = isPaid;
        this.createdDate = createdDate;
    }

    public ReceiptResponseDTO(Long id, CustomerResponseDTO customer, BigDecimal totalExclVat, BigDecimal vat,
            BigDecimal totalInclVat, Boolean isPaid, LocalDate createdDate, List<InspectionResponseDTO> inspections,
            List<RepairResponseDTO> repairs) {
        this.id = id;
        this.customer = customer;
        this.totalExclVat = totalExclVat;
        this.vat = vat;
        this.totalInclVat = totalInclVat;
        this.isPaid = isPaid;
        this.createdDate = createdDate;
        this.inspections = inspections;
        this.repairs = repairs;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerResponseDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponseDTO customer) {
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

    public List<InspectionResponseDTO> getInspections() {
        return inspections;
    }

    public void setInspections(List<InspectionResponseDTO> inspections) {
        this.inspections = inspections;
    }

    public List<RepairResponseDTO> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<RepairResponseDTO> repairs) {
        this.repairs = repairs;
    }

    @Override
    public String toString() {
        return "ReceiptResponseDTO{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : null) +
                ", totalExclVat=" + totalExclVat +
                ", vat=" + vat +
                ", totalInclVat=" + totalInclVat +
                ", isPaid=" + isPaid +
                ", createdDate=" + createdDate +
                ", inspectionCount=" + (inspections != null ? inspections.size() : 0) +
                ", repairCount=" + (repairs != null ? repairs.size() : 0) +
                '}';
    }
}