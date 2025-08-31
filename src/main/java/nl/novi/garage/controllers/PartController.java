package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.part.PartRequestDTO;
import nl.novi.garage.dtos.part.PartResponseDTO;
import nl.novi.garage.dtos.part.PartStockUpdateDTO;
import nl.novi.garage.services.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parts")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class PartController {

    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<PartResponseDTO> createPart(@Valid @RequestBody PartRequestDTO partRequestDTO) {
        PartResponseDTO response = partService.createPart(partRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartResponseDTO>> getAllParts() {
        List<PartResponseDTO> parts = partService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponseDTO> getPartById(@PathVariable Long id) {
        PartResponseDTO part = partService.getPartById(id);
        return ResponseEntity.ok(part);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<PartResponseDTO> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartRequestDTO partRequestDTO) {
        PartResponseDTO response = partService.updatePart(id, partRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<PartResponseDTO> updatePartStock(
            @PathVariable Long id,
            @Valid @RequestBody PartStockUpdateDTO stockUpdateDTO) {
        PartResponseDTO response = partService.updatePartStock(id, stockUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PartResponseDTO>> searchParts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer lowStock) {

        if (name != null && !name.trim().isEmpty()) {
            List<PartResponseDTO> parts = partService.searchPartsByName(name);
            return ResponseEntity.ok(parts);
        }

        if (lowStock != null) {
            List<PartResponseDTO> parts = partService.getPartsWithLowStock(lowStock);
            return ResponseEntity.ok(parts);
        }

        // If no search parameters provided, return all parts
        List<PartResponseDTO> parts = partService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<PartResponseDTO>> getPartsInStock() {
        List<PartResponseDTO> parts = partService.getPartsInStock();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<PartResponseDTO>> getOutOfStockParts() {
        List<PartResponseDTO> parts = partService.getOutOfStockParts();
        return ResponseEntity.ok(parts);
    }
}