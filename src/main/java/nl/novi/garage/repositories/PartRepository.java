package nl.novi.garage.repositories;

import nl.novi.garage.models.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Part> findByNameContainingIgnoreCase(String name);

    List<Part> findByStockGreaterThan(Integer stock);

    List<Part> findByStockLessThanEqual(Integer stock);

    List<Part> findByStockEquals(Integer stock);
}