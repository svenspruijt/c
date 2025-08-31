package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.repair.*;
import nl.novi.garage.services.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/repairs")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class RepairController {

    private final RepairService repairService;

    @Autowired
    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairResponseDTO> createRepair(@Valid @RequestBody RepairRequestDTO repairRequestDTO) {
        RepairResponseDTO response = repairService.createRepair(repairRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RepairResponseDTO>> getAllRepairs() {
        List<RepairResponseDTO> repairs = repairService.getAllRepairs();
        return ResponseEntity.ok(repairs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairResponseDTO> getRepairById(@PathVariable Long id) {
        RepairResponseDTO repair = repairService.getRepairById(id);
        return ResponseEntity.ok(repair);
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<RepairResponseDTO>> getRepairsByCarId(@PathVariable Long carId) {
        List<RepairResponseDTO> repairs = repairService.getRepairsByCarId(carId);
        return ResponseEntity.ok(repairs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairResponseDTO> updateRepair(
            @PathVariable Long id,
            @Valid @RequestBody RepairRequestDTO repairRequestDTO) {
        RepairResponseDTO response = repairService.updateRepair(id, repairRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairResponseDTO> markAsPaid(@PathVariable Long id) {
        RepairResponseDTO response = repairService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<Void> deleteRepair(@PathVariable Long id) {
        repairService.deleteRepair(id);
        return ResponseEntity.noContent().build();
    }

    // Action management endpoints
    @PostMapping("/{id}/actions")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairActionItemDTO> addActionToRepair(
            @PathVariable Long id,
            @Valid @RequestBody AddActionToRepairDTO addActionDTO) {
        RepairActionItemDTO response = repairService.addActionToRepair(id, addActionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{repairId}/actions/{actionId}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<Void> removeActionFromRepair(
            @PathVariable Long repairId,
            @PathVariable Long actionId) {
        repairService.removeActionFromRepair(repairId, actionId);
        return ResponseEntity.noContent().build();
    }

    // Part management endpoints
    @PostMapping("/{id}/parts")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairPartItemDTO> addPartToRepair(
            @PathVariable Long id,
            @Valid @RequestBody AddPartToRepairDTO addPartDTO) {
        RepairPartItemDTO response = repairService.addPartToRepair(id, addPartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{repairId}/parts/{partId}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<Void> removePartFromRepair(
            @PathVariable Long repairId,
            @PathVariable Long partId) {
        repairService.removePartFromRepair(repairId, partId);
        return ResponseEntity.noContent().build();
    }

    // Custom action management endpoints
    @PostMapping("/{id}/custom-actions")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<RepairCustomActionItemDTO> addCustomActionToRepair(
            @PathVariable Long id,
            @Valid @RequestBody AddCustomActionToRepairDTO addCustomActionDTO) {
        RepairCustomActionItemDTO response = repairService.addCustomActionToRepair(id, addCustomActionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/custom-actions/{customActionId}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<Void> removeCustomActionFromRepair(@PathVariable Long customActionId) {
        repairService.removeCustomActionFromRepair(customActionId);
        return ResponseEntity.noContent().build();
    }

    // Search and filter endpoints
    @GetMapping("/search")
    public ResponseEntity<List<RepairResponseDTO>> searchRepairs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (status != null && !status.trim().isEmpty()) {
            List<RepairResponseDTO> repairs = repairService.getRepairsByStatus(status);
            return ResponseEntity.ok(repairs);
        }

        if (isPaid != null) {
            List<RepairResponseDTO> repairs = repairService.getRepairsByPaymentStatus(isPaid);
            return ResponseEntity.ok(repairs);
        }

        if (startDate != null && endDate != null) {
            List<RepairResponseDTO> repairs = repairService.getRepairsByDateRange(startDate, endDate);
            return ResponseEntity.ok(repairs);
        }

        // If no search parameters provided, return all repairs
        List<RepairResponseDTO> repairs = repairService.getAllRepairs();
        return ResponseEntity.ok(repairs);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<RepairResponseDTO>> getUnpaidRepairs() {
        List<RepairResponseDTO> repairs = repairService.getRepairsByPaymentStatus(false);
        return ResponseEntity.ok(repairs);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RepairResponseDTO>> getRepairsByStatus(@PathVariable String status) {
        List<RepairResponseDTO> repairs = repairService.getRepairsByStatus(status);
        return ResponseEntity.ok(repairs);
    }
}