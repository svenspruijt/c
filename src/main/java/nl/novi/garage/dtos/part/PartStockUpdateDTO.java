package nl.novi.garage.dtos.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PartStockUpdateDTO {

    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // Constructors
    public PartStockUpdateDTO() {
    }

    public PartStockUpdateDTO(Integer stock) {
        this.stock = stock;
    }

    // Getters and Setters
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "PartStockUpdateDTO{" +
                "stock=" + stock +
                '}';
    }
}