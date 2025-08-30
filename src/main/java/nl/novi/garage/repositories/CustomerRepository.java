package nl.novi.garage.repositories;

import nl.novi.garage.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByName(String name);

    List<Customer> findByNameContainingIgnoreCase(String name);

    Optional<Customer> findByPhonenumber(String phonenumber);

    boolean existsByPhonenumber(String phonenumber);

    List<Customer> findByNameContainingIgnoreCaseOrPhonenumberContaining(String name, String phonenumber);
}