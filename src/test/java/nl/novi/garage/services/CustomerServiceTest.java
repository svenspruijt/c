package nl.novi.garage.services;

import nl.novi.garage.dtos.customer.CustomerRequestDTO;
import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.models.Customer;
import nl.novi.garage.models.Car;
import nl.novi.garage.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerRequestDTO testCustomerRequestDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jan Jansen");
        testCustomer.setPhonenumber("+31612345678");
        testCustomer.setCars(new ArrayList<>());

        testCustomerRequestDTO = new CustomerRequestDTO();
        testCustomerRequestDTO.setName("Jan Jansen");
        testCustomerRequestDTO.setPhonenumber("+31612345678");
    }

    @Test
    void createCustomer_ShouldReturnCustomerResponseDTO_WhenValidRequest() {
        // Arrange
        when(customerRepository.existsByPhonenumber(testCustomerRequestDTO.getPhonenumber())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponseDTO result = customerService.createCustomer(testCustomerRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getPhonenumber(), result.getPhonenumber());
        verify(customerRepository, times(1)).existsByPhonenumber(testCustomerRequestDTO.getPhonenumber());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // Arrange
        when(customerRepository.existsByPhonenumber(testCustomerRequestDTO.getPhonenumber())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(testCustomerRequestDTO));

        assertEquals("Customer with phone number " + testCustomerRequestDTO.getPhonenumber() + " already exists",
                exception.getMessage());
        verify(customerRepository, times(1)).existsByPhonenumber(testCustomerRequestDTO.getPhonenumber());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Maria van der Berg");
        customer2.setPhonenumber("0687654321");

        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCustomer.getName(), result.get(0).getName());
        assertEquals(customer2.getName(), result.get(1).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_ShouldReturnCustomerWithCars_WhenCustomerExists() {
        // Arrange
        Car testCar = new Car();
        testCar.setId(1L);
        testCar.setCustomer(testCustomer);
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setLicensePlate("AB-123-CD");
        testCustomer.setCars(Arrays.asList(testCar));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerResponseDTO result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        assertNotNull(result.getCars());
        assertEquals(1, result.getCars().size());
        assertEquals(testCar.getBrand(), result.getCars().get(0).getBrand());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.getCustomerById(999L));

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository, times(1)).findById(999L);
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenValidRequest() {
        // Arrange
        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("Jan Jansen Updated");
        updateDTO.setPhonenumber("+31612345679");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Jan Jansen Updated");
        updatedCustomer.setPhonenumber("+31612345679");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByPhonenumber(updateDTO.getPhonenumber())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerResponseDTO result = customerService.updateCustomer(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCustomer.getId(), result.getId());
        assertEquals(updatedCustomer.getName(), result.getName());
        assertEquals(updatedCustomer.getPhonenumber(), result.getPhonenumber());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldThrowException_WhenNewPhoneNumberAlreadyExists() {
        // Arrange
        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("Jan Jansen Updated");
        updateDTO.setPhonenumber("+31612345679");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByPhonenumber(updateDTO.getPhonenumber())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateCustomer(1L, updateDTO));

        assertEquals("Customer with phone number " + updateDTO.getPhonenumber() + " already exists",
                exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldUpdateSuccessfully_WhenSamePhoneNumber() {
        // Arrange
        CustomerRequestDTO updateDTO = new CustomerRequestDTO();
        updateDTO.setName("Jan Jansen Updated");
        updateDTO.setPhonenumber(testCustomer.getPhonenumber()); // Same phone number

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponseDTO result = customerService.updateCustomer(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).existsByPhonenumber(anyString());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteSuccessfully_WhenCustomerHasNoCars() {
        // Arrange
        testCustomer.setCars(new ArrayList<>());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(testCustomer);
    }

    @Test
    void deleteCustomer_ShouldThrowException_WhenCustomerHasCars() {
        // Arrange
        Car testCar = new Car();
        testCustomer.setCars(Arrays.asList(testCar));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> customerService.deleteCustomer(1L));

        assertEquals("Cannot delete customer with associated cars. Please remove cars first.",
                exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    void searchCustomers_ShouldReturnMatchingCustomers() {
        // Arrange
        String query = "Jan";
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByNameContainingIgnoreCaseOrPhonenumberContaining(query, query))
                .thenReturn(customers);

        // Act
        List<CustomerResponseDTO> result = customerService.searchCustomers(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCustomer.getName(), result.get(0).getName());
        verify(customerRepository, times(1))
                .findByNameContainingIgnoreCaseOrPhonenumberContaining(query, query);
    }

    @Test
    void getCustomerByPhoneNumber_ShouldReturnCustomer_WhenPhoneNumberExists() {
        // Arrange
        String phoneNumber = "+31612345678";
        when(customerRepository.findByPhonenumber(phoneNumber)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerResponseDTO result = customerService.getCustomerByPhoneNumber(phoneNumber);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getName(), result.getName());
        assertEquals(testCustomer.getPhonenumber(), result.getPhonenumber());
        verify(customerRepository, times(1)).findByPhonenumber(phoneNumber);
    }

    @Test
    void getCustomerByPhoneNumber_ShouldThrowException_WhenPhoneNumberNotFound() {
        // Arrange
        String phoneNumber = "+31612345999";
        when(customerRepository.findByPhonenumber(phoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.getCustomerByPhoneNumber(phoneNumber));

        assertEquals("Customer not found with phone number: " + phoneNumber, exception.getMessage());
        verify(customerRepository, times(1)).findByPhonenumber(phoneNumber);
    }
}