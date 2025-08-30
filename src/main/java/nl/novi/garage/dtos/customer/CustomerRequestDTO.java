package nl.novi.garage.dtos.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone number must contain only digits, spaces, +, -, or parentheses")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    private String phonenumber;

    // Constructors
    public CustomerRequestDTO() {
    }

    public CustomerRequestDTO(String name, String phonenumber) {
        this.name = name;
        this.phonenumber = phonenumber;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "CustomerRequestDTO{" +
                "name='" + name + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                '}';
    }
}