package nl.novi.garage.dtos.repair;

import java.math.BigDecimal;

public class RepairCustomActionItemDTO {

    private Long id;
    private String description;
    private BigDecimal price;

    // Constructors
    public RepairCustomActionItemDTO() {
    }

    public RepairCustomActionItemDTO(Long id, String description, BigDecimal price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "RepairCustomActionItemDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}