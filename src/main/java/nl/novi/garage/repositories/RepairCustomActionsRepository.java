package nl.novi.garage.repositories;

import nl.novi.garage.models.RepairCustomActions;
import nl.novi.garage.models.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairCustomActionsRepository extends JpaRepository<RepairCustomActions, Long> {

    List<RepairCustomActions> findByRepair(Repair repair);

    List<RepairCustomActions> findByRepairId(Long repairId);

    void deleteByRepairId(Long repairId);
}