package nl.novi.garage.services;

import nl.novi.garage.dtos.car.CarRequestDTO;
import nl.novi.garage.dtos.car.CarResponseDTO;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.Customer;
import nl.novi.garage.repositories.CarRepository;
import nl.novi.garage.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CarService(CarRepository carRepository, CustomerRepository customerRepository) {
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    public CarResponseDTO createCar(CarRequestDTO carRequestDTO) {
        // Check if license plate already exists
        if (carRepository.existsByLicensePlate(carRequestDTO.getLicensePlate())) {
            throw new IllegalArgumentException(
                    "Car with license plate " + carRequestDTO.getLicensePlate() + " already exists");
        }

        // Get customer
        Customer customer = customerRepository.findById(carRequestDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with id: " + carRequestDTO.getCustomerId()));

        // Create new car entity
        Car car = new Car();
        car.setCustomer(customer);
        car.setBrand(carRequestDTO.getBrand());
        car.setModel(carRequestDTO.getModel());
        car.setLicensePlate(carRequestDTO.getLicensePlate().toUpperCase());

        // Save car
        Car savedCar = carRepository.save(car);

        return mapToResponseDTO(savedCar);
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarResponseDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));
        return mapToResponseDTO(car);
    }

    @Transactional(readOnly = true)
    public CarResponseDTO getCarByLicensePlate(String licensePlate) {
        Car car = carRepository.findByLicensePlate(licensePlate.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Car not found with license plate: " + licensePlate));
        return mapToResponseDTO(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getCarsByCustomerId(Long customerId) {
        // Verify customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        List<Car> cars = carRepository.findByCustomerId(customerId);
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CarResponseDTO updateCar(Long id, CarRequestDTO carRequestDTO) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));

        // Check if license plate already exists for another car
        String newLicensePlate = carRequestDTO.getLicensePlate().toUpperCase();
        if (!existingCar.getLicensePlate().equals(newLicensePlate) &&
                carRepository.existsByLicensePlate(newLicensePlate)) {
            throw new IllegalArgumentException("Car with license plate " + newLicensePlate + " already exists");
        }

        // Get new customer if changed
        if (!existingCar.getCustomer().getId().equals(carRequestDTO.getCustomerId())) {
            Customer newCustomer = customerRepository.findById(carRequestDTO.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Customer not found with id: " + carRequestDTO.getCustomerId()));
            existingCar.setCustomer(newCustomer);
        }

        // Update car fields
        existingCar.setBrand(carRequestDTO.getBrand());
        existingCar.setModel(carRequestDTO.getModel());
        existingCar.setLicensePlate(newLicensePlate);

        Car updatedCar = carRepository.save(existingCar);
        return mapToResponseDTO(updatedCar);
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));

        // TODO: In future, check if car has associated inspections/repairs
        // For now, we can safely delete
        carRepository.delete(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> searchCarsByBrand(String brand) {
        List<Car> cars = carRepository.findByBrandIgnoreCase(brand);
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> searchCarsByModel(String model) {
        List<Car> cars = carRepository.findByModelIgnoreCase(model);
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> searchCarsByBrandAndModel(String brand, String model) {
        List<Car> cars = carRepository.findByBrandIgnoreCaseAndModelIgnoreCase(brand, model);
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> searchCarsByLicensePlate(String licensePlate) {
        List<Car> cars = carRepository.findByLicensePlateContainingIgnoreCase(licensePlate);
        return cars.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method for DTO mapping
    private CarResponseDTO mapToResponseDTO(Car car) {
        return new CarResponseDTO(
                car.getId(),
                car.getCustomer().getId(),
                car.getCustomer().getName(),
                car.getBrand(),
                car.getModel(),
                car.getLicensePlate());
    }
}