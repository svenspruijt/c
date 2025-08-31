package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.inspection.InspectionRequestDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.services.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/inspections")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class InspectionController {

    private final InspectionService inspectionService;

    @Autowired
    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<InspectionResponseDTO> createInspection(
            @Valid @RequestBody InspectionRequestDTO inspectionRequestDTO) {
        InspectionResponseDTO response = inspectionService.createInspection(inspectionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InspectionResponseDTO>> getAllInspections() {
        List<InspectionResponseDTO> inspections = inspectionService.getAllInspections();
        return ResponseEntity.ok(inspections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionResponseDTO> getInspectionById(@PathVariable Long id) {
        InspectionResponseDTO inspection = inspectionService.getInspectionById(id);
        return ResponseEntity.ok(inspection);
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<InspectionResponseDTO>> getInspectionsByCarId(@PathVariable Long carId) {
        List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByCarId(carId);
        return ResponseEntity.ok(inspections);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<InspectionResponseDTO> updateInspection(
            @PathVariable Long id,
            @Valid @RequestBody InspectionRequestDTO inspectionRequestDTO) {
        InspectionResponseDTO response = inspectionService.updateInspection(id, inspectionRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<InspectionResponseDTO> completeInspection(
            @PathVariable Long id,
            @RequestBody(required = false) String finalReport) {

        // Extract final report from request body if provided
        String report = null;
        if (finalReport != null && !finalReport.trim().isEmpty()) {
            report = finalReport.trim().replaceAll("^\"|\"$", ""); // Remove quotes if present
        }

        InspectionResponseDTO response = inspectionService.completeInspection(id, report);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<InspectionResponseDTO> markAsPaid(@PathVariable Long id) {
        InspectionResponseDTO response = inspectionService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MONTEUR') or hasRole('BEHEER')")
    public ResponseEntity<Void> deleteInspection(@PathVariable Long id) {
        inspectionService.deleteInspection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<InspectionResponseDTO>> searchInspections(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (status != null && !status.trim().isEmpty()) {
            List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByStatus(status);
            return ResponseEntity.ok(inspections);
        }

        if (isPaid != null) {
            List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByPaymentStatus(isPaid);
            return ResponseEntity.ok(inspections);
        }

        if (startDate != null && endDate != null) {
            List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(inspections);
        }

        // If no search parameters provided, return all inspections
        List<InspectionResponseDTO> inspections = inspectionService.getAllInspections();
        return ResponseEntity.ok(inspections);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<InspectionResponseDTO>> getUnpaidInspections() {
        List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByPaymentStatus(false);
        return ResponseEntity.ok(inspections);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InspectionResponseDTO>> getInspectionsByStatus(@PathVariable String status) {
        List<InspectionResponseDTO> inspections = inspectionService.getInspectionsByStatus(status);
        return ResponseEntity.ok(inspections);
    }
}