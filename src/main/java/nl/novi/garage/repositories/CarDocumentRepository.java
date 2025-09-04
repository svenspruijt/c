package nl.novi.garage.repositories;

import nl.novi.garage.models.Car;
import nl.novi.garage.models.CarDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarDocumentRepository extends JpaRepository<CarDocument, Long> {

    List<CarDocument> findByCar(Car car);

    List<CarDocument> findByCarId(Long carId);

    Optional<CarDocument> findByCarIdAndFilename(Long carId, String filename);

    boolean existsByCarIdAndFilename(Long carId, String filename);

    void deleteByCarId(Long carId);
}