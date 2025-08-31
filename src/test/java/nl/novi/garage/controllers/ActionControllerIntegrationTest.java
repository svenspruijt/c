package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.action.ActionRequestDTO;
import nl.novi.garage.dtos.action.ActionResponseDTO;
import nl.novi.garage.services.ActionService;
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

@WebMvcTest(controllers = ActionController.class, excludeFilters = {
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
class ActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionService actionService;

    @Autowired
    private ObjectMapper objectMapper;

    private ActionRequestDTO actionRequestDTO;
    private ActionResponseDTO actionResponseDTO;

    @BeforeEach
    void setUp() {
        actionRequestDTO = new ActionRequestDTO();
        actionRequestDTO.setName("APK Keuring");
        actionRequestDTO.setDescription("Algemene Periodieke Keuring conform RDW eisen");
        actionRequestDTO.setPrice(new BigDecimal("50.00"));

        actionResponseDTO = new ActionResponseDTO();
        actionResponseDTO.setId(1L);
        actionResponseDTO.setName("APK Keuring");
        actionResponseDTO.setDescription("Algemene Periodieke Keuring conform RDW eisen");
        actionResponseDTO.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void createAction_ShouldReturnCreatedAction_WhenValidRequest() throws Exception {
        // Arrange
        when(actionService.createAction(any(ActionRequestDTO.class))).thenReturn(actionResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actionRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("APK Keuring"))
                .andExpect(jsonPath("$.description").value("Algemene Periodieke Keuring conform RDW eisen"))
                .andExpect(jsonPath("$.price").value(50.00));

        verify(actionService, times(1)).createAction(any(ActionRequestDTO.class));
    }

    @Test
    void createAction_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        ActionRequestDTO invalidRequest = new ActionRequestDTO();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setDescription("abc"); // Invalid: too short description
        invalidRequest.setPrice(new BigDecimal("-1.00")); // Invalid: negative price

        // Act & Assert
        mockMvc.perform(post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(actionService, never()).createAction(any(ActionRequestDTO.class));
    }

    @Test
    void getAllActions_ShouldReturnActionList() throws Exception {
        // Arrange
        ActionResponseDTO action2 = new ActionResponseDTO();
        action2.setId(2L);
        action2.setName("Kleine beurt");
        action2.setDescription("Olie verversen, filters controleren, vloeistoffen bijvullen");
        action2.setPrice(new BigDecimal("125.00"));

        List<ActionResponseDTO> actions = Arrays.asList(actionResponseDTO, action2);
        when(actionService.getAllActions()).thenReturn(actions);

        // Act & Assert
        mockMvc.perform(get("/actions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("APK Keuring"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Kleine beurt"));

        verify(actionService, times(1)).getAllActions();
    }

    @Test
    void getActionById_ShouldReturnAction_WhenActionExists() throws Exception {
        // Arrange
        when(actionService.getActionById(1L)).thenReturn(actionResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/actions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("APK Keuring"))
                .andExpect(jsonPath("$.price").value(50.00));

        verify(actionService, times(1)).getActionById(1L);
    }

    @Test
    void updateAction_ShouldReturnUpdatedAction_WhenValidRequest() throws Exception {
        // Arrange
        ActionRequestDTO updateRequest = new ActionRequestDTO();
        updateRequest.setName("Updated action");
        updateRequest.setDescription("Updated description of the action");
        updateRequest.setPrice(new BigDecimal("75.00"));

        ActionResponseDTO updatedResponse = new ActionResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated action");
        updatedResponse.setDescription("Updated description of the action");
        updatedResponse.setPrice(new BigDecimal("75.00"));

        when(actionService.updateAction(eq(1L), any(ActionRequestDTO.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/actions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated action"))
                .andExpect(jsonPath("$.description").value("Updated description of the action"))
                .andExpect(jsonPath("$.price").value(75.00));

        verify(actionService, times(1)).updateAction(eq(1L), any(ActionRequestDTO.class));
    }

    @Test
    void deleteAction_ShouldReturnNoContent_WhenActionExists() throws Exception {
        // Arrange
        doNothing().when(actionService).deleteAction(1L);

        // Act & Assert
        mockMvc.perform(delete("/actions/1"))
                .andExpect(status().isNoContent());

        verify(actionService, times(1)).deleteAction(1L);
    }

    @Test
    void searchActions_ShouldSearchByName_WhenNameParameterProvided() throws Exception {
        // Arrange
        List<ActionResponseDTO> actions = Arrays.asList(actionResponseDTO);
        when(actionService.searchActionsByName("APK")).thenReturn(actions);

        // Act & Assert
        mockMvc.perform(get("/actions/search")
                .param("name", "APK"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("APK Keuring"));

        verify(actionService, times(1)).searchActionsByName("APK");
    }

    @Test
    void searchActions_ShouldSearchByDescription_WhenDescriptionParameterProvided() throws Exception {
        // Arrange
        List<ActionResponseDTO> actions = Arrays.asList(actionResponseDTO);
        when(actionService.searchActionsByDescription("Keuring")).thenReturn(actions);

        // Act & Assert
        mockMvc.perform(get("/actions/search")
                .param("description", "Keuring"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Algemene Periodieke Keuring conform RDW eisen"));

        verify(actionService, times(1)).searchActionsByDescription("Keuring");
    }

    @Test
    void searchActions_ShouldReturnAllActions_WhenNoParameters() throws Exception {
        // Arrange
        List<ActionResponseDTO> actions = Arrays.asList(actionResponseDTO);
        when(actionService.getAllActions()).thenReturn(actions);

        // Act & Assert
        mockMvc.perform(get("/actions/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(actionService, times(1)).getAllActions();
    }

    @Test
    void createAction_ShouldReturnBadRequest_WhenActionNameAlreadyExists() throws Exception {
        // Arrange
        when(actionService.createAction(any(ActionRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Action with name 'APK Keuring' already exists"));

        // Act & Assert
        mockMvc.perform(post("/actions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actionRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(actionService, times(1)).createAction(any(ActionRequestDTO.class));
    }

    @Test
    void getActionById_ShouldReturnBadRequest_WhenActionNotFound() throws Exception {
        // Arrange
        when(actionService.getActionById(999L))
                .thenThrow(new IllegalArgumentException("Action not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/actions/999"))
                .andExpect(status().isBadRequest());

        verify(actionService, times(1)).getActionById(999L);
    }
}