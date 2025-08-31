package nl.novi.garage.repositories;

import nl.novi.garage.models.RepairActions;
import nl.novi.garage.models.Repair;
import nl.novi.garage.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairActionsRepository extends JpaRepository<RepairActions, Long> {

    List<RepairActions> findByRepair(Repair repair);

    List<RepairActions> findByRepairId(Long repairId);

    List<RepairActions> findByAction(Action action);

    List<RepairActions> findByActionId(Long actionId);

    Optional<RepairActions> findByRepairIdAndActionId(Long repairId, Long actionId);

    void deleteByRepairId(Long repairId);
}