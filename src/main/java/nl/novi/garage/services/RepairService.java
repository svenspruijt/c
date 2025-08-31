package nl.novi.garage.services;

import nl.novi.garage.dtos.repair.*;
import nl.novi.garage.models.*;
import nl.novi.garage.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepairService {

    private final RepairRepository repairRepository;
    private final CarRepository carRepository;
    private final RepairActionsRepository repairActionsRepository;
    private final RepairPartsRepository repairPartsRepository;
    private final RepairCustomActionsRepository repairCustomActionsRepository;
    private final ActionRepository actionRepository;
    private final PartRepository partRepository;

    @Autowired
    public RepairService(RepairRepository repairRepository,
            CarRepository carRepository,
            RepairActionsRepository repairActionsRepository,
            RepairPartsRepository repairPartsRepository,
            RepairCustomActionsRepository repairCustomActionsRepository,
            ActionRepository actionRepository,
            PartRepository partRepository) {
        this.repairRepository = repairRepository;
        this.carRepository = carRepository;
        this.repairActionsRepository = repairActionsRepository;
        this.repairPartsRepository = repairPartsRepository;
        this.repairCustomActionsRepository = repairCustomActionsRepository;
        this.actionRepository = actionRepository;
        this.partRepository = partRepository;
    }

    public RepairResponseDTO createRepair(RepairRequestDTO repairRequestDTO) {
        // Get car
        Car car = carRepository.findById(repairRequestDTO.getCarId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Car not found with id: " + repairRequestDTO.getCarId()));

        // Create new repair entity
        Repair repair = new Repair();
        repair.setCar(car);
        repair.setDate(repairRequestDTO.getDate());
        repair.setStatus(repairRequestDTO.getStatus());
        repair.setReport(repairRequestDTO.getReport());
        repair.setIsPaid(repairRequestDTO.getIsPaid());

        // Save repair
        Repair savedRepair = repairRepository.save(repair);

        return mapToResponseDTO(savedRepair);
    }

    @Transactional(readOnly = true)
    public List<RepairResponseDTO> getAllRepairs() {
        List<Repair> repairs = repairRepository.findAll();
        return repairs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RepairResponseDTO getRepairById(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + id));
        return mapToResponseDTO(repair);
    }

    @Transactional(readOnly = true)
    public List<RepairResponseDTO> getRepairsByCarId(Long carId) {
        // Verify car exists
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));

        List<Repair> repairs = repairRepository.findByCarIdOrderByDateDesc(carId);
        return repairs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairResponseDTO> getRepairsByStatus(String status) {
        List<Repair> repairs = repairRepository.findByStatus(status);
        return repairs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairResponseDTO> getRepairsByPaymentStatus(Boolean isPaid) {
        List<Repair> repairs = repairRepository.findByIsPaid(isPaid);
        return repairs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairResponseDTO> getRepairsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Repair> repairs = repairRepository.findByDateBetween(startDate, endDate);
        return repairs.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public RepairResponseDTO updateRepair(Long id, RepairRequestDTO repairRequestDTO) {
        Repair existingRepair = repairRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + id));

        // Get car if changed
        if (!existingRepair.getCar().getId().equals(repairRequestDTO.getCarId())) {
            Car newCar = carRepository.findById(repairRequestDTO.getCarId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Car not found with id: " + repairRequestDTO.getCarId()));
            existingRepair.setCar(newCar);
        }

        // Update repair fields
        existingRepair.setDate(repairRequestDTO.getDate());
        existingRepair.setStatus(repairRequestDTO.getStatus());
        existingRepair.setReport(repairRequestDTO.getReport());
        existingRepair.setIsPaid(repairRequestDTO.getIsPaid());

        Repair updatedRepair = repairRepository.save(existingRepair);
        return mapToResponseDTO(updatedRepair);
    }

    public RepairActionItemDTO addActionToRepair(Long repairId, AddActionToRepairDTO addActionDTO) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + repairId));

        Action action = actionRepository.findById(addActionDTO.getActionId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Action not found with id: " + addActionDTO.getActionId()));

        // Check if action already exists for this repair
        repairActionsRepository.findByRepairIdAndActionId(repairId, addActionDTO.getActionId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Action already added to this repair");
                });

        // Create repair action
        RepairActions repairAction = new RepairActions(repair, action, addActionDTO.getAmount());
        RepairActions savedRepairAction = repairActionsRepository.save(repairAction);

        return new RepairActionItemDTO(
                savedRepairAction.getId(),
                action.getId(),
                action.getName(),
                action.getDescription(),
                action.getPrice(),
                savedRepairAction.getAmount());
    }

    public RepairPartItemDTO addPartToRepair(Long repairId, AddPartToRepairDTO addPartDTO) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + repairId));

        Part part = partRepository.findById(addPartDTO.getPartId())
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + addPartDTO.getPartId()));

        // Check stock availability
        if (part.getStock() < addPartDTO.getAmount()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + part.getStock() + ", Required: " + addPartDTO.getAmount());
        }

        // Check if part already exists for this repair
        repairPartsRepository.findByRepairIdAndPartId(repairId, addPartDTO.getPartId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Part already added to this repair");
                });

        // Reduce stock
        part.setStock(part.getStock() - addPartDTO.getAmount());
        partRepository.save(part);

        // Create repair part
        RepairParts repairPart = new RepairParts(repair, part, addPartDTO.getAmount());
        RepairParts savedRepairPart = repairPartsRepository.save(repairPart);

        return new RepairPartItemDTO(
                savedRepairPart.getId(),
                part.getId(),
                part.getName(),
                part.getPrice(),
                savedRepairPart.getAmount());
    }

    public RepairCustomActionItemDTO addCustomActionToRepair(Long repairId,
            AddCustomActionToRepairDTO addCustomActionDTO) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + repairId));

        // Create custom action
        RepairCustomActions customAction = new RepairCustomActions(repair, addCustomActionDTO.getDescription(),
                addCustomActionDTO.getPrice());
        RepairCustomActions savedCustomAction = repairCustomActionsRepository.save(customAction);

        return new RepairCustomActionItemDTO(
                savedCustomAction.getId(),
                savedCustomAction.getDescription(),
                savedCustomAction.getPrice());
    }

    public void removeActionFromRepair(Long repairId, Long actionId) {
        RepairActions repairAction = repairActionsRepository.findByRepairIdAndActionId(repairId, actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action not found in this repair"));

        repairActionsRepository.delete(repairAction);
    }

    public void removePartFromRepair(Long repairId, Long partId) {
        RepairParts repairPart = repairPartsRepository.findByRepairIdAndPartId(repairId, partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found in this repair"));

        // Restore stock
        Part part = repairPart.getPart();
        part.setStock(part.getStock() + repairPart.getAmount());
        partRepository.save(part);

        repairPartsRepository.delete(repairPart);
    }

    public void removeCustomActionFromRepair(Long customActionId) {
        RepairCustomActions customAction = repairCustomActionsRepository.findById(customActionId)
                .orElseThrow(() -> new IllegalArgumentException("Custom action not found with id: " + customActionId));

        repairCustomActionsRepository.delete(customAction);
    }

    public RepairResponseDTO markAsPaid(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + id));

        // Check if already paid
        if (repair.getIsPaid()) {
            throw new IllegalStateException("Repair is already marked as paid");
        }

        repair.setIsPaid(true);
        Repair updatedRepair = repairRepository.save(repair);
        return mapToResponseDTO(updatedRepair);
    }

    public void deleteRepair(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair not found with id: " + id));

        // Business rule: Can only delete if not paid
        if (repair.getIsPaid()) {
            throw new IllegalStateException("Cannot delete paid repair");
        }

        // Restore stock for all parts used in this repair
        List<RepairParts> repairParts = repairPartsRepository.findByRepairId(id);
        for (RepairParts repairPart : repairParts) {
            Part part = repairPart.getPart();
            part.setStock(part.getStock() + repairPart.getAmount());
            partRepository.save(part);
        }

        // Delete repair (cascade will handle related entities)
        repairRepository.delete(repair);
    }

    // Helper method for DTO mapping
    private RepairResponseDTO mapToResponseDTO(Repair repair) {
        RepairResponseDTO dto = new RepairResponseDTO();
        dto.setId(repair.getId());
        dto.setCarId(repair.getCar().getId());
        dto.setCarBrand(repair.getCar().getBrand());
        dto.setCarModel(repair.getCar().getModel());
        dto.setCarLicensePlate(repair.getCar().getLicensePlate());
        dto.setDate(repair.getDate());
        dto.setStatus(repair.getStatus());
        dto.setReport(repair.getReport());
        dto.setIsPaid(repair.getIsPaid());

        // Map actions
        List<RepairActions> repairActions = repairActionsRepository.findByRepairId(repair.getId());
        dto.setActions(repairActions.stream()
                .map(ra -> new RepairActionItemDTO(
                        ra.getId(),
                        ra.getAction().getId(),
                        ra.getAction().getName(),
                        ra.getAction().getDescription(),
                        ra.getAction().getPrice(),
                        ra.getAmount()))
                .collect(Collectors.toList()));

        // Map parts
        List<RepairParts> repairParts = repairPartsRepository.findByRepairId(repair.getId());
        dto.setParts(repairParts.stream()
                .map(rp -> new RepairPartItemDTO(
                        rp.getId(),
                        rp.getPart().getId(),
                        rp.getPart().getName(),
                        rp.getPart().getPrice(),
                        rp.getAmount()))
                .collect(Collectors.toList()));

        // Map custom actions
        List<RepairCustomActions> customActions = repairCustomActionsRepository.findByRepairId(repair.getId());
        dto.setCustomActions(customActions.stream()
                .map(ca -> new RepairCustomActionItemDTO(
                        ca.getId(),
                        ca.getDescription(),
                        ca.getPrice()))
                .collect(Collectors.toList()));

        return dto;
    }
}