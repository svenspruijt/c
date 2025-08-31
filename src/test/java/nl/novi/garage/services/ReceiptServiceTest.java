package nl.novi.garage.services;

import nl.novi.garage.dtos.receipt.ReceiptGenerateRequestDTO;
import nl.novi.garage.dtos.receipt.ReceiptPaymentDTO;
import nl.novi.garage.dtos.receipt.ReceiptResponseDTO;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private ReceiptInspectionsRepository receiptInspectionsRepository;
    @Mock
    private ReceiptRepairsRepository receiptRepairsRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InspectionRepository inspectionRepository;
    @Mock
    private RepairRepository repairRepository;
    @Mock
    private RepairActionsRepository repairActionsRepository;
    @Mock
    private RepairPartsRepository repairPartsRepository;
    @Mock
    private RepairCustomActionsRepository repairCustomActionsRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private Customer testCustomer;
    private Car testCar;
    private Inspection testInspection;
    private Repair testRepair;
    private Receipt testReceipt;
    private ReceiptGenerateRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jan Jansen");
        testCustomer.setPhonenumber("+31612345678");

        // Setup car
        testCar = new Car();
        testCar.setId(1L);
        testCar.setCustomer(testCustomer);
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setLicensePlate("AB-123-CD");

        // Setup inspection
        testInspection = new Inspection();
        testInspection.setId(1L);
        testInspection.setCar(testCar);
        testInspection.setDate(LocalDate.now());
        testInspection.setReport("Inspection completed");
        testInspection.setStatus("COMPLETED");
        testInspection.setIsPaid(false);

        // Setup repair
        testRepair = new Repair();
        testRepair.setId(1L);
        testRepair.setCar(testCar);
        testRepair.setDate(LocalDate.now());
        testRepair.setStatus("COMPLETED");
        testRepair.setReport("Repair completed");
        testRepair.setIsPaid(false);

        // Setup receipt
        testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setCustomer(testCustomer);
        testReceipt.setTotalExclVat(new BigDecimal("50.00"));
        testReceipt.setVat(new BigDecimal("10.50"));
        testReceipt.setTotalInclVat(new BigDecimal("60.50"));
        testReceipt.setIsPaid(false);
        testReceipt.setCreatedDate(LocalDate.now());

        // Setup request DTO
        testRequestDTO = new ReceiptGenerateRequestDTO();
        testRequestDTO.setCustomerId(1L);
        testRequestDTO.setInspectionIds(Arrays.asList(1L));
        testRequestDTO.setRepairIds(Arrays.asList(1L));
    }

    @Test
    void generateReceipt_ShouldReturnReceiptResponseDTO_WhenValidRequest() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));
        when(repairRepository.findById(1L)).thenReturn(Optional.of(testRepair));
        when(repairActionsRepository.findByRepairId(1L)).thenReturn(new ArrayList<>());
        when(repairPartsRepository.findByRepairId(1L)).thenReturn(new ArrayList<>());
        when(repairCustomActionsRepository.findByRepairId(1L)).thenReturn(new ArrayList<>());
        when(receiptRepository.save(any(Receipt.class))).thenReturn(testReceipt);
        when(receiptInspectionsRepository.save(any(ReceiptInspections.class)))
                .thenReturn(new ReceiptInspections());
        when(receiptRepairsRepository.save(any(ReceiptRepairs.class))).thenReturn(new ReceiptRepairs());

        // Act
        ReceiptResponseDTO result = receiptService.generateReceipt(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testReceipt.getId(), result.getId());
        assertEquals(testCustomer.getId(), result.getCustomer().getId());
        assertEquals(new BigDecimal("50.00"), result.getTotalExclVat());
        assertEquals(new BigDecimal("10.50"), result.getVat());
        assertEquals(new BigDecimal("60.50"), result.getTotalInclVat());
        assertFalse(result.getIsPaid());

        verify(customerRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).findById(1L);
        verify(repairRepository, times(1)).findById(1L);
        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(receiptInspectionsRepository, times(1)).save(any(ReceiptInspections.class));
        verify(receiptRepairsRepository, times(1)).save(any(ReceiptRepairs.class));
    }

    @Test
    void generateReceipt_ShouldThrowException_WhenCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> receiptService.generateReceipt(testRequestDTO));

        assertEquals("Customer not found with id: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void generateReceipt_ShouldThrowException_WhenInspectionNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(inspectionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> receiptService.generateReceipt(testRequestDTO));

        assertEquals("Inspection not found with id: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(inspectionRepository, times(1)).findById(1L);
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void generateReceipt_ShouldThrowException_WhenInspectionDoesNotBelongToCustomer() {
        // Arrange
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        Car otherCar = new Car();
        otherCar.setCustomer(otherCustomer);
        testInspection.setCar(otherCar);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> receiptService.generateReceipt(testRequestDTO));

        assertEquals("Inspection 1 does not belong to customer 1", exception.getMessage());
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void generateReceipt_ShouldThrowException_WhenInspectionAlreadyPaid() {
        // Arrange
        testInspection.setIsPaid(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> receiptService.generateReceipt(testRequestDTO));

        assertEquals("Inspection 1 is already paid", exception.getMessage());
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void generateReceipt_ShouldCalculateCorrectVAT_ForInspectionOnly() {
        // Arrange
        testRequestDTO.setRepairIds(null); // Only inspection
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(inspectionRepository.findById(1L)).thenReturn(Optional.of(testInspection));

        Receipt expectedReceipt = new Receipt();
        expectedReceipt.setId(1L);
        expectedReceipt.setCustomer(testCustomer);
        expectedReceipt.setTotalExclVat(new BigDecimal("50.00")); // €50 inspection fee
        expectedReceipt.setVat(new BigDecimal("10.50")); // 21% VAT
        expectedReceipt.setTotalInclVat(new BigDecimal("60.50"));
        expectedReceipt.setIsPaid(false);
        expectedReceipt.setCreatedDate(LocalDate.now());

        when(receiptRepository.save(any(Receipt.class))).thenReturn(expectedReceipt);
        when(receiptInspectionsRepository.save(any(ReceiptInspections.class)))
                .thenReturn(new ReceiptInspections());

        // Act
        ReceiptResponseDTO result = receiptService.generateReceipt(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result.getTotalExclVat());
        assertEquals(new BigDecimal("10.50"), result.getVat());
        assertEquals(new BigDecimal("60.50"), result.getTotalInclVat());

        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(receiptInspectionsRepository, times(1)).save(any(ReceiptInspections.class));
        verify(receiptRepairsRepository, never()).save(any(ReceiptRepairs.class));
    }

    @Test
    void getAllReceipts_ShouldReturnListOfReceipts() {
        // Arrange
        Receipt receipt2 = new Receipt();
        receipt2.setId(2L);
        receipt2.setCustomer(testCustomer);
        receipt2.setTotalExclVat(new BigDecimal("100.00"));
        receipt2.setVat(new BigDecimal("21.00"));
        receipt2.setTotalInclVat(new BigDecimal("121.00"));
        receipt2.setIsPaid(true);
        receipt2.setCreatedDate(LocalDate.now());

        List<Receipt> receipts = Arrays.asList(testReceipt, receipt2);
        when(receiptRepository.findAll()).thenReturn(receipts);

        // Act
        List<ReceiptResponseDTO> result = receiptService.getAllReceipts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReceipt.getId(), result.get(0).getId());
        assertEquals(receipt2.getId(), result.get(1).getId());
        verify(receiptRepository, times(1)).findAll();
    }

    @Test
    void getReceiptById_ShouldReturnReceiptWithDetails_WhenReceiptExists() {
        // Arrange
        when(receiptRepository.findById(1L)).thenReturn(Optional.of(testReceipt));
        when(receiptInspectionsRepository.findByReceiptId(1L)).thenReturn(new ArrayList<>());
        when(receiptRepairsRepository.findByReceiptId(1L)).thenReturn(new ArrayList<>());

        // Act
        ReceiptResponseDTO result = receiptService.getReceiptById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReceipt.getId(), result.getId());
        assertEquals(testCustomer.getId(), result.getCustomer().getId());
        assertNotNull(result.getInspections());
        assertNotNull(result.getRepairs());
        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptInspectionsRepository, times(1)).findByReceiptId(1L);
        verify(receiptRepairsRepository, times(1)).findByReceiptId(1L);
    }

    @Test
    void getReceiptById_ShouldThrowException_WhenReceiptNotFound() {
        // Arrange
        when(receiptRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> receiptService.getReceiptById(999L));

        assertEquals("Receipt not found with id: 999", exception.getMessage());
        verify(receiptRepository, times(1)).findById(999L);
    }

    @Test
    void getReceiptsByCustomerId_ShouldReturnCustomerReceipts() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(receiptRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testReceipt));

        // Act
        List<ReceiptResponseDTO> result = receiptService.getReceiptsByCustomerId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReceipt.getId(), result.get(0).getId());
        verify(customerRepository, times(1)).findById(1L);
        verify(receiptRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void getReceiptsByPaymentStatus_ShouldReturnFilteredReceipts() {
        // Arrange
        when(receiptRepository.findByIsPaid(false)).thenReturn(Arrays.asList(testReceipt));

        // Act
        List<ReceiptResponseDTO> result = receiptService.getReceiptsByPaymentStatus(false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReceipt.getId(), result.get(0).getId());
        assertFalse(result.get(0).getIsPaid());
        verify(receiptRepository, times(1)).findByIsPaid(false);
    }

    @Test
    void markAsPaid_ShouldMarkReceiptAndRelatedItemsAsPaid() {
        // Arrange
        ReceiptInspections receiptInspection = new ReceiptInspections(testReceipt, testInspection);
        ReceiptRepairs receiptRepair = new ReceiptRepairs(testReceipt, testRepair);

        Receipt paidReceipt = new Receipt();
        paidReceipt.setId(1L);
        paidReceipt.setCustomer(testCustomer);
        paidReceipt.setTotalExclVat(testReceipt.getTotalExclVat());
        paidReceipt.setVat(testReceipt.getVat());
        paidReceipt.setTotalInclVat(testReceipt.getTotalInclVat());
        paidReceipt.setIsPaid(true);
        paidReceipt.setCreatedDate(testReceipt.getCreatedDate());

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(testReceipt));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(paidReceipt);
        when(receiptInspectionsRepository.findByReceiptId(1L)).thenReturn(Arrays.asList(receiptInspection));
        when(receiptRepairsRepository.findByReceiptId(1L)).thenReturn(Arrays.asList(receiptRepair));
        when(inspectionRepository.save(any(Inspection.class))).thenReturn(testInspection);
        when(repairRepository.save(any(Repair.class))).thenReturn(testRepair);

        // Act
        ReceiptResponseDTO result = receiptService.markAsPaid(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsPaid());
        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptRepository, times(1)).save(any(Receipt.class));
        verify(inspectionRepository, times(1)).save(any(Inspection.class));
        verify(repairRepository, times(1)).save(any(Repair.class));
    }

    @Test
    void markAsPaid_ShouldThrowException_WhenReceiptAlreadyPaid() {
        // Arrange
        testReceipt.setIsPaid(true);
        when(receiptRepository.findById(1L)).thenReturn(Optional.of(testReceipt));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> receiptService.markAsPaid(1L));

        assertEquals("Receipt is already marked as paid", exception.getMessage());
        verify(receiptRepository, times(1)).findById(1L);
        verify(receiptRepository, never()).save(any(Receipt.class));
    }

    @Test
    void updatePaymentStatus_ShouldNotAllowMarkingPaidReceiptAsUnpaid() {
        // Arrange
        testReceipt.setIsPaid(true);
        ReceiptPaymentDTO paymentDTO = new ReceiptPaymentDTO(false);

        when(receiptRepository.findById(1L)).thenReturn(Optional.of(testReceipt));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> receiptService.updatePaymentStatus(1L, paymentDTO));

        assertEquals("Cannot mark a paid receipt as unpaid", exception.getMessage());
        verify(receiptRepository, times(1)).findById(1L);
    }

    @Test
    void updatePaymentStatus_ShouldReturnUnchangedReceipt_WhenAlreadyUnpaidAndRequestingUnpaid() {
        // Arrange
        ReceiptPaymentDTO paymentDTO = new ReceiptPaymentDTO(false);
        when(receiptRepository.findById(1L)).thenReturn(Optional.of(testReceipt));

        // Act
        ReceiptResponseDTO result = receiptService.updatePaymentStatus(1L, paymentDTO);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsPaid());
        verify(receiptRepository, times(1)).findById(1L);
    }
}