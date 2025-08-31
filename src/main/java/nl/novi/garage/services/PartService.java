package nl.novi.garage.services;

import nl.novi.garage.dtos.part.PartRequestDTO;
import nl.novi.garage.dtos.part.PartResponseDTO;
import nl.novi.garage.dtos.part.PartStockUpdateDTO;
import nl.novi.garage.models.Part;
import nl.novi.garage.repositories.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public PartResponseDTO createPart(PartRequestDTO partRequestDTO) {
        // Check if part with same name already exists
        if (partRepository.existsByNameIgnoreCase(partRequestDTO.getName())) {
            throw new IllegalArgumentException(
                    "Part with name '" + partRequestDTO.getName() + "' already exists");
        }

        // Create new part entity
        Part part = new Part();
        part.setName(partRequestDTO.getName());
        part.setPrice(partRequestDTO.getPrice());
        part.setStock(partRequestDTO.getStock());

        // Save part
        Part savedPart = partRepository.save(part);

        return mapToResponseDTO(savedPart);
    }

    @Transactional(readOnly = true)
    public List<PartResponseDTO> getAllParts() {
        List<Part> parts = partRepository.findAll();
        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartResponseDTO getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + id));
        return mapToResponseDTO(part);
    }

    @Transactional(readOnly = true)
    public PartResponseDTO getPartByName(String name) {
        Part part = partRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with name: " + name));
        return mapToResponseDTO(part);
    }

    public PartResponseDTO updatePart(Long id, PartRequestDTO partRequestDTO) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + id));

        // Check if name already exists for another part
        if (!existingPart.getName().equalsIgnoreCase(partRequestDTO.getName()) &&
                partRepository.existsByNameIgnoreCase(partRequestDTO.getName())) {
            throw new IllegalArgumentException("Part with name '" + partRequestDTO.getName() + "' already exists");
        }

        // Update part fields
        existingPart.setName(partRequestDTO.getName());
        existingPart.setPrice(partRequestDTO.getPrice());
        existingPart.setStock(partRequestDTO.getStock());

        Part updatedPart = partRepository.save(existingPart);
        return mapToResponseDTO(updatedPart);
    }

    public PartResponseDTO updatePartStock(Long id, PartStockUpdateDTO stockUpdateDTO) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + id));

        // Update only the stock
        existingPart.setStock(stockUpdateDTO.getStock());

        Part updatedPart = partRepository.save(existingPart);
        return mapToResponseDTO(updatedPart);
    }

    public void deletePart(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with id: " + id));

        // TODO: In future, check if part is used in any repairs
        // For now, we can safely delete
        partRepository.delete(part);
    }

    @Transactional(readOnly = true)
    public List<PartResponseDTO> searchPartsByName(String name) {
        List<Part> parts = partRepository.findByNameContainingIgnoreCase(name);
        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartResponseDTO> getPartsWithLowStock(Integer threshold) {
        List<Part> parts = partRepository.findByStockLessThanEqual(threshold);
        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartResponseDTO> getPartsInStock() {
        List<Part> parts = partRepository.findByStockGreaterThan(0);
        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartResponseDTO> getOutOfStockParts() {
        List<Part> parts = partRepository.findByStockEquals(0);
        return parts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method for DTO mapping
    private PartResponseDTO mapToResponseDTO(Part part) {
        return new PartResponseDTO(
                part.getId(),
                part.getName(),
                part.getPrice(),
                part.getStock());
    }
}