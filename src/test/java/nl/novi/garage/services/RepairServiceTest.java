package nl.novi.garage.services;

import nl.novi.garage.dtos.repair.*;
import nl.novi.garage.models.*;
import nl.novi.garage.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairServiceTest {

    @Mock
    private RepairRepository repairRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RepairActionsRepository repairActionsRepository;
    @Mock
    private RepairPartsRepository repairPartsRepository;
    @Mock
    private RepairCustomActionsRepository repairCustomActionsRepository;
    @Mock
    private ActionRepository actionRepository;
    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private RepairService repairService;

    private Repair testRepair;
    private Car testCar;
    private Customer testCustomer;
    private RepairRequestDTO testRepairRequestDTO;
    private Action testAction;
    private Part testPart;

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

        testRepair = new Repair();
        testRepair.setId(1L);
        testRepair.setCar(testCar);
        testRepair.setDate(LocalDate.of(2024, 2, 15));
        testRepair.setStatus("IN_PROGRESS");
        testRepair.setReport("Remmen vervangen na APK keuring");
        testRepair.setIsPaid(false);

        testRepairRequestDTO = new RepairRequestDTO();
        testRepairRequestDTO.setCarId(1L);
        testRepairRequestDTO.setDate(LocalDate.of(2024, 2, 15));
        testRepairRequestDTO.setStatus("IN_PROGRESS");
        testRepairRequestDTO.setReport("Remmen vervangen na APK keuring");
        testRepairRequestDTO.setIsPaid(false);

        testAction = new Action();
        testAction.setId(1L);
        testAction.setName("Remmen vervangen");
        testAction.setDescription("Vervangen van remblokken en/of remschijven inclusief montage");
        testAction.setPrice(new BigDecimal("195.00"));

        testPart = new Part();
        testPart.setId(1L);
        testPart.setName("Remblokken set");
        testPart.setPrice(new BigDecimal("45.99"));
        testPart.setStock(25);
    }

    @Test
    void createRepair_ShouldReturnRepairResponseDTO_WhenValidRequest() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(repairRepository.save(any(Repair.class))).thenReturn(testRepair);
        when(repairActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());

        // Act
        RepairResponseDTO result = repairService.createRepair(testRepairRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testRepair.getId(), result.getId());
        assertEquals(testRepair.getCar().getId(), result.getCarId());
        assertEquals(testRepair.getCar().getBrand(), result.getCarBrand());
        assertEquals(testRepair.getCar().getModel(), result.getCarModel());
        assertEquals(testRepair.getCar().getLicensePlate(), result.getCarLicensePlate());
        assertEquals(testRepair.getDate(), result.getDate());
        assertEquals(testRepair.getStatus(), result.getStatus());
        assertEquals(testRepair.getReport(), result.getReport());
        assertEquals(testRepair.getIsPaid(), result.getIsPaid());
        verify(carRepository, times(1)).findById(1L);
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void createRepair_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairService.createRepair(testRepairRequestDTO));

        assertEquals("Car not found with id: 1", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(repairRepository, never()).save(any(Repair.class));
    }

    @Test
    void getAllRepairs_ShouldReturnListOfRepairs() {
        // Arrange
        Repair repair2 = new Repair();
        repair2.setId(2L);
        repair2.setCar(testCar);
        repair2.setDate(LocalDate.of(2024, 3, 15));
        repair2.setStatus("COMPLETED");
        repair2.setReport("Onderhoudsbeurt voltooid");
        repair2.setIsPaid(true);

        List<Repair> repairs = Arrays.asList(testRepair, repair2);
        when(repairRepository.findAll()).thenReturn(repairs);
        when(repairActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());

        // Act
        List<RepairResponseDTO> result = repairService.getAllRepairs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testRepair.getStatus(), result.get(0).getStatus());
        assertEquals(repair2.getStatus(), result.get(1).getStatus());
        verify(repairRepository, times(1)).findAll();
    }

    @Test
    void getRepairById_ShouldReturnRepair_WhenRepairExists() {
        // Arrange
        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(repairActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());

        // Act
        RepairResponseDTO result = repairService.getRepairById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testRepair.getId(), result.getId());
        assertEquals(testRepair.getReport(), result.getReport());
        assertEquals(testRepair.getStatus(), result.getStatus());
        verify(repairRepository, times(1)).findById(1L);
    }

    @Test
    void getRepairById_ShouldThrowException_WhenRepairNotFound() {
        // Arrange
        when(repairRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairService.getRepairById(999L));

        assertEquals("Repair not found with id: 999", exception.getMessage());
        verify(repairRepository, times(1)).findById(999L);
    }

    @Test
    void addActionToRepair_ShouldReturnRepairActionItemDTO_WhenValidRequest() {
        // Arrange
        AddActionToRepairDTO addActionDTO = new AddActionToRepairDTO(1L, 1);
        RepairActions repairAction = new RepairActions(testRepair, testAction, 1);
        repairAction.setId(1L);

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));
        when(repairActionsRepository.findByRepairIdAndActionId(1L, 1L)).thenReturn(Optional.empty());
        when(repairActionsRepository.save(any(RepairActions.class))).thenReturn(repairAction);

        // Act
        RepairActionItemDTO result = repairService.addActionToRepair(1L, addActionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(repairAction.getId(), result.getId());
        assertEquals(testAction.getId(), result.getActionId());
        assertEquals(testAction.getName(), result.getActionName());
        assertEquals(testAction.getDescription(), result.getActionDescription());
        assertEquals(testAction.getPrice(), result.getActionPrice());
        assertEquals(1, result.getAmount());
        verify(repairRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).findById(1L);
        verify(repairActionsRepository, times(1)).save(any(RepairActions.class));
    }

    @Test
    void addActionToRepair_ShouldThrowException_WhenActionAlreadyExists() {
        // Arrange
        AddActionToRepairDTO addActionDTO = new AddActionToRepairDTO(1L, 1);
        RepairActions existingRepairAction = new RepairActions(testRepair, testAction, 1);

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));
        when(repairActionsRepository.findByRepairIdAndActionId(1L, 1L)).thenReturn(Optional.of(existingRepairAction));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairService.addActionToRepair(1L, addActionDTO));

        assertEquals("Action already added to this repair", exception.getMessage());
        verify(repairActionsRepository, never()).save(any(RepairActions.class));
    }

    @Test
    void addPartToRepair_ShouldReturnRepairPartItemDTO_WhenValidRequest() {
        // Arrange
        AddPartToRepairDTO addPartDTO = new AddPartToRepairDTO(1L, 2);
        RepairParts repairPart = new RepairParts(testRepair, testPart, 2);
        repairPart.setId(1L);

        Part updatedPart = new Part();
        updatedPart.setId(1L);
        updatedPart.setName("Remblokken set");
        updatedPart.setPrice(new BigDecimal("45.99"));
        updatedPart.setStock(23); // Reduced by 2

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(repairPartsRepository.findByRepairIdAndPartId(1L, 1L)).thenReturn(Optional.empty());
        when(partRepository.save(any(Part.class))).thenReturn(updatedPart);
        when(repairPartsRepository.save(any(RepairParts.class))).thenReturn(repairPart);

        // Act
        RepairPartItemDTO result = repairService.addPartToRepair(1L, addPartDTO);

        // Assert
        assertNotNull(result);
        assertEquals(repairPart.getId(), result.getId());
        assertEquals(testPart.getId(), result.getPartId());
        assertEquals(testPart.getName(), result.getPartName());
        assertEquals(testPart.getPrice(), result.getPartPrice());
        assertEquals(2, result.getAmount());
        verify(repairRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).save(any(Part.class));
        verify(repairPartsRepository, times(1)).save(any(RepairParts.class));
    }

    @Test
    void addPartToRepair_ShouldThrowException_WhenInsufficientStock() {
        // Arrange
        AddPartToRepairDTO addPartDTO = new AddPartToRepairDTO(1L, 30); // More than available stock (25)

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairService.addPartToRepair(1L, addPartDTO));

        assertEquals("Insufficient stock. Available: 25, Required: 30", exception.getMessage());
        verify(partRepository, never()).save(any(Part.class));
        verify(repairPartsRepository, never()).save(any(RepairParts.class));
    }

    @Test
    void addCustomActionToRepair_ShouldReturnRepairCustomActionItemDTO_WhenValidRequest() {
        // Arrange
        AddCustomActionToRepairDTO addCustomActionDTO = new AddCustomActionToRepairDTO(
                "Extra controle na reparatie", new BigDecimal("25.00"));
        RepairCustomActions customAction = new RepairCustomActions(testRepair,
                "Extra controle na reparatie", new BigDecimal("25.00"));
        customAction.setId(1L);

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(repairCustomActionsRepository.save(any(RepairCustomActions.class))).thenReturn(customAction);

        // Act
        RepairCustomActionItemDTO result = repairService.addCustomActionToRepair(1L, addCustomActionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(customAction.getId(), result.getId());
        assertEquals(customAction.getDescription(), result.getDescription());
        assertEquals(customAction.getPrice(), result.getPrice());
        verify(repairRepository, times(1)).findById(1L);
        verify(repairCustomActionsRepository, times(1)).save(any(RepairCustomActions.class));
    }

    @Test
    void removePartFromRepair_ShouldRestoreStock_WhenValidRequest() {
        // Arrange
        RepairParts repairPart = new RepairParts(testRepair, testPart, 2);
        Part updatedPart = new Part();
        updatedPart.setId(1L);
        updatedPart.setName("Remblokken set");
        updatedPart.setPrice(new BigDecimal("45.99"));
        updatedPart.setStock(27); // Restored by 2

        when(repairPartsRepository.findByRepairIdAndPartId(1L, 1L)).thenReturn(Optional.of(repairPart));
        when(partRepository.save(any(Part.class))).thenReturn(updatedPart);

        // Act
        repairService.removePartFromRepair(1L, 1L);

        // Assert
        verify(repairPartsRepository, times(1)).findByRepairIdAndPartId(1L, 1L);
        verify(partRepository, times(1)).save(any(Part.class));
        verify(repairPartsRepository, times(1)).delete(repairPart);
    }

    @Test
    void markAsPaid_ShouldReturnPaidRepair_WhenNotPaidYet() {
        // Arrange
        testRepair.setIsPaid(false);

        Repair paidRepair = new Repair();
        paidRepair.setId(1L);
        paidRepair.setCar(testCar);
        paidRepair.setDate(LocalDate.of(2024, 2, 15));
        paidRepair.setStatus("IN_PROGRESS");
        paidRepair.setReport("Remmen vervangen na APK keuring");
        paidRepair.setIsPaid(true);

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(repairRepository.save(any(Repair.class))).thenReturn(paidRepair);
        when(repairActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(1L)).thenReturn(Arrays.asList());

        // Act
        RepairResponseDTO result = repairService.markAsPaid(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());
        verify(repairRepository, times(1)).findById(1L);
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void markAsPaid_ShouldThrowException_WhenAlreadyPaid() {
        // Arrange
        testRepair.setIsPaid(true);
        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repairService.markAsPaid(1L));

        assertEquals("Repair is already marked as paid", exception.getMessage());
        verify(repairRepository, times(1)).findById(1L);
        verify(repairRepository, never()).save(any(Repair.class));
    }

    @Test
    void deleteRepair_ShouldRestoreStockAndDelete_WhenNotPaid() {
        // Arrange
        testRepair.setIsPaid(false);
        RepairParts repairPart = new RepairParts(testRepair, testPart, 2);
        List<RepairParts> repairParts = Arrays.asList(repairPart);

        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(repairPartsRepository.findByRepairId(1L)).thenReturn(repairParts);

        // Act
        repairService.deleteRepair(1L);

        // Assert
        verify(repairRepository, times(1)).findById(1L);
        verify(repairPartsRepository, times(1)).findByRepairId(1L);
        verify(partRepository, times(1)).save(any(Part.class));
        verify(repairRepository, times(1)).delete(testRepair);
    }

    @Test
    void deleteRepair_ShouldThrowException_WhenRepairIsPaid() {
        // Arrange
        testRepair.setIsPaid(true);
        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> repairService.deleteRepair(1L));

        assertEquals("Cannot delete paid repair", exception.getMessage());
        verify(repairRepository, times(1)).findById(1L);
        verify(repairRepository, never()).delete(any(Repair.class));
    }

    @Test
    void getRepairsByStatus_ShouldReturnMatchingRepairs() {
        // Arrange
        String status = "IN_PROGRESS";
        List<Repair> repairs = Arrays.asList(testRepair);
        when(repairRepository.findByStatus(status)).thenReturn(repairs);
        when(repairActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());

        // Act
        List<RepairResponseDTO> result = repairService.getRepairsByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(repairRepository, times(1)).findByStatus(status);
    }

    @Test
    void getRepairsByPaymentStatus_ShouldReturnMatchingRepairs() {
        // Arrange
        Boolean isPaid = false;
        List<Repair> repairs = Arrays.asList(testRepair);
        when(repairRepository.findByIsPaid(isPaid)).thenReturn(repairs);
        when(repairActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairPartsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());
        when(repairCustomActionsRepository.findByRepairId(anyLong())).thenReturn(Arrays.asList());

        // Act
        List<RepairResponseDTO> result = repairService.getRepairsByPaymentStatus(isPaid);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(isPaid, result.get(0).getIsPaid());
        verify(repairRepository, times(1)).findByIsPaid(isPaid);
    }
}