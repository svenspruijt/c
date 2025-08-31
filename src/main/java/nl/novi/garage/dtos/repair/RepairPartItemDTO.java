package nl.novi.garage.dtos.repair;

import java.math.BigDecimal;

public class RepairPartItemDTO {

    private Long id;
    private Long partId;
    private String partName;
    private BigDecimal partPrice;
    private Integer amount;

    // Constructors
    public RepairPartItemDTO() {
    }

    public RepairPartItemDTO(Long id, Long partId, String partName, BigDecimal partPrice, Integer amount) {
        this.id = id;
        this.partId = partId;
        this.partName = partName;
        this.partPrice = partPrice;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public BigDecimal getPartPrice() {
        return partPrice;
    }

    public void setPartPrice(BigDecimal partPrice) {
        this.partPrice = partPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "RepairPartItemDTO{" +
                "id=" + id +
                ", partId=" + partId +
                ", partName='" + partName + '\'' +
                ", partPrice=" + partPrice +
                ", amount=" + amount +
                '}';
    }
}