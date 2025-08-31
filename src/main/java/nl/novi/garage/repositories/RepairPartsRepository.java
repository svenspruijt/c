package nl.novi.garage.repositories;

import nl.novi.garage.models.RepairParts;
import nl.novi.garage.models.Repair;
import nl.novi.garage.models.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairPartsRepository extends JpaRepository<RepairParts, Long> {

    List<RepairParts> findByRepair(Repair repair);

    List<RepairParts> findByRepairId(Long repairId);

    List<RepairParts> findByPart(Part part);

    List<RepairParts> findByPartId(Long partId);

    Optional<RepairParts> findByRepairIdAndPartId(Long repairId, Long partId);

    void deleteByRepairId(Long repairId);
}