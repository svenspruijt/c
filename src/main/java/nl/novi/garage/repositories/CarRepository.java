package nl.novi.garage.repositories;

import nl.novi.garage.models.Car;
import nl.novi.garage.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByLicensePlate(String licensePlate);

    boolean existsByLicensePlate(String licensePlate);

    List<Car> findByCustomer(Customer customer);

    List<Car> findByCustomerId(Long customerId);

    List<Car> findByBrandIgnoreCase(String brand);

    List<Car> findByModelIgnoreCase(String model);

    List<Car> findByBrandIgnoreCaseAndModelIgnoreCase(String brand, String model);

    List<Car> findByLicensePlateContainingIgnoreCase(String licensePlate);
}