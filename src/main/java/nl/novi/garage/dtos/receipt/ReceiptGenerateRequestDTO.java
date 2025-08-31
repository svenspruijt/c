package nl.novi.garage.dtos.receipt;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ReceiptGenerateRequestDTO {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    private List<Long> inspectionIds;

    private List<Long> repairIds;

    // Constructors
    public ReceiptGenerateRequestDTO() {
    }

    public ReceiptGenerateRequestDTO(Long customerId, List<Long> inspectionIds, List<Long> repairIds) {
        this.customerId = customerId;
        this.inspectionIds = inspectionIds;
        this.repairIds = repairIds;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getInspectionIds() {
        return inspectionIds;
    }

    public void setInspectionIds(List<Long> inspectionIds) {
        this.inspectionIds = inspectionIds;
    }

    public List<Long> getRepairIds() {
        return repairIds;
    }

    public void setRepairIds(List<Long> repairIds) {
        this.repairIds = repairIds;
    }

    @Override
    public String toString() {
        return "ReceiptGenerateRequestDTO{" +
                "customerId=" + customerId +
                ", inspectionIds=" + inspectionIds +
                ", repairIds=" + repairIds +
                '}';
    }
}