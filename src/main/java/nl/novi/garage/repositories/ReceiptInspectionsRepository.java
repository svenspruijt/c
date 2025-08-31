package nl.novi.garage.repositories;

import nl.novi.garage.models.Inspection;
import nl.novi.garage.models.Receipt;
import nl.novi.garage.models.ReceiptInspections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptInspectionsRepository extends JpaRepository<ReceiptInspections, Long> {

    List<ReceiptInspections> findByReceipt(Receipt receipt);

    List<ReceiptInspections> findByReceiptId(Long receiptId);

    List<ReceiptInspections> findByInspection(Inspection inspection);

    List<ReceiptInspections> findByInspectionId(Long inspectionId);

    Optional<ReceiptInspections> findByReceiptIdAndInspectionId(Long receiptId, Long inspectionId);

    boolean existsByReceiptIdAndInspectionId(Long receiptId, Long inspectionId);

    void deleteByReceiptId(Long receiptId);

    void deleteByInspectionId(Long inspectionId);
}