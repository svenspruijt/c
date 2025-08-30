package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.car.CarRequestDTO;
import nl.novi.garage.dtos.car.CarResponseDTO;
import nl.novi.garage.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO response = carService.createCar(carRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<CarResponseDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable Long id) {
        CarResponseDTO car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/license-plate/{licensePlate}")
    public ResponseEntity<CarResponseDTO> getCarByLicensePlate(@PathVariable String licensePlate) {
        CarResponseDTO car = carService.getCarByLicensePlate(licensePlate);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByCustomerId(@PathVariable Long customerId) {
        List<CarResponseDTO> cars = carService.getCarsByCustomerId(customerId);
        return ResponseEntity.ok(cars);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDTO> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO response = carService.updateCar(id, carRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/brand/{brand}")
    public ResponseEntity<List<CarResponseDTO>> searchCarsByBrand(@PathVariable String brand) {
        List<CarResponseDTO> cars = carService.searchCarsByBrand(brand);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/search/model/{model}")
    public ResponseEntity<List<CarResponseDTO>> searchCarsByModel(@PathVariable String model) {
        List<CarResponseDTO> cars = carService.searchCarsByModel(model);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarResponseDTO>> searchCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String licensePlate) {

        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            List<CarResponseDTO> cars = carService.searchCarsByLicensePlate(licensePlate);
            return ResponseEntity.ok(cars);
        }

        if (brand != null && model != null) {
            List<CarResponseDTO> cars = carService.searchCarsByBrandAndModel(brand, model);
            return ResponseEntity.ok(cars);
        }

        if (brand != null) {
            List<CarResponseDTO> cars = carService.searchCarsByBrand(brand);
            return ResponseEntity.ok(cars);
        }

        if (model != null) {
            List<CarResponseDTO> cars = carService.searchCarsByModel(model);
            return ResponseEntity.ok(cars);
        }

        // If no search parameters provided, return all cars
        List<CarResponseDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }
}