package nl.novi.garage.dtos.repair;

import java.math.BigDecimal;

public class RepairActionItemDTO {

    private Long id;
    private Long actionId;
    private String actionName;
    private String actionDescription;
    private BigDecimal actionPrice;
    private Integer amount;

    // Constructors
    public RepairActionItemDTO() {
    }

    public RepairActionItemDTO(Long id, Long actionId, String actionName, String actionDescription,
            BigDecimal actionPrice, Integer amount) {
        this.id = id;
        this.actionId = actionId;
        this.actionName = actionName;
        this.actionDescription = actionDescription;
        this.actionPrice = actionPrice;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public BigDecimal getActionPrice() {
        return actionPrice;
    }

    public void setActionPrice(BigDecimal actionPrice) {
        this.actionPrice = actionPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "RepairActionItemDTO{" +
                "id=" + id +
                ", actionId=" + actionId +
                ", actionName='" + actionName + '\'' +
                ", actionDescription='" + actionDescription + '\'' +
                ", actionPrice=" + actionPrice +
                ", amount=" + amount +
                '}';
    }
}