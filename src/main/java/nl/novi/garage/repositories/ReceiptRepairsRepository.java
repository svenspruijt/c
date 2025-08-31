package nl.novi.garage.repositories;

import nl.novi.garage.models.Receipt;
import nl.novi.garage.models.ReceiptRepairs;
import nl.novi.garage.models.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepairsRepository extends JpaRepository<ReceiptRepairs, Long> {

    List<ReceiptRepairs> findByReceipt(Receipt receipt);

    List<ReceiptRepairs> findByReceiptId(Long receiptId);

    List<ReceiptRepairs> findByRepair(Repair repair);

    List<ReceiptRepairs> findByRepairId(Long repairId);

    Optional<ReceiptRepairs> findByReceiptIdAndRepairId(Long receiptId, Long repairId);

    boolean existsByReceiptIdAndRepairId(Long receiptId, Long repairId);

    void deleteByReceiptId(Long receiptId);

    void deleteByRepairId(Long repairId);
}