package nl.novi.garage.dtos.repair;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddPartToRepairDTO {

    @NotNull(message = "Part ID cannot be null")
    private Long partId;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    // Constructors
    public AddPartToRepairDTO() {
    }

    public AddPartToRepairDTO(Long partId, Integer amount) {
        this.partId = partId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AddPartToRepairDTO{" +
                "partId=" + partId +
                ", amount=" + amount +
                '}';
    }
}