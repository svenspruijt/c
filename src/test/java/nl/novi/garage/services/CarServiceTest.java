package nl.novi.garage.services;

import nl.novi.garage.dtos.car.CarRequestDTO;
import nl.novi.garage.dtos.car.CarResponseDTO;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.Customer;
import nl.novi.garage.repositories.CarRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private Customer testCustomer;
    private CarRequestDTO testCarRequestDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jan Jansen");
        testCustomer.setPhonenumber("+31612345678");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCustomer(testCustomer);
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setLicensePlate("AB-123-CD");

        testCarRequestDTO = new CarRequestDTO();
        testCarRequestDTO.setCustomerId(1L);
        testCarRequestDTO.setBrand("Toyota");
        testCarRequestDTO.setModel("Corolla");
        testCarRequestDTO.setLicensePlate("AB-123-CD");
    }

    @Test
    void createCar_ShouldReturnCarResponseDTO_WhenValidRequest() {
        // Arrange
        when(carRepository.existsByLicensePlate("AB-123-CD")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        CarResponseDTO result = carService.createCar(testCarRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testCar.getId(), result.getId());
        assertEquals(testCar.getBrand(), result.getBrand());
        assertEquals(testCar.getModel(), result.getModel());
        assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
        assertEquals(testCustomer.getId(), result.getCustomerId());
        assertEquals(testCustomer.getName(), result.getCustomerName());
        verify(carRepository, times(1)).existsByLicensePlate("AB-123-CD");
        verify(customerRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void createCar_ShouldThrowException_WhenLicensePlateAlreadyExists() {
        // Arrange
        when(carRepository.existsByLicensePlate("AB-123-CD")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.createCar(testCarRequestDTO));

        assertEquals("Car with license plate AB-123-CD already exists", exception.getMessage());
        verify(carRepository, times(1)).existsByLicensePlate("AB-123-CD");
        verify(customerRepository, never()).findById(anyLong());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void createCar_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        when(carRepository.existsByLicensePlate("AB-123-CD")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.createCar(testCarRequestDTO));

        assertEquals("Customer not found with id: 1", exception.getMessage());
        verify(carRepository, times(1)).existsByLicensePlate("AB-123-CD");
        verify(customerRepository, times(1)).findById(1L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void createCar_ShouldConvertLicensePlateToUpperCase() {
        // Arrange
        testCarRequestDTO.setLicensePlate("ab-123-cd");
        when(carRepository.existsByLicensePlate("ab-123-cd")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        CarResponseDTO result = carService.createCar(testCarRequestDTO);

        // Assert
        assertNotNull(result);
        verify(carRepository, times(1)).existsByLicensePlate("ab-123-cd");
        verify(carRepository, times(1)).save(argThat(car -> "AB-123-CD".equals(car.getLicensePlate())));
    }

    @Test
    void getAllCars_ShouldReturnListOfCars() {
        // Arrange
        Car car2 = new Car();
        car2.setId(2L);
        car2.setCustomer(testCustomer);
        car2.setBrand("Honda");
        car2.setModel("Civic");
        car2.setLicensePlate("XY-456-ZW");

        List<Car> cars = Arrays.asList(testCar, car2);
        when(carRepository.findAll()).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.getAllCars();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCar.getBrand(), result.get(0).getBrand());
        assertEquals(car2.getBrand(), result.get(1).getBrand());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void getCarById_ShouldReturnCar_WhenCarExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        CarResponseDTO result = carService.getCarById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testCar.getId(), result.getId());
        assertEquals(testCar.getBrand(), result.getBrand());
        assertEquals(testCar.getModel(), result.getModel());
        assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void getCarById_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.getCarById(999L));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
    }

    @Test
    void getCarByLicensePlate_ShouldReturnCar_WhenLicensePlateExists() {
        // Arrange
        when(carRepository.findByLicensePlate("AB-123-CD")).thenReturn(Optional.of(testCar));

        // Act
        CarResponseDTO result = carService.getCarByLicensePlate("ab-123-cd");

        // Assert
        assertNotNull(result);
        assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
        verify(carRepository, times(1)).findByLicensePlate("AB-123-CD");
    }

    @Test
    void getCarsByCustomerId_ShouldReturnCarsForCustomer_WhenCustomerExists() {
        // Arrange
        List<Car> cars = Arrays.asList(testCar);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(carRepository.findByCustomerId(1L)).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.getCarsByCustomerId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getBrand(), result.get(0).getBrand());
        verify(customerRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void getCarsByCustomerId_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.getCarsByCustomerId(999L));

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerRepository, times(1)).findById(999L);
        verify(carRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void updateCar_ShouldReturnUpdatedCar_WhenValidRequest() {
        // Arrange
        CarRequestDTO updateDTO = new CarRequestDTO();
        updateDTO.setCustomerId(1L);
        updateDTO.setBrand("Honda");
        updateDTO.setModel("Civic");
        updateDTO.setLicensePlate("XY-456-ZW");

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setCustomer(testCustomer);
        updatedCar.setBrand("Honda");
        updatedCar.setModel("Civic");
        updatedCar.setLicensePlate("XY-456-ZW");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByLicensePlate("XY-456-ZW")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        // Act
        CarResponseDTO result = carService.updateCar(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCar.getBrand(), result.getBrand());
        assertEquals(updatedCar.getModel(), result.getModel());
        assertEquals(updatedCar.getLicensePlate(), result.getLicensePlate());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void updateCar_ShouldThrowException_WhenNewLicensePlateAlreadyExists() {
        // Arrange
        CarRequestDTO updateDTO = new CarRequestDTO();
        updateDTO.setCustomerId(1L);
        updateDTO.setBrand("Honda");
        updateDTO.setModel("Civic");
        updateDTO.setLicensePlate("XY-456-ZW");

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByLicensePlate("XY-456-ZW")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.updateCar(1L, updateDTO));

        assertEquals("Car with license plate XY-456-ZW already exists", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_ShouldUpdateCustomer_WhenCustomerIdChanges() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setId(2L);
        newCustomer.setName("Maria van der Berg");

        CarRequestDTO updateDTO = new CarRequestDTO();
        updateDTO.setCustomerId(2L);
        updateDTO.setBrand("Honda");
        updateDTO.setModel("Civic");
        updateDTO.setLicensePlate("AB-123-CD"); // Same license plate

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(newCustomer));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // Act
        CarResponseDTO result = carService.updateCar(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(carRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(2L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void deleteCar_ShouldDeleteSuccessfully_WhenCarExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        carService.deleteCar(1L);

        // Assert
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).delete(testCar);
    }

    @Test
    void deleteCar_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.deleteCar(999L));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    void searchCarsByBrand_ShouldReturnMatchingCars() {
        // Arrange
        String brand = "Toyota";
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByBrandIgnoreCase(brand)).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.searchCarsByBrand(brand);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getBrand(), result.get(0).getBrand());
        verify(carRepository, times(1)).findByBrandIgnoreCase(brand);
    }

    @Test
    void searchCarsByModel_ShouldReturnMatchingCars() {
        // Arrange
        String model = "Corolla";
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByModelIgnoreCase(model)).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.searchCarsByModel(model);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getModel(), result.get(0).getModel());
        verify(carRepository, times(1)).findByModelIgnoreCase(model);
    }

    @Test
    void searchCarsByBrandAndModel_ShouldReturnMatchingCars() {
        // Arrange
        String brand = "Toyota";
        String model = "Corolla";
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByBrandIgnoreCaseAndModelIgnoreCase(brand, model)).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.searchCarsByBrandAndModel(brand, model);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getBrand(), result.get(0).getBrand());
        assertEquals(testCar.getModel(), result.get(0).getModel());
        verify(carRepository, times(1)).findByBrandIgnoreCaseAndModelIgnoreCase(brand, model);
    }

    @Test
    void searchCarsByLicensePlate_ShouldReturnMatchingCars() {
        // Arrange
        String licensePlate = "AB-123";
        List<Car> cars = Arrays.asList(testCar);
        when(carRepository.findByLicensePlateContainingIgnoreCase(licensePlate)).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carService.searchCarsByLicensePlate(licensePlate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCar.getLicensePlate(), result.get(0).getLicensePlate());
        verify(carRepository, times(1)).findByLicensePlateContainingIgnoreCase(licensePlate);
    }
}