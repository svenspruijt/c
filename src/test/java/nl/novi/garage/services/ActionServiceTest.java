package nl.novi.garage.services;

import nl.novi.garage.dtos.action.ActionRequestDTO;
import nl.novi.garage.dtos.action.ActionResponseDTO;
import nl.novi.garage.models.Action;
import nl.novi.garage.repositories.ActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private ActionService actionService;

    private Action testAction;
    private ActionRequestDTO testActionRequestDTO;

    @BeforeEach
    void setUp() {
        testAction = new Action();
        testAction.setId(1L);
        testAction.setName("APK Keuring");
        testAction.setDescription("Algemene Periodieke Keuring conform RDW eisen");
        testAction.setPrice(new BigDecimal("50.00"));

        testActionRequestDTO = new ActionRequestDTO();
        testActionRequestDTO.setName("APK Keuring");
        testActionRequestDTO.setDescription("Algemene Periodieke Keuring conform RDW eisen");
        testActionRequestDTO.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void createAction_ShouldReturnActionResponseDTO_WhenValidRequest() {
        // Arrange
        when(actionRepository.existsByNameIgnoreCase("APK Keuring")).thenReturn(false);
        when(actionRepository.save(any(Action.class))).thenReturn(testAction);

        // Act
        ActionResponseDTO result = actionService.createAction(testActionRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testAction.getId(), result.getId());
        assertEquals(testAction.getName(), result.getName());
        assertEquals(testAction.getDescription(), result.getDescription());
        assertEquals(testAction.getPrice(), result.getPrice());
        verify(actionRepository, times(1)).existsByNameIgnoreCase("APK Keuring");
        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    void createAction_ShouldThrowException_WhenActionNameAlreadyExists() {
        // Arrange
        when(actionRepository.existsByNameIgnoreCase("APK Keuring")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> actionService.createAction(testActionRequestDTO));

        assertEquals("Action with name 'APK Keuring' already exists", exception.getMessage());
        verify(actionRepository, times(1)).existsByNameIgnoreCase("APK Keuring");
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    void getAllActions_ShouldReturnListOfActions() {
        // Arrange
        Action action2 = new Action();
        action2.setId(2L);
        action2.setName("Kleine beurt");
        action2.setDescription("Olie verversen, filters controleren, vloeistoffen bijvullen");
        action2.setPrice(new BigDecimal("125.00"));

        List<Action> actions = Arrays.asList(testAction, action2);
        when(actionRepository.findAll()).thenReturn(actions);

        // Act
        List<ActionResponseDTO> result = actionService.getAllActions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testAction.getName(), result.get(0).getName());
        assertEquals(action2.getName(), result.get(1).getName());
        verify(actionRepository, times(1)).findAll();
    }

    @Test
    void getActionById_ShouldReturnAction_WhenActionExists() {
        // Arrange
        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));

        // Act
        ActionResponseDTO result = actionService.getActionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAction.getId(), result.getId());
        assertEquals(testAction.getName(), result.getName());
        assertEquals(testAction.getDescription(), result.getDescription());
        assertEquals(testAction.getPrice(), result.getPrice());
        verify(actionRepository, times(1)).findById(1L);
    }

    @Test
    void getActionById_ShouldThrowException_WhenActionNotFound() {
        // Arrange
        when(actionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> actionService.getActionById(999L));

        assertEquals("Action not found with id: 999", exception.getMessage());
        verify(actionRepository, times(1)).findById(999L);
    }

    @Test
    void getActionByName_ShouldReturnAction_WhenNameExists() {
        // Arrange
        when(actionRepository.findByNameIgnoreCase("apk keuring")).thenReturn(Optional.of(testAction));

        // Act
        ActionResponseDTO result = actionService.getActionByName("apk keuring");

        // Assert
        assertNotNull(result);
        assertEquals(testAction.getName(), result.getName());
        verify(actionRepository, times(1)).findByNameIgnoreCase("apk keuring");
    }

    @Test
    void getActionByName_ShouldThrowException_WhenNameNotFound() {
        // Arrange
        when(actionRepository.findByNameIgnoreCase("Nonexistent action")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> actionService.getActionByName("Nonexistent action"));

        assertEquals("Action not found with name: Nonexistent action", exception.getMessage());
        verify(actionRepository, times(1)).findByNameIgnoreCase("Nonexistent action");
    }

    @Test
    void updateAction_ShouldReturnUpdatedAction_WhenValidRequest() {
        // Arrange
        ActionRequestDTO updateDTO = new ActionRequestDTO();
        updateDTO.setName("Updated action");
        updateDTO.setDescription("Updated description");
        updateDTO.setPrice(new BigDecimal("75.00"));

        Action updatedAction = new Action();
        updatedAction.setId(1L);
        updatedAction.setName("Updated action");
        updatedAction.setDescription("Updated description");
        updatedAction.setPrice(new BigDecimal("75.00"));

        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));
        when(actionRepository.existsByNameIgnoreCase("Updated action")).thenReturn(false);
        when(actionRepository.save(any(Action.class))).thenReturn(updatedAction);

        // Act
        ActionResponseDTO result = actionService.updateAction(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAction.getName(), result.getName());
        assertEquals(updatedAction.getDescription(), result.getDescription());
        assertEquals(updatedAction.getPrice(), result.getPrice());
        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    void updateAction_ShouldThrowException_WhenNewNameAlreadyExists() {
        // Arrange
        ActionRequestDTO updateDTO = new ActionRequestDTO();
        updateDTO.setName("Existing action");
        updateDTO.setDescription("Updated description");
        updateDTO.setPrice(new BigDecimal("75.00"));

        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));
        when(actionRepository.existsByNameIgnoreCase("Existing action")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> actionService.updateAction(1L, updateDTO));

        assertEquals("Action with name 'Existing action' already exists", exception.getMessage());
        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    void updateAction_ShouldAllowSameNameUpdate() {
        // Arrange
        ActionRequestDTO updateDTO = new ActionRequestDTO();
        updateDTO.setName("APK Keuring"); // Same name
        updateDTO.setDescription("Updated description");
        updateDTO.setPrice(new BigDecimal("60.00")); // Different price

        Action updatedAction = new Action();
        updatedAction.setId(1L);
        updatedAction.setName("APK Keuring");
        updatedAction.setDescription("Updated description");
        updatedAction.setPrice(new BigDecimal("60.00"));

        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));
        when(actionRepository.save(any(Action.class))).thenReturn(updatedAction);

        // Act
        ActionResponseDTO result = actionService.updateAction(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAction.getDescription(), result.getDescription());
        assertEquals(updatedAction.getPrice(), result.getPrice());
        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).save(any(Action.class));
        verify(actionRepository, never()).existsByNameIgnoreCase(anyString());
    }

    @Test
    void deleteAction_ShouldDeleteSuccessfully_WhenActionExists() {
        // Arrange
        when(actionRepository.findById(1L)).thenReturn(Optional.of(testAction));

        // Act
        actionService.deleteAction(1L);

        // Assert
        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).delete(testAction);
    }

    @Test
    void deleteAction_ShouldThrowException_WhenActionNotFound() {
        // Arrange
        when(actionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> actionService.deleteAction(999L));

        assertEquals("Action not found with id: 999", exception.getMessage());
        verify(actionRepository, times(1)).findById(999L);
        verify(actionRepository, never()).delete(any(Action.class));
    }

    @Test
    void searchActionsByName_ShouldReturnMatchingActions() {
        // Arrange
        String searchName = "APK";
        List<Action> actions = Arrays.asList(testAction);
        when(actionRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(actions);

        // Act
        List<ActionResponseDTO> result = actionService.searchActionsByName(searchName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAction.getName(), result.get(0).getName());
        verify(actionRepository, times(1)).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void searchActionsByDescription_ShouldReturnMatchingActions() {
        // Arrange
        String searchDescription = "Keuring";
        List<Action> actions = Arrays.asList(testAction);
        when(actionRepository.findByDescriptionContainingIgnoreCase(searchDescription)).thenReturn(actions);

        // Act
        List<ActionResponseDTO> result = actionService.searchActionsByDescription(searchDescription);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAction.getDescription(), result.get(0).getDescription());
        verify(actionRepository, times(1)).findByDescriptionContainingIgnoreCase(searchDescription);
    }
}