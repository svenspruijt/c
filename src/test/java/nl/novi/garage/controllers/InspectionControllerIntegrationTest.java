package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.inspection.InspectionRequestDTO;
import nl.novi.garage.dtos.inspection.InspectionResponseDTO;
import nl.novi.garage.services.InspectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InspectionController.class, excludeFilters = {
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
class InspectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InspectionService inspectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private InspectionRequestDTO inspectionRequestDTO;
    private InspectionResponseDTO inspectionResponseDTO;

    @BeforeEach
    void setUp() {
        inspectionRequestDTO = new InspectionRequestDTO();
        inspectionRequestDTO.setCarId(1L);
        inspectionRequestDTO.setDate(LocalDate.of(2024, 1, 15));
        inspectionRequestDTO.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        inspectionRequestDTO.setStatus("COMPLETED");
        inspectionRequestDTO.setIsPaid(true);

        inspectionResponseDTO = new InspectionResponseDTO();
        inspectionResponseDTO.setId(1L);
        inspectionResponseDTO.setCarId(1L);
        inspectionResponseDTO.setCarBrand("Toyota");
        inspectionResponseDTO.setCarModel("Corolla");
        inspectionResponseDTO.setCarLicensePlate("AB-123-CD");
        inspectionResponseDTO.setDate(LocalDate.of(2024, 1, 15));
        inspectionResponseDTO.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        inspectionResponseDTO.setStatus("COMPLETED");
        inspectionResponseDTO.setIsPaid(true);
    }

    @Test
    void createInspection_ShouldReturnCreatedInspection_WhenValidRequest() throws Exception {
        // Arrange
        when(inspectionService.createInspection(any(InspectionRequestDTO.class))).thenReturn(inspectionResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/inspections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inspectionRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.carBrand").value("Toyota"))
                .andExpect(jsonPath("$.carModel").value("Corolla"))
                .andExpect(jsonPath("$.carLicensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.date").value("2024-01-15"))
                .andExpect(jsonPath("$.report").value("APK keuring uitgevoerd. Auto voldoet aan alle eisen."))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(inspectionService, times(1)).createInspection(any(InspectionRequestDTO.class));
    }

    @Test
    void createInspection_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        InspectionRequestDTO invalidRequest = new InspectionRequestDTO();
        invalidRequest.setCarId(null); // Invalid: null car ID
        invalidRequest.setDate(null); // Invalid: null date
        invalidRequest.setReport(""); // Invalid: blank report
        invalidRequest.setStatus(""); // Invalid: blank status
        invalidRequest.setIsPaid(null); // Invalid: null payment status

        // Act & Assert
        mockMvc.perform(post("/inspections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(inspectionService, never()).createInspection(any(InspectionRequestDTO.class));
    }

    @Test
    void getAllInspections_ShouldReturnInspectionList() throws Exception {
        // Arrange
        InspectionResponseDTO inspection2 = new InspectionResponseDTO();
        inspection2.setId(2L);
        inspection2.setCarId(2L);
        inspection2.setCarBrand("Honda");
        inspection2.setCarModel("Civic");
        inspection2.setCarLicensePlate("XY-456-ZW");
        inspection2.setDate(LocalDate.of(2024, 2, 15));
        inspection2.setReport("Tweede keuring uitgevoerd.");
        inspection2.setStatus("IN_PROGRESS");
        inspection2.setIsPaid(false);

        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO, inspection2);
        when(inspectionService.getAllInspections()).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));

        verify(inspectionService, times(1)).getAllInspections();
    }

    @Test
    void getInspectionById_ShouldReturnInspection_WhenInspectionExists() throws Exception {
        // Arrange
        when(inspectionService.getInspectionById(1L)).thenReturn(inspectionResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/inspections/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.report").value("APK keuring uitgevoerd. Auto voldoet aan alle eisen."))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(inspectionService, times(1)).getInspectionById(1L);
    }

    @Test
    void getInspectionsByCarId_ShouldReturnInspections_WhenCarExists() throws Exception {
        // Arrange
        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO);
        when(inspectionService.getInspectionsByCarId(1L)).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/car/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].carId").value(1));

        verify(inspectionService, times(1)).getInspectionsByCarId(1L);
    }

    @Test
    void updateInspection_ShouldReturnUpdatedInspection_WhenValidRequest() throws Exception {
        // Arrange
        InspectionRequestDTO updateRequest = new InspectionRequestDTO();
        updateRequest.setCarId(1L);
        updateRequest.setDate(LocalDate.of(2024, 2, 15));
        updateRequest.setReport("Updated inspection report");
        updateRequest.setStatus("IN_PROGRESS");
        updateRequest.setIsPaid(false);

        InspectionResponseDTO updatedResponse = new InspectionResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setCarId(1L);
        updatedResponse.setCarBrand("Toyota");
        updatedResponse.setCarModel("Corolla");
        updatedResponse.setCarLicensePlate("AB-123-CD");
        updatedResponse.setDate(LocalDate.of(2024, 2, 15));
        updatedResponse.setReport("Updated inspection report");
        updatedResponse.setStatus("IN_PROGRESS");
        updatedResponse.setIsPaid(false);

        when(inspectionService.updateInspection(eq(1L), any(InspectionRequestDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/inspections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.report").value("Updated inspection report"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(inspectionService, times(1)).updateInspection(eq(1L), any(InspectionRequestDTO.class));
    }

    @Test
    void completeInspection_ShouldReturnCompletedInspection_WhenValidRequest() throws Exception {
        // Arrange
        InspectionResponseDTO completedResponse = new InspectionResponseDTO();
        completedResponse.setId(1L);
        completedResponse.setCarId(1L);
        completedResponse.setCarBrand("Toyota");
        completedResponse.setCarModel("Corolla");
        completedResponse.setCarLicensePlate("AB-123-CD");
        completedResponse.setDate(LocalDate.of(2024, 1, 15));
        completedResponse.setReport("Final inspection report");
        completedResponse.setStatus("COMPLETED");
        completedResponse.setIsPaid(false);

        when(inspectionService.completeInspection(eq(1L), anyString())).thenReturn(completedResponse);

        // Act & Assert
        mockMvc.perform(put("/inspections/1/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"Final inspection report\""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.report").value("Final inspection report"));

        verify(inspectionService, times(1)).completeInspection(eq(1L), anyString());
    }

    @Test
    void markAsPaid_ShouldReturnPaidInspection_WhenValidRequest() throws Exception {
        // Arrange
        InspectionResponseDTO paidResponse = new InspectionResponseDTO();
        paidResponse.setId(1L);
        paidResponse.setCarId(1L);
        paidResponse.setCarBrand("Toyota");
        paidResponse.setCarModel("Corolla");
        paidResponse.setCarLicensePlate("AB-123-CD");
        paidResponse.setDate(LocalDate.of(2024, 1, 15));
        paidResponse.setReport("APK keuring uitgevoerd. Auto voldoet aan alle eisen.");
        paidResponse.setStatus("COMPLETED");
        paidResponse.setIsPaid(true);

        when(inspectionService.markAsPaid(1L)).thenReturn(paidResponse);

        // Act & Assert
        mockMvc.perform(put("/inspections/1/pay"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isPaid").value(true));

        verify(inspectionService, times(1)).markAsPaid(1L);
    }

    @Test
    void deleteInspection_ShouldReturnNoContent_WhenInspectionExists() throws Exception {
        // Arrange
        doNothing().when(inspectionService).deleteInspection(1L);

        // Act & Assert
        mockMvc.perform(delete("/inspections/1"))
                .andExpect(status().isNoContent());

        verify(inspectionService, times(1)).deleteInspection(1L);
    }

    @Test
    void searchInspections_ShouldSearchByStatus_WhenStatusParameterProvided() throws Exception {
        // Arrange
        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO);
        when(inspectionService.getInspectionsByStatus("COMPLETED")).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/search")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(inspectionService, times(1)).getInspectionsByStatus("COMPLETED");
    }

    @Test
    void searchInspections_ShouldSearchByPaymentStatus_WhenIsPaidParameterProvided() throws Exception {
        // Arrange
        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO);
        when(inspectionService.getInspectionsByPaymentStatus(true)).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/search")
                .param("isPaid", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(true));

        verify(inspectionService, times(1)).getInspectionsByPaymentStatus(true);
    }

    @Test
    void searchInspections_ShouldSearchByDateRange_WhenDateParametersProvided() throws Exception {
        // Arrange
        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO);
        when(inspectionService.getInspectionsByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/search")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(inspectionService, times(1)).getInspectionsByDateRange(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getUnpaidInspections_ShouldReturnUnpaidInspections() throws Exception {
        // Arrange
        InspectionResponseDTO unpaidInspection = new InspectionResponseDTO();
        unpaidInspection.setId(2L);
        unpaidInspection.setCarId(1L);
        unpaidInspection.setCarBrand("Toyota");
        unpaidInspection.setCarModel("Corolla");
        unpaidInspection.setCarLicensePlate("AB-123-CD");
        unpaidInspection.setDate(LocalDate.of(2024, 2, 15));
        unpaidInspection.setReport("Unpaid inspection");
        unpaidInspection.setStatus("COMPLETED");
        unpaidInspection.setIsPaid(false);

        List<InspectionResponseDTO> inspections = Arrays.asList(unpaidInspection);
        when(inspectionService.getInspectionsByPaymentStatus(false)).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/unpaid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].isPaid").value(false));

        verify(inspectionService, times(1)).getInspectionsByPaymentStatus(false);
    }

    @Test
    void getInspectionsByStatus_ShouldReturnInspectionsWithSpecificStatus() throws Exception {
        // Arrange
        List<InspectionResponseDTO> inspections = Arrays.asList(inspectionResponseDTO);
        when(inspectionService.getInspectionsByStatus("COMPLETED")).thenReturn(inspections);

        // Act & Assert
        mockMvc.perform(get("/inspections/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));

        verify(inspectionService, times(1)).getInspectionsByStatus("COMPLETED");
    }

    @Test
    void createInspection_ShouldReturnBadRequest_WhenCarNotFound() throws Exception {
        // Arrange
        when(inspectionService.createInspection(any(InspectionRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Car not found with id: 1"));

        // Act & Assert
        mockMvc.perform(post("/inspections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inspectionRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(inspectionService, times(1)).createInspection(any(InspectionRequestDTO.class));
    }
}