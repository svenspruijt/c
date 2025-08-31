package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.dtos.receipt.ReceiptGenerateRequestDTO;
import nl.novi.garage.dtos.receipt.ReceiptPaymentDTO;
import nl.novi.garage.dtos.receipt.ReceiptResponseDTO;
import nl.novi.garage.services.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReceiptController.class, excludeFilters = {
        @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = {
                nl.novi.garage.security.JwtRequestFilter.class,
                nl.novi.garage.security.JwtService.class,
                nl.novi.garage.services.UserDetailsServiceImpl.class
        })
}, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class ReceiptControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiptService receiptService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReceiptGenerateRequestDTO receiptGenerateRequestDTO;
    private ReceiptResponseDTO receiptResponseDTO;
    private ReceiptPaymentDTO receiptPaymentDTO;
    private CustomerResponseDTO customerResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup customer
        customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setId(1L);
        customerResponseDTO.setName("Jan Jansen");
        customerResponseDTO.setPhonenumber("+31612345678");

        // Setup request DTO
        receiptGenerateRequestDTO = new ReceiptGenerateRequestDTO();
        receiptGenerateRequestDTO.setCustomerId(1L);
        receiptGenerateRequestDTO.setInspectionIds(Arrays.asList(1L, 2L));
        receiptGenerateRequestDTO.setRepairIds(Arrays.asList(1L));

        // Setup response DTO
        receiptResponseDTO = new ReceiptResponseDTO();
        receiptResponseDTO.setId(1L);
        receiptResponseDTO.setCustomer(customerResponseDTO);
        receiptResponseDTO.setTotalExclVat(new BigDecimal("100.00"));
        receiptResponseDTO.setVat(new BigDecimal("21.00"));
        receiptResponseDTO.setTotalInclVat(new BigDecimal("121.00"));
        receiptResponseDTO.setIsPaid(false);
        receiptResponseDTO.setCreatedDate(LocalDate.now());

        // Setup payment DTO
        receiptPaymentDTO = new ReceiptPaymentDTO();
        receiptPaymentDTO.setIsPaid(true);
    }

    @Test
    void generateReceipt_ShouldReturnCreatedReceipt_WhenValidRequest() throws Exception {
        // Arrange
        when(receiptService.generateReceipt(any(ReceiptGenerateRequestDTO.class))).thenReturn(receiptResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/receipts/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptGenerateRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.customer.name").value("Jan Jansen"))
                .andExpect(jsonPath("$.totalExclVat").value(100.00))
                .andExpect(jsonPath("$.vat").value(21.00))
                .andExpect(jsonPath("$.totalInclVat").value(121.00))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(receiptService, times(1)).generateReceipt(any(ReceiptGenerateRequestDTO.class));
    }

    @Test
    void generateReceipt_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        ReceiptGenerateRequestDTO invalidRequest = new ReceiptGenerateRequestDTO();
        // Missing customerId - should trigger validation error

        // Act & Assert
        mockMvc.perform(post("/receipts/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(receiptService, never()).generateReceipt(any(ReceiptGenerateRequestDTO.class));
    }

    @Test
    void generateReceipt_ShouldReturnBadRequest_WhenCustomerNotFound() throws Exception {
        // Arrange
        when(receiptService.generateReceipt(any(ReceiptGenerateRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Customer not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/receipts/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptGenerateRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).generateReceipt(any(ReceiptGenerateRequestDTO.class));
    }

    @Test
    void getAllReceipts_ShouldReturnReceiptList() throws Exception {
        // Arrange
        ReceiptResponseDTO receipt2 = new ReceiptResponseDTO();
        receipt2.setId(2L);
        receipt2.setCustomer(customerResponseDTO);
        receipt2.setTotalExclVat(new BigDecimal("50.00"));
        receipt2.setVat(new BigDecimal("10.50"));
        receipt2.setTotalInclVat(new BigDecimal("60.50"));
        receipt2.setIsPaid(true);
        receipt2.setCreatedDate(LocalDate.now());

        List<ReceiptResponseDTO> receipts = Arrays.asList(receiptResponseDTO, receipt2);
        when(receiptService.getAllReceipts()).thenReturn(receipts);

        // Act & Assert
        mockMvc.perform(get("/receipts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].isPaid").value(true));

        verify(receiptService, times(1)).getAllReceipts();
    }

    @Test
    void getReceiptById_ShouldReturnReceipt_WhenReceiptExists() throws Exception {
        // Arrange
        when(receiptService.getReceiptById(1L)).thenReturn(receiptResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customer.name").value("Jan Jansen"))
                .andExpect(jsonPath("$.totalInclVat").value(121.00))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(receiptService, times(1)).getReceiptById(1L);
    }

    @Test
    void getReceiptById_ShouldReturnNotFound_WhenReceiptNotExists() throws Exception {
        // Arrange
        when(receiptService.getReceiptById(999L))
                .thenThrow(new IllegalArgumentException("Receipt not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/receipts/999"))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).getReceiptById(999L);
    }

    @Test
    void getReceiptsByCustomerId_ShouldReturnCustomerReceipts() throws Exception {
        // Arrange
        List<ReceiptResponseDTO> receipts = Arrays.asList(receiptResponseDTO);
        when(receiptService.getReceiptsByCustomerId(1L)).thenReturn(receipts);

        // Act & Assert
        mockMvc.perform(get("/receipts/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customer.id").value(1))
                .andExpect(jsonPath("$[0].customer.name").value("Jan Jansen"));

        verify(receiptService, times(1)).getReceiptsByCustomerId(1L);
    }

    @Test
    void getReceiptsByCustomerId_ShouldReturnBadRequest_WhenCustomerNotFound() throws Exception {
        // Arrange
        when(receiptService.getReceiptsByCustomerId(999L))
                .thenThrow(new IllegalArgumentException("Customer not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/receipts/customer/999"))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).getReceiptsByCustomerId(999L);
    }

    @Test
    void getUnpaidReceipts_ShouldReturnUnpaidReceipts() throws Exception {
        // Arrange
        List<ReceiptResponseDTO> unpaidReceipts = Arrays.asList(receiptResponseDTO);
        when(receiptService.getReceiptsByPaymentStatus(false)).thenReturn(unpaidReceipts);

        // Act & Assert
        mockMvc.perform(get("/receipts/unpaid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(false));

        verify(receiptService, times(1)).getReceiptsByPaymentStatus(false);
    }

    @Test
    void getPaidReceipts_ShouldReturnPaidReceipts() throws Exception {
        // Arrange
        ReceiptResponseDTO paidReceipt = new ReceiptResponseDTO();
        paidReceipt.setId(1L);
        paidReceipt.setCustomer(customerResponseDTO);
        paidReceipt.setTotalExclVat(new BigDecimal("100.00"));
        paidReceipt.setVat(new BigDecimal("21.00"));
        paidReceipt.setTotalInclVat(new BigDecimal("121.00"));
        paidReceipt.setIsPaid(true);
        paidReceipt.setCreatedDate(LocalDate.now());

        List<ReceiptResponseDTO> paidReceipts = Arrays.asList(paidReceipt);
        when(receiptService.getReceiptsByPaymentStatus(true)).thenReturn(paidReceipts);

        // Act & Assert
        mockMvc.perform(get("/receipts/paid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(true));

        verify(receiptService, times(1)).getReceiptsByPaymentStatus(true);
    }

    @Test
    void markReceiptAsPaid_ShouldReturnUpdatedReceipt_WhenReceiptExists() throws Exception {
        // Arrange
        ReceiptResponseDTO paidReceipt = new ReceiptResponseDTO();
        paidReceipt.setId(1L);
        paidReceipt.setCustomer(customerResponseDTO);
        paidReceipt.setTotalExclVat(receiptResponseDTO.getTotalExclVat());
        paidReceipt.setVat(receiptResponseDTO.getVat());
        paidReceipt.setTotalInclVat(receiptResponseDTO.getTotalInclVat());
        paidReceipt.setIsPaid(true);
        paidReceipt.setCreatedDate(receiptResponseDTO.getCreatedDate());

        when(receiptService.markAsPaid(1L)).thenReturn(paidReceipt);

        // Act & Assert
        mockMvc.perform(put("/receipts/1/pay"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(receiptService, times(1)).markAsPaid(1L);
    }

    @Test
    void markReceiptAsPaid_ShouldReturnBadRequest_WhenReceiptAlreadyPaid() throws Exception {
        // Arrange
        when(receiptService.markAsPaid(1L))
                .thenThrow(new IllegalStateException("Receipt is already marked as paid"));

        // Act & Assert
        mockMvc.perform(put("/receipts/1/pay"))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).markAsPaid(1L);
    }

    @Test
    void updatePaymentStatus_ShouldReturnUpdatedReceipt_WhenValidRequest() throws Exception {
        // Arrange
        ReceiptResponseDTO updatedReceipt = new ReceiptResponseDTO();
        updatedReceipt.setId(1L);
        updatedReceipt.setCustomer(customerResponseDTO);
        updatedReceipt.setTotalExclVat(receiptResponseDTO.getTotalExclVat());
        updatedReceipt.setVat(receiptResponseDTO.getVat());
        updatedReceipt.setTotalInclVat(receiptResponseDTO.getTotalInclVat());
        updatedReceipt.setIsPaid(true);
        updatedReceipt.setCreatedDate(receiptResponseDTO.getCreatedDate());

        when(receiptService.updatePaymentStatus(eq(1L), any(ReceiptPaymentDTO.class))).thenReturn(updatedReceipt);

        // Act & Assert
        mockMvc.perform(put("/receipts/1/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptPaymentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(receiptService, times(1)).updatePaymentStatus(eq(1L), any(ReceiptPaymentDTO.class));
    }

    @Test
    void updatePaymentStatus_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        ReceiptPaymentDTO invalidRequest = new ReceiptPaymentDTO();
        // Missing isPaid field - should trigger validation error

        // Act & Assert
        mockMvc.perform(put("/receipts/1/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(receiptService, never()).updatePaymentStatus(anyLong(), any(ReceiptPaymentDTO.class));
    }

    @Test
    void updatePaymentStatus_ShouldReturnBadRequest_WhenTryingToMarkPaidReceiptAsUnpaid() throws Exception {
        // Arrange
        ReceiptPaymentDTO unpaidRequest = new ReceiptPaymentDTO();
        unpaidRequest.setIsPaid(false);

        when(receiptService.updatePaymentStatus(eq(1L), any(ReceiptPaymentDTO.class)))
                .thenThrow(new IllegalStateException("Cannot mark a paid receipt as unpaid"));

        // Act & Assert
        mockMvc.perform(put("/receipts/1/payment-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unpaidRequest)))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).updatePaymentStatus(eq(1L), any(ReceiptPaymentDTO.class));
    }

    @Test
    void generateReceipt_ShouldReturnBadRequest_WhenInspectionAlreadyPaid() throws Exception {
        // Arrange
        when(receiptService.generateReceipt(any(ReceiptGenerateRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Inspection 1 is already paid"));

        // Act & Assert
        mockMvc.perform(post("/receipts/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptGenerateRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).generateReceipt(any(ReceiptGenerateRequestDTO.class));
    }

    @Test
    void generateReceipt_ShouldReturnBadRequest_WhenRepairDoesNotBelongToCustomer() throws Exception {
        // Arrange
        when(receiptService.generateReceipt(any(ReceiptGenerateRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Repair 1 does not belong to customer 1"));

        // Act & Assert
        mockMvc.perform(post("/receipts/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptGenerateRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(receiptService, times(1)).generateReceipt(any(ReceiptGenerateRequestDTO.class));
    }
}