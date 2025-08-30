package nl.novi.garage.services;

import nl.novi.garage.dtos.customer.CustomerRequestDTO;
import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.dtos.car.CarResponseDTO;
import nl.novi.garage.models.Customer;
import nl.novi.garage.models.Car;
import nl.novi.garage.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        // Check if phone number already exists
        if (customerRepository.existsByPhonenumber(customerRequestDTO.getPhonenumber())) {
            throw new IllegalArgumentException(
                    "Customer with phone number " + customerRequestDTO.getPhonenumber() + " already exists");
        }

        // Create new customer entity
        Customer customer = new Customer();
        customer.setName(customerRequestDTO.getName());
        customer.setPhonenumber(customerRequestDTO.getPhonenumber());

        // Save customer
        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponseDTO(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
        return mapToResponseDTOWithCars(customer);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        // Check if phone number already exists for another customer
        if (!existingCustomer.getPhonenumber().equals(customerRequestDTO.getPhonenumber()) &&
                customerRepository.existsByPhonenumber(customerRequestDTO.getPhonenumber())) {
            throw new IllegalArgumentException(
                    "Customer with phone number " + customerRequestDTO.getPhonenumber() + " already exists");
        }

        // Update customer fields
        existingCustomer.setName(customerRequestDTO.getName());
        existingCustomer.setPhonenumber(customerRequestDTO.getPhonenumber());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return mapToResponseDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        // Check if customer has cars
        if (customer.getCars() != null && !customer.getCars().isEmpty()) {
            throw new IllegalStateException("Cannot delete customer with associated cars. Please remove cars first.");
        }

        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> searchCustomers(String query) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCaseOrPhonenumberContaining(query,
                query);
        return customers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByPhoneNumber(String phoneNumber) {
        Customer customer = customerRepository.findByPhonenumber(phoneNumber)
                .orElseThrow(
                        () -> new IllegalArgumentException("Customer not found with phone number: " + phoneNumber));
        return mapToResponseDTOWithCars(customer);
    }

    // Helper methods for DTO mapping
    private CustomerResponseDTO mapToResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhonenumber());
    }

    private CustomerResponseDTO mapToResponseDTOWithCars(Customer customer) {
        List<CarResponseDTO> carDTOs = null;
        if (customer.getCars() != null) {
            carDTOs = customer.getCars().stream()
                    .map(this::mapCarToResponseDTO)
                    .collect(Collectors.toList());
        }

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhonenumber(),
                carDTOs);
    }

    private CarResponseDTO mapCarToResponseDTO(Car car) {
        return new CarResponseDTO(
                car.getId(),
                car.getCustomer().getId(),
                car.getCustomer().getName(),
                car.getBrand(),
                car.getModel(),
                car.getLicensePlate());
    }
}