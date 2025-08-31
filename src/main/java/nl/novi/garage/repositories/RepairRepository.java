package nl.novi.garage.repositories;

import nl.novi.garage.models.Repair;
import nl.novi.garage.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {

    List<Repair> findByCar(Car car);

    List<Repair> findByCarId(Long carId);

    List<Repair> findByStatus(String status);

    List<Repair> findByIsPaid(Boolean isPaid);

    List<Repair> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Repair> findByCarIdAndStatus(Long carId, String status);

    List<Repair> findByCarIdOrderByDateDesc(Long carId);
}