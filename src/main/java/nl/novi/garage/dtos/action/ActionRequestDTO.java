package nl.novi.garage.dtos.action;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ActionRequestDTO {

    @NotBlank(message = "Action name cannot be blank")
    @Size(min = 2, max = 100, message = "Action name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    // Constructors
    public ActionRequestDTO() {
    }

    public ActionRequestDTO(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "ActionRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}