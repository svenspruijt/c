package nl.novi.garage.services;

import nl.novi.garage.dtos.inspection.InspectionRequestDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.models.Inspection;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.Customer;
import nl.novi.garage.repositories.InspectionRepository;
import nl.novi.garage.repositories.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private InspectionService inspectionService;

    private Inspection testInspection;
    private Car testCar;
    private Customer testCustomer;
    private InspectionRequestDTO testInspectionRequestDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jan Jansen");
        testCustomer.setPhonenumber("+31612345678");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCustomer(testCustomer);
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setLicensePlate("AB-123-CD");

        testInspection = new Inspection();
        testInspection.setId(1L);
        testInspection.setCar(testCar);
        testInspection.setDate(LocalDate.of(2024, 1, 15));
        testInspection.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        testInspection.setStatus("COMPLETED");
        testInspection.setIsPaid(true);

        testInspectionRequestDTO = new InspectionRequestDTO();
        testInspectionRequestDTO.setCarId(1L);
        testInspectionRequestDTO.setDate(LocalDate.of(2024, 1, 15));
        testInspectionRequestDTO.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        testInspectionRequestDTO.setStatus("COMPLETED");
        testInspectionRequestDTO.setIsPaid(true);
    }

    @Test
    void createInspection_ShouldReturnInspectionResponseDTO_WhenValidRequest() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(testInspection);

        // Act
        InspectionResponseDTO result = inspectionService.createInspection(testInspectionRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testInspection.getId(), result.getId());
        assertEquals(testInspection.getCar().getId(), result.getCarId());
        assertEquals(testInspection.getCar().getBrand(), result.getCarBrand());
        assertEquals(testInspection.getCar().getModel(), result.getCarModel());
        assertEquals(testInspection.getCar().getLicensePlate(), result.getCarLicensePlate());
        assertEquals(testInspection.getDate(), result.getDate());
        assertEquals(testInspection.getReport(), result.getReport());
        assertEquals(testInspection.getStatus(), result.getStatus());
        assertEquals(testInspection.getIsPaid(), result.getIsPaid());
        verify(carRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
    }

    @Test
    void createInspection_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inspectionService.createInspection(testInspectionRequestDTO));

        assertEquals("Car not found with id: 1", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(inspectionRepository, never()).save(any(Inspection.class));
    }

    @Test
    void getAllInspections_ShouldReturnListOfInspections() {
        // Arrange
        Inspection inspection2 = new Inspection();
        inspection2.setId(2L);
        inspection2.setCar(testCar);
        inspection2.setDate(LocalDate.of(2024, 2, 15));
        inspection2.setReport("Tweede keuring uitgevoerd.");
        inspection2.setStatus("IN_PROGRESS");
        inspection2.setIsPaid(false);

        List<Inspection> inspections = Arrays.asList(testInspection, inspection2);
        when(inspectionRepository.findAll()).thenReturn(inspections);

        // Act
        List<InspectionResponseDTO> result = inspectionService.getAllInspections();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testInspection.getStatus(), result.get(0).getStatus());
        assertEquals(inspection2.getStatus(), result.get(1).getStatus());
        verify(inspectionRepository, times(1)).findAll();
    }

    @Test
    void getInspectionById_ShouldReturnInspection_WhenInspectionExists() {
        // Arrange
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act
        InspectionResponseDTO result = inspectionService.getInspectionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testInspection.getId(), result.getId());
        assertEquals(testInspection.getReport(), result.getReport());
        assertEquals(testInspection.getStatus(), result.getStatus());
        verify(inspectionRepository, times(1)).findById(1L);
    }

    @Test
    void getInspectionById_ShouldThrowException_WhenInspectionNotFound() {
        // Arrange
        when(inspectionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inspectionService.getInspectionById(999L));

        assertEquals("Inspection not found with id: 999", exception.getMessage());
        verify(inspectionRepository, times(1)).findById(999L);
    }

    @Test
    void getInspectionsByCarId_ShouldReturnInspections_WhenCarExists() {
        // Arrange
        List<Inspection> inspections = Arrays.asList(testInspection);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(inspectionRepository.findByCarIdOrderByDateDesc(1L)).thenReturn(inspections);

        // Act
        List<InspectionResponseDTO> result = inspectionService.getInspectionsByCarId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testInspection.getReport(), result.get(0).getReport());
        verify(carRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).findByCarIdOrderByDateDesc(1L);
    }

    @Test
    void getInspectionsByCarId_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inspectionService.getInspectionsByCarId(999L));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
        verify(inspectionRepository, never()).findByCarIdOrderByDateDesc(anyLong());
    }

    @Test
    void getInspectionsByStatus_ShouldReturnMatchingInspections() {
        // Arrange
        String status = "COMPLETED";
        List<Inspection> inspections = Arrays.asList(testInspection);
        when(inspectionRepository.findByStatus(status)).thenReturn(inspections);

        // Act
        List<InspectionResponseDTO> result = inspectionService.getInspectionsByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(inspectionRepository, times(1)).findByStatus(status);
    }

    @Test
    void getInspectionsByPaymentStatus_ShouldReturnMatchingInspections() {
        // Arrange
        Boolean isPaid = true;
        List<Inspection> inspections = Arrays.asList(testInspection);
        when(inspectionRepository.findByIsPaid(isPaid)).thenReturn(inspections);

        // Act
        List<InspectionResponseDTO> result = inspectionService.getInspectionsByPaymentStatus(isPaid);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(isPaid, result.get(0).getIsPaid());
        verify(inspectionRepository, times(1)).findByIsPaid(isPaid);
    }

    @Test
    void getInspectionsByDateRange_ShouldReturnInspectionsInRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<Inspection> inspections = Arrays.asList(testInspection);
        when(inspectionRepository.findByDateBetween(startDate, endDate)).thenReturn(inspections);

        // Act
        List<InspectionResponseDTO> result = inspectionService.getInspectionsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(inspectionRepository, times(1)).findByDateBetween(startDate, endDate);
    }

    @Test
    void updateInspection_ShouldReturnUpdatedInspection_WhenValidRequest() {
        // Arrange
        InspectionRequestDTO updateDTO = new InspectionRequestDTO();
        updateDTO.setCarId(1L);
        updateDTO.setDate(LocalDate.of(2024, 2, 15));
        updateDTO.setReport("Updated report");
        updateDTO.setStatus("IN_PROGRESS");
        updateDTO.setIsPaid(false);

        Inspection updatedInspection = new Inspection();
        updatedInspection.setId(1L);
        updatedInspection.setCar(testCar);
        updatedInspection.setDate(LocalDate.of(2024, 2, 15));
        updatedInspection.setReport("Updated report");
        updatedInspection.setStatus("IN_PROGRESS");
        updatedInspection.setIsPaid(false);

        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(updatedInspection);

        // Act
        InspectionResponseDTO result = inspectionService.updateInspection(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedInspection.getReport(), result.getReport());
        assertEquals(updatedInspection.getStatus(), result.getStatus());
        assertEquals(updatedInspection.getIsPaid(), result.getIsPaid());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
    }

    @Test
    void updateInspection_ShouldUpdateCar_WhenCarIdChanges() {
        // Arrange
        Car newCar = new Car();
        newCar.setId(2L);
        newCar.setCustomer(testCustomer);
        newCar.setBrand("Honda");
        newCar.setModel("Civic");
        newCar.setLicensePlate("XY-456-ZW");

        InspectionRequestDTO updateDTO = new InspectionRequestDTO();
        updateDTO.setCarId(2L);
        updateDTO.setDate(LocalDate.of(2024, 2, 15));
        updateDTO.setReport("Updated report");
        updateDTO.setStatus("IN_PROGRESS");
        updateDTO.setIsPaid(false);

        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));
        when(carRepository.findById(2L)).thenReturn(Optional.of(newCar));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(testInspection);

        // Act
        InspectionResponseDTO result = inspectionService.updateInspection(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(inspectionRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).findById(2L);
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
    }

    @Test
    void completeInspection_ShouldReturnCompletedInspection_WhenValidRequest() {
        // Arrange
        testInspection.setStatus("IN_PROGRESS");
        String finalReport = "Final inspection report with all details";

        Inspection completedInspection = new Inspection();
        completedInspection.setId(1L);
        completedInspection.setCar(testCar);
        completedInspection.setDate(LocalDate.of(2024, 1, 15));
        completedInspection.setReport(finalReport);
        completedInspection.setStatus("COMPLETED");
        completedInspection.setIsPaid(false);

        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(completedInspection);

        // Act
        InspectionResponseDTO result = inspectionService.completeInspection(1L, finalReport);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(finalReport, result.getReport());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
    }

    @Test
    void completeInspection_ShouldThrowException_WhenAlreadyCompleted() {
        // Arrange
        testInspection.setStatus("COMPLETED");
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inspectionService.completeInspection(1L, "Final report"));

        assertEquals("Inspection is already completed", exception.getMessage());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, never()).save(any(Inspection.class));
    }

    @Test
    void markAsPaid_ShouldReturnPaidInspection_WhenNotPaidYet() {
        // Arrange
        testInspection.setIsPaid(false);

        Inspection paidInspection = new Inspection();
        paidInspection.setId(1L);
        paidInspection.setCar(testCar);
        paidInspection.setDate(LocalDate.of(2024, 1, 15));
        paidInspection.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        paidInspection.setStatus("COMPLETED");
        paidInspection.setIsPaid(true);

        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(paidInspection);

        // Act
        InspectionResponseDTO result = inspectionService.markAsPaid(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
    }

    @Test
    void markAsPaid_ShouldThrowException_WhenAlreadyPaid() {
        // Arrange
        testInspection.setIsPaid(true);
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inspectionService.markAsPaid(1L));

        assertEquals("Inspection is already marked as paid", exception.getMessage());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, never()).save(any(Inspection.class));
    }

    @Test
    void deleteInspection_ShouldDeleteSuccessfully_WhenNotPaid() {
        // Arrange
        testInspection.setIsPaid(false);
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act
        inspectionService.deleteInspection(1L);

        // Assert
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).delete(testInspection);
    }

    @Test
    void deleteInspection_ShouldThrowException_WhenInspectionIsPaid() {
        // Arrange
        testInspection.setIsPaid(true);
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> inspectionService.deleteInspection(1L));

        assertEquals("Cannot delete paid inspection", exception.getMessage());
        verify(inspectionRepository, times(1)).findById(1L);
        verify(inspectionRepository, never()).delete(any(Inspection.class));
    }

    @Test
    void deleteInspection_ShouldThrowException_WhenInspectionNotFound() {
        // Arrange
        when(inspectionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inspectionService.deleteInspection(999L));

        assertEquals("Inspection not found with id: 999", exception.getMessage());
        verify(inspectionRepository, times(1)).findById(999L);
        verify(inspectionRepository, never()).delete(any(Inspection.class));
    }
}