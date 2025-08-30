package nl.novi.garage.dtos.customer;

import nl.novi.garage.dtos.car.CarResponseDTO;

import java.util.List;

public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String phonenumber;
    private List<CarResponseDTO> cars;

    // Constructors
    public CustomerResponseDTO() {
    }

    public CustomerResponseDTO(Long id, String name, String phonenumber) {
        this.id = id;
        this.name = name;
        this.phonenumber = phonenumber;
    }

    public CustomerResponseDTO(Long id, String name, String phonenumber, List<CarResponseDTO> cars) {
        this.id = id;
        this.name = name;
        this.phonenumber = phonenumber;
        this.cars = cars;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<CarResponseDTO> getCars() {
        return cars;
    }

    public void setCars(List<CarResponseDTO> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "CustomerResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", carCount=" + (cars != null ? cars.size() : 0) +
                '}';
    }
}