package nl.novi.garage.repositories;

import nl.novi.garage.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    Optional<Action> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Action> findByNameContainingIgnoreCase(String name);

    List<Action> findByDescriptionContainingIgnoreCase(String description);
}