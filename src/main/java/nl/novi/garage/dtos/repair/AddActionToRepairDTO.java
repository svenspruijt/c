package nl.novi.garage.dtos.repair;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddActionToRepairDTO {

    @NotNull(message = "Action ID cannot be null")
    private Long actionId;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;

    // Constructors
    public AddActionToRepairDTO() {
    }

    public AddActionToRepairDTO(Long actionId, Integer amount) {
        this.actionId = actionId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AddActionToRepairDTO{" +
                "actionId=" + actionId +
                ", amount=" + amount +
                '}';
    }
}