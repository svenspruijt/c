package nl.novi.garage.services;

import nl.novi.garage.dtos.inspection.InspectionRequestDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.models.Inspection;
import nl.novi.garage.models.Car;
import nl.novi.garage.repositories.InspectionRepository;
import nl.novi.garage.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final CarRepository carRepository;

    @Autowired
    public InspectionService(InspectionRepository inspectionRepository, CarRepository carRepository) {
        this.inspectionRepository = inspectionRepository;
        this.carRepository = carRepository;
    }

    public InspectionResponseDTO createInspection(InspectionRequestDTO inspectionRequestDTO) {
        // Get car
        Car car = carRepository.findById(inspectionRequestDTO.getCarId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Car not found with id: " + inspectionRequestDTO.getCarId()));

        // Create new inspection entity
        Inspection inspection = new Inspection();
        inspection.setCar(car);
        inspection.setDate(inspectionRequestDTO.getDate());
        inspection.setReport(inspectionRequestDTO.getReport());
        inspection.setStatus(inspectionRequestDTO.getStatus());
        inspection.setIsPaid(inspectionRequestDTO.getIsPaid());

        // Save inspection
        Inspection savedInspection = inspectionRepository.save(inspection);

        return mapToResponseDTO(savedInspection);
    }

    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getAllInspections() {
        List<Inspection> inspections = inspectionRepository.findAll();
        return inspections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InspectionResponseDTO getInspectionById(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + id));
        return mapToResponseDTO(inspection);
    }

    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getInspectionsByCarId(Long carId) {
        // Verify car exists
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));

        List<Inspection> inspections = inspectionRepository.findByCarIdOrderByDateDesc(carId);
        return inspections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getInspectionsByStatus(String status) {
        List<Inspection> inspections = inspectionRepository.findByStatus(status);
        return inspections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getInspectionsByPaymentStatus(Boolean isPaid) {
        List<Inspection> inspections = inspectionRepository.findByIsPaid(isPaid);
        return inspections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getInspectionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Inspection> inspections = inspectionRepository.findByDateBetween(startDate, endDate);
        return inspections.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public InspectionResponseDTO updateInspection(Long id, InspectionRequestDTO inspectionRequestDTO) {
        Inspection existingInspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + id));

        // Get car if changed
        if (!existingInspection.getCar().getId().equals(inspectionRequestDTO.getCarId())) {
            Car newCar = carRepository.findById(inspectionRequestDTO.getCarId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Car not found with id: " + inspectionRequestDTO.getCarId()));
            existingInspection.setCar(newCar);
        }

        // Update inspection fields
        existingInspection.setDate(inspectionRequestDTO.getDate());
        existingInspection.setReport(inspectionRequestDTO.getReport());
        existingInspection.setStatus(inspectionRequestDTO.getStatus());
        existingInspection.setIsPaid(inspectionRequestDTO.getIsPaid());

        Inspection updatedInspection = inspectionRepository.save(existingInspection);
        return mapToResponseDTO(updatedInspection);
    }

    public InspectionResponseDTO completeInspection(Long id, String finalReport) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + id));

        // Validate current status
        if ("COMPLETED".equals(inspection.getStatus())) {
            throw new IllegalStateException("Inspection is already completed");
        }

        // Update inspection to completed status
        inspection.setStatus("COMPLETED");
        if (finalReport != null && !finalReport.trim().isEmpty()) {
            inspection.setReport(finalReport);
        }

        Inspection updatedInspection = inspectionRepository.save(inspection);
        return mapToResponseDTO(updatedInspection);
    }

    public InspectionResponseDTO markAsPaid(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + id));

        // Check if already paid
        if (inspection.getIsPaid()) {
            throw new IllegalStateException("Inspection is already marked as paid");
        }

        inspection.setIsPaid(true);
        Inspection updatedInspection = inspectionRepository.save(inspection);
        return mapToResponseDTO(updatedInspection);
    }

    public void deleteInspection(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found with id: " + id));

        // Business rule: Can only delete if not paid
        if (inspection.getIsPaid()) {
            throw new IllegalStateException("Cannot delete paid inspection");
        }

        inspectionRepository.delete(inspection);
    }

    // Helper method for DTO mapping
    private InspectionResponseDTO mapToResponseDTO(Inspection inspection) {
        InspectionResponseDTO dto = new InspectionResponseDTO();
        dto.setId(inspection.getId());
        dto.setCarId(inspection.getCar().getId());
        dto.setCarBrand(inspection.getCar().getBrand());
        dto.setCarModel(inspection.getCar().getModel());
        dto.setCarLicensePlate(inspection.getCar().getLicensePlate());
        dto.setDate(inspection.getDate());
        dto.setReport(inspection.getReport());
        dto.setStatus(inspection.getStatus());
        dto.setIsPaid(inspection.getIsPaid());
        return dto;
    }
}