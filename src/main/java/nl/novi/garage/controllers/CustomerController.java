package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.customer.CustomerRequestDTO;
import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO response = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO response = customerService.updateCustomer(id, customerRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String phone) {

        if (phone != null && !phone.trim().isEmpty()) {
            // Handle URL encoding: + symbols in phone numbers get converted to spaces
            String decodedPhone = phone;
            if (decodedPhone.startsWith(" ")) {
                // Leading space indicates original + symbol
                decodedPhone = "+" + decodedPhone.substring(1);
            } else {
                // Replace any other spaces with +
                decodedPhone = decodedPhone.replace(" ", "+");
            }

            CustomerResponseDTO customer = customerService.getCustomerByPhoneNumber(decodedPhone);
            return ResponseEntity.ok(List.of(customer));
        }

        if (q != null && !q.trim().isEmpty()) {
            List<CustomerResponseDTO> customers = customerService.searchCustomers(q);
            return ResponseEntity.ok(customers);
        }

        // If no search parameters provided, return all customers
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/{id}/cars")
    public ResponseEntity<String> addCarToCustomer(@PathVariable Long id) {
        return ResponseEntity.ok("Use POST /cars endpoint to add a car to customer " + id);
    }

}