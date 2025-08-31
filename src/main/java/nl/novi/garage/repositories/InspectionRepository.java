package nl.novi.garage.repositories;

import nl.novi.garage.models.Inspection;
import nl.novi.garage.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    List<Inspection> findByCar(Car car);

    List<Inspection> findByCarId(Long carId);

    List<Inspection> findByStatus(String status);

    List<Inspection> findByIsPaid(Boolean isPaid);

    List<Inspection> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Inspection> findByCarIdAndStatus(Long carId, String status);

    List<Inspection> findByCarIdOrderByDateDesc(Long carId);
}