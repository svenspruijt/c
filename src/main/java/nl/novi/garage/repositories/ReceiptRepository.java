package nl.novi.garage.repositories;

import nl.novi.garage.models.Customer;
import nl.novi.garage.models.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByCustomer(Customer customer);

    List<Receipt> findByCustomerId(Long customerId);

    List<Receipt> findByIsPaid(Boolean isPaid);

    List<Receipt> findByCreatedDate(LocalDate createdDate);

    List<Receipt> findByCreatedDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Receipt r WHERE r.customer.id = :customerId AND r.isPaid = :isPaid")
    List<Receipt> findByCustomerIdAndIsPaid(@Param("customerId") Long customerId, @Param("isPaid") Boolean isPaid);

    boolean existsByCustomerIdAndIsPaid(Long customerId, Boolean isPaid);
}