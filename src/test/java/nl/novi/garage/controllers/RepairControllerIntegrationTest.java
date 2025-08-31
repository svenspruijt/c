package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.repair.*;
import nl.novi.garage.services.RepairService;
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

@WebMvcTest(controllers = RepairController.class, excludeFilters = {
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
class RepairControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairService repairService;

    @Autowired
    private ObjectMapper objectMapper;

    private RepairRequestDTO repairRequestDTO;
    private RepairResponseDTO repairResponseDTO;
    private AddActionToRepairDTO addActionToRepairDTO;
    private AddPartToRepairDTO addPartToRepairDTO;
    private AddCustomActionToRepairDTO addCustomActionToRepairDTO;
    private RepairActionItemDTO repairActionItemDTO;
    private RepairPartItemDTO repairPartItemDTO;
    private RepairCustomActionItemDTO repairCustomActionItemDTO;

    @BeforeEach
    void setUp() {
        repairRequestDTO = new RepairRequestDTO();
        repairRequestDTO.setCarId(1L);
        repairRequestDTO.setDate(LocalDate.of(2024, 2, 15));
        repairRequestDTO.setStatus("IN_PROGRESS");
        repairRequestDTO.setReport("Remmen vervangen na APK keuring");
        repairRequestDTO.setIsPaid(false);

        repairResponseDTO = new RepairResponseDTO();
        repairResponseDTO.setId(1L);
        repairResponseDTO.setCarId(1L);
        repairResponseDTO.setCarBrand("Toyota");
        repairResponseDTO.setCarModel("Corolla");
        repairResponseDTO.setCarLicensePlate("AB-123-CD");
        repairResponseDTO.setDate(LocalDate.of(2024, 2, 15));
        repairResponseDTO.setStatus("IN_PROGRESS");
        repairResponseDTO.setReport("Remmen vervangen na APK keuring");
        repairResponseDTO.setIsPaid(false);
        repairResponseDTO.setActions(Arrays.asList());
        repairResponseDTO.setParts(Arrays.asList());
        repairResponseDTO.setCustomActions(Arrays.asList());

        addActionToRepairDTO = new AddActionToRepairDTO();
        addActionToRepairDTO.setActionId(1L);
        addActionToRepairDTO.setAmount(1);

        addPartToRepairDTO = new AddPartToRepairDTO();
        addPartToRepairDTO.setPartId(1L);
        addPartToRepairDTO.setAmount(2);

        addCustomActionToRepairDTO = new AddCustomActionToRepairDTO();
        addCustomActionToRepairDTO.setDescription("Extra controle na reparatie");
        addCustomActionToRepairDTO.setPrice(new BigDecimal("25.00"));

        repairActionItemDTO = new RepairActionItemDTO();
        repairActionItemDTO.setId(1L);
        repairActionItemDTO.setActionId(1L);
        repairActionItemDTO.setActionName("Remmen vervangen");
        repairActionItemDTO.setActionDescription("Vervangen van remblokken en/of remschijven inclusief montage");
        repairActionItemDTO.setActionPrice(new BigDecimal("195.00"));
        repairActionItemDTO.setAmount(1);

        repairPartItemDTO = new RepairPartItemDTO();
        repairPartItemDTO.setId(1L);
        repairPartItemDTO.setPartId(1L);
        repairPartItemDTO.setPartName("Remblokken set");
        repairPartItemDTO.setPartPrice(new BigDecimal("45.99"));
        repairPartItemDTO.setAmount(2);

        repairCustomActionItemDTO = new RepairCustomActionItemDTO();
        repairCustomActionItemDTO.setId(1L);
        repairCustomActionItemDTO.setDescription("Extra controle na reparatie");
        repairCustomActionItemDTO.setPrice(new BigDecimal("25.00"));
    }

    @Test
    void createRepair_ShouldReturnCreatedRepair_WhenValidRequest() throws Exception {
        // Arrange
        when(repairService.createRepair(any(RepairRequestDTO.class))).thenReturn(repairResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/repairs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repairRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.carBrand").value("Toyota"))
                .andExpect(jsonPath("$.carModel").value("Corolla"))
                .andExpect(jsonPath("$.carLicensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.date").value("2024-02-15"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.report").value("Remmen vervangen na APK keuring"))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(repairService, times(1)).createRepair(any(RepairRequestDTO.class));
    }

    @Test
    void createRepair_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        RepairRequestDTO invalidRequest = new RepairRequestDTO();
        invalidRequest.setCarId(null); // Invalid: null car ID
        invalidRequest.setDate(null); // Invalid: null date
        invalidRequest.setStatus(""); // Invalid: blank status
        invalidRequest.setReport(""); // Invalid: blank report
        invalidRequest.setIsPaid(null); // Invalid: null payment status

        // Act & Assert
        mockMvc.perform(post("/repairs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(repairService, never()).createRepair(any(RepairRequestDTO.class));
    }

    @Test
    void getAllRepairs_ShouldReturnRepairList() throws Exception {
        // Arrange
        RepairResponseDTO repair2 = new RepairResponseDTO();
        repair2.setId(2L);
        repair2.setCarId(2L);
        repair2.setCarBrand("Honda");
        repair2.setCarModel("Civic");
        repair2.setCarLicensePlate("XY-456-ZW");
        repair2.setDate(LocalDate.of(2024, 3, 15));
        repair2.setStatus("COMPLETED");
        repair2.setReport("Onderhoudsbeurt voltooid");
        repair2.setIsPaid(true);
        repair2.setActions(Arrays.asList());
        repair2.setParts(Arrays.asList());
        repair2.setCustomActions(Arrays.asList());

        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO, repair2);
        when(repairService.getAllRepairs()).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("COMPLETED"));

        verify(repairService, times(1)).getAllRepairs();
    }

    @Test
    void getRepairById_ShouldReturnRepair_WhenRepairExists() throws Exception {
        // Arrange
        when(repairService.getRepairById(1L)).thenReturn(repairResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/repairs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.report").value("Remmen vervangen na APK keuring"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(repairService, times(1)).getRepairById(1L);
    }

    @Test
    void getRepairsByCarId_ShouldReturnRepairs_WhenCarExists() throws Exception {
        // Arrange
        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO);
        when(repairService.getRepairsByCarId(1L)).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs/car/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].carId").value(1));

        verify(repairService, times(1)).getRepairsByCarId(1L);
    }

    @Test
    void updateRepair_ShouldReturnUpdatedRepair_WhenValidRequest() throws Exception {
        // Arrange
        RepairRequestDTO updateRequest = new RepairRequestDTO();
        updateRequest.setCarId(1L);
        updateRequest.setDate(LocalDate.of(2024, 3, 15));
        updateRequest.setStatus("COMPLETED");
        updateRequest.setReport("Updated repair report");
        updateRequest.setIsPaid(true);

        RepairResponseDTO updatedResponse = new RepairResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setCarId(1L);
        updatedResponse.setCarBrand("Toyota");
        updatedResponse.setCarModel("Corolla");
        updatedResponse.setCarLicensePlate("AB-123-CD");
        updatedResponse.setDate(LocalDate.of(2024, 3, 15));
        updatedResponse.setStatus("COMPLETED");
        updatedResponse.setReport("Updated repair report");
        updatedResponse.setIsPaid(true);
        updatedResponse.setActions(Arrays.asList());
        updatedResponse.setParts(Arrays.asList());
        updatedResponse.setCustomActions(Arrays.asList());

        when(repairService.updateRepair(eq(1L), any(RepairRequestDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/repairs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.report").value("Updated repair report"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(repairService, times(1)).updateRepair(eq(1L), any(RepairRequestDTO.class));
    }

    @Test
    void addActionToRepair_ShouldReturnActionItem_WhenValidRequest() throws Exception {
        // Arrange
        when(repairService.addActionToRepair(eq(1L), any(AddActionToRepairDTO.class))).thenReturn(repairActionItemDTO);

        // Act & Assert
        mockMvc.perform(post("/repairs/1/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addActionToRepairDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.actionId").value(1))
                .andExpect(jsonPath("$.actionName").value("Remmen vervangen"))
                .andExpect(jsonPath("$.actionPrice").value(195.00))
                .andExpect(jsonPath("$.amount").value(1));

        verify(repairService, times(1)).addActionToRepair(eq(1L), any(AddActionToRepairDTO.class));
    }

    @Test
    void addPartToRepair_ShouldReturnPartItem_WhenValidRequest() throws Exception {
        // Arrange
        when(repairService.addPartToRepair(eq(1L), any(AddPartToRepairDTO.class))).thenReturn(repairPartItemDTO);

        // Act & Assert
        mockMvc.perform(post("/repairs/1/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addPartToRepairDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.partId").value(1))
                .andExpect(jsonPath("$.partName").value("Remblokken set"))
                .andExpect(jsonPath("$.partPrice").value(45.99))
                .andExpect(jsonPath("$.amount").value(2));

        verify(repairService, times(1)).addPartToRepair(eq(1L), any(AddPartToRepairDTO.class));
    }

    @Test
    void addCustomActionToRepair_ShouldReturnCustomActionItem_WhenValidRequest() throws Exception {
        // Arrange
        when(repairService.addCustomActionToRepair(eq(1L), any(AddCustomActionToRepairDTO.class)))
                .thenReturn(repairCustomActionItemDTO);

        // Act & Assert
        mockMvc.perform(post("/repairs/1/custom-actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCustomActionToRepairDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Extra controle na reparatie"))
                .andExpect(jsonPath("$.price").value(25.00));

        verify(repairService, times(1)).addCustomActionToRepair(eq(1L), any(AddCustomActionToRepairDTO.class));
    }

    @Test
    void removeActionFromRepair_ShouldReturnNoContent_WhenValidRequest() throws Exception {
        // Arrange
        doNothing().when(repairService).removeActionFromRepair(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/repairs/1/actions/1"))
                .andExpect(status().isNoContent());

        verify(repairService, times(1)).removeActionFromRepair(1L, 1L);
    }

    @Test
    void removePartFromRepair_ShouldReturnNoContent_WhenValidRequest() throws Exception {
        // Arrange
        doNothing().when(repairService).removePartFromRepair(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/repairs/1/parts/1"))
                .andExpect(status().isNoContent());

        verify(repairService, times(1)).removePartFromRepair(1L, 1L);
    }

    @Test
    void removeCustomActionFromRepair_ShouldReturnNoContent_WhenValidRequest() throws Exception {
        // Arrange
        doNothing().when(repairService).removeCustomActionFromRepair(1L);

        // Act & Assert
        mockMvc.perform(delete("/repairs/custom-actions/1"))
                .andExpect(status().isNoContent());

        verify(repairService, times(1)).removeCustomActionFromRepair(1L);
    }

    @Test
    void markAsPaid_ShouldReturnPaidRepair_WhenValidRequest() throws Exception {
        // Arrange
        RepairResponseDTO paidResponse = new RepairResponseDTO();
        paidResponse.setId(1L);
        paidResponse.setCarId(1L);
        paidResponse.setCarBrand("Toyota");
        paidResponse.setCarModel("Corolla");
        paidResponse.setCarLicensePlate("AB-123-CD");
        paidResponse.setDate(LocalDate.of(2024, 2, 15));
        paidResponse.setStatus("IN_PROGRESS");
        paidResponse.setReport("Remmen vervangen na APK keuring");
        paidResponse.setIsPaid(true);
        paidResponse.setActions(Arrays.asList());
        paidResponse.setParts(Arrays.asList());
        paidResponse.setCustomActions(Arrays.asList());

        when(repairService.markAsPaid(1L)).thenReturn(paidResponse);

        // Act & Assert
        mockMvc.perform(put("/repairs/1/pay"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(repairService, times(1)).markAsPaid(1L);
    }

    @Test
    void deleteRepair_ShouldReturnNoContent_WhenRepairExists() throws Exception {
        // Arrange
        doNothing().when(repairService).deleteRepair(1L);

        // Act & Assert
        mockMvc.perform(delete("/repairs/1"))
                .andExpect(status().isNoContent());

        verify(repairService, times(1)).deleteRepair(1L);
    }

    @Test
    void searchRepairs_ShouldSearchByStatus_WhenStatusParameterProvided() throws Exception {
        // Arrange
        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO);
        when(repairService.getRepairsByStatus("IN_PROGRESS")).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs/search")
                .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));

        verify(repairService, times(1)).getRepairsByStatus("IN_PROGRESS");
    }

    @Test
    void searchRepairs_ShouldSearchByPaymentStatus_WhenIsPaidParameterProvided() throws Exception {
        // Arrange
        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO);
        when(repairService.getRepairsByPaymentStatus(false)).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs/search")
                .param("isPaid", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(false));

        verify(repairService, times(1)).getRepairsByPaymentStatus(false);
    }

    @Test
    void getUnpaidRepairs_ShouldReturnUnpaidRepairs() throws Exception {
        // Arrange
        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO);
        when(repairService.getRepairsByPaymentStatus(false)).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs/unpaid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(false));

        verify(repairService, times(1)).getRepairsByPaymentStatus(false);
    }

    @Test
    void getRepairsByStatus_ShouldReturnRepairsWithSpecificStatus() throws Exception {
        // Arrange
        List<RepairResponseDTO> repairs = Arrays.asList(repairResponseDTO);
        when(repairService.getRepairsByStatus("IN_PROGRESS")).thenReturn(repairs);

        // Act & Assert
        mockMvc.perform(get("/repairs/status/IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));

        verify(repairService, times(1)).getRepairsByStatus("IN_PROGRESS");
    }

    @Test
    void createRepair_ShouldReturnBadRequest_WhenCarNotFound() throws Exception {
        // Arrange
        when(repairService.createRepair(any(RepairRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Car not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/repairs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repairRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(repairService, times(1)).createRepair(any(RepairRequestDTO.class));
    }

    @Test
    void addPartToRepair_ShouldReturnBadRequest_WhenInsufficientStock() throws Exception {
        // Arrange
        when(repairService.addPartToRepair(eq(1L), any(AddPartToRepairDTO.class)))
                .thenThrow(new IllegalArgumentException("Insufficient stock. Available: 25, Required: 30"));

        // Act & Assert
        mockMvc.perform(post("/repairs/1/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addPartToRepairDTO)))
                .andExpect(status().isBadRequest());

        verify(repairService, times(1)).addPartToRepair(eq(1L), any(AddPartToRepairDTO.class));
    }
}