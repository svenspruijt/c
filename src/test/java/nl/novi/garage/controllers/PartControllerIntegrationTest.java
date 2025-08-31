package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.part.PartRequestDTO;
import nl.novi.garage.dtos.part.PartResponseDTO;
import nl.novi.garage.dtos.part.PartStockUpdateDTO;
import nl.novi.garage.services.PartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PartController.class, excludeFilters = {
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
class PartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartService partService;

    @Autowired
    private ObjectMapper objectMapper;

    private PartRequestDTO partRequestDTO;
    private PartResponseDTO partResponseDTO;
    private PartStockUpdateDTO stockUpdateDTO;

    @BeforeEach
    void setUp() {
        partRequestDTO = new PartRequestDTO();
        partRequestDTO.setName("Remblokken set");
        partRequestDTO.setPrice(new BigDecimal("45.99"));
        partRequestDTO.setStock(25);

        partResponseDTO = new PartResponseDTO();
        partResponseDTO.setId(1L);
        partResponseDTO.setName("Remblokken set");
        partResponseDTO.setPrice(new BigDecimal("45.99"));
        partResponseDTO.setStock(25);

        stockUpdateDTO = new PartStockUpdateDTO();
        stockUpdateDTO.setStock(30);
    }

    @Test
    void createPart_ShouldReturnCreatedPart_WhenValidRequest() throws Exception {
        // Arrange
        when(partService.createPart(any(PartRequestDTO.class))).thenReturn(partResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Remblokken set"))
                .andExpect(jsonPath("$.price").value(45.99))
                .andExpect(jsonPath("$.stock").value(25));

        verify(partService, times(1)).createPart(any(PartRequestDTO.class));
    }

    @Test
    void createPart_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        PartRequestDTO invalidRequest = new PartRequestDTO();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setPrice(new BigDecimal("-1.00")); // Invalid: negative price
        invalidRequest.setStock(-5); // Invalid: negative stock

        // Act & Assert
        mockMvc.perform(post("/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(partService, never()).createPart(any(PartRequestDTO.class));
    }

    @Test
    void getAllParts_ShouldReturnPartList() throws Exception {
        // Arrange
        PartResponseDTO part2 = new PartResponseDTO();
        part2.setId(2L);
        part2.setName("Motorolie 5W-30");
        part2.setPrice(new BigDecimal("35.50"));
        part2.setStock(40);

        List<PartResponseDTO> parts = Arrays.asList(partResponseDTO, part2);
        when(partService.getAllParts()).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Remblokken set"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Motorolie 5W-30"));

        verify(partService, times(1)).getAllParts();
    }

    @Test
    void getPartById_ShouldReturnPart_WhenPartExists() throws Exception {
        // Arrange
        when(partService.getPartById(1L)).thenReturn(partResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/parts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Remblokken set"))
                .andExpect(jsonPath("$.price").value(45.99));

        verify(partService, times(1)).getPartById(1L);
    }

    @Test
    void updatePart_ShouldReturnUpdatedPart_WhenValidRequest() throws Exception {
        // Arrange
        PartRequestDTO updateRequest = new PartRequestDTO();
        updateRequest.setName("Updated part");
        updateRequest.setPrice(new BigDecimal("50.00"));
        updateRequest.setStock(20);

        PartResponseDTO updatedResponse = new PartResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated part");
        updatedResponse.setPrice(new BigDecimal("50.00"));
        updatedResponse.setStock(20);

        when(partService.updatePart(eq(1L), any(PartRequestDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/parts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated part"))
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.stock").value(20));

        verify(partService, times(1)).updatePart(eq(1L), any(PartRequestDTO.class));
    }

    @Test
    void updatePartStock_ShouldReturnUpdatedPart_WhenValidRequest() throws Exception {
        // Arrange
        PartResponseDTO updatedResponse = new PartResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Remblokken set");
        updatedResponse.setPrice(new BigDecimal("45.99"));
        updatedResponse.setStock(30);

        when(partService.updatePartStock(eq(1L), any(PartStockUpdateDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/parts/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(30));

        verify(partService, times(1)).updatePartStock(eq(1L), any(PartStockUpdateDTO.class));
    }

    @Test
    void deletePart_ShouldReturnNoContent_WhenPartExists() throws Exception {
        // Arrange
        doNothing().when(partService).deletePart(1L);

        // Act & Assert
        mockMvc.perform(delete("/parts/1"))
                .andExpect(status().isNoContent());

        verify(partService, times(1)).deletePart(1L);
    }

    @Test
    void searchParts_ShouldSearchByName_WhenNameParameterProvided() throws Exception {
        // Arrange
        List<PartResponseDTO> parts = Arrays.asList(partResponseDTO);
        when(partService.searchPartsByName("rem")).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts/search")
                .param("name", "rem"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Remblokken set"));

        verify(partService, times(1)).searchPartsByName("rem");
    }

    @Test
    void searchParts_ShouldSearchByLowStock_WhenLowStockParameterProvided() throws Exception {
        // Arrange
        List<PartResponseDTO> parts = Arrays.asList(partResponseDTO);
        when(partService.getPartsWithLowStock(10)).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts/search")
                .param("lowStock", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(partService, times(1)).getPartsWithLowStock(10);
    }

    @Test
    void searchParts_ShouldReturnAllParts_WhenNoParameters() throws Exception {
        // Arrange
        List<PartResponseDTO> parts = Arrays.asList(partResponseDTO);
        when(partService.getAllParts()).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(partService, times(1)).getAllParts();
    }

    @Test
    void getPartsInStock_ShouldReturnPartsWithStock() throws Exception {
        // Arrange
        List<PartResponseDTO> parts = Arrays.asList(partResponseDTO);
        when(partService.getPartsInStock()).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts/in-stock"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(partService, times(1)).getPartsInStock();
    }

    @Test
    void getOutOfStockParts_ShouldReturnOutOfStockParts() throws Exception {
        // Arrange
        List<PartResponseDTO> parts = Arrays.asList();
        when(partService.getOutOfStockParts()).thenReturn(parts);

        // Act & Assert
        mockMvc.perform(get("/parts/out-of-stock"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(partService, times(1)).getOutOfStockParts();
    }

    @Test
    void createPart_ShouldReturnBadRequest_WhenPartNameAlreadyExists() throws Exception {
        // Arrange
        when(partService.createPart(any(PartRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Part with name 'Remblokken set' already exists"));

        // Act & Assert
        mockMvc.perform(post("/parts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(partService, times(1)).createPart(any(PartRequestDTO.class));
    }
}