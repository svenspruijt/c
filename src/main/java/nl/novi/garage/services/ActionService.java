package nl.novi.garage.services;

import nl.novi.garage.dtos.action.ActionRequestDTO;
import nl.novi.garage.dtos.action.ActionResponseDTO;
import nl.novi.garage.models.Action;
import nl.novi.garage.repositories.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActionService {

    private final ActionRepository actionRepository;

    @Autowired
    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    public ActionResponseDTO createAction(ActionRequestDTO actionRequestDTO) {
        // Check if action with same name already exists
        if (actionRepository.existsByNameIgnoreCase(actionRequestDTO.getName())) {
            throw new IllegalArgumentException(
                    "Action with name '" + actionRequestDTO.getName() + "' already exists");
        }

        // Create new action entity
        Action action = new Action();
        action.setName(actionRequestDTO.getName());
        action.setDescription(actionRequestDTO.getDescription());
        action.setPrice(actionRequestDTO.getPrice());

        // Save action
        Action savedAction = actionRepository.save(action);

        return mapToResponseDTO(savedAction);
    }

    @Transactional(readOnly = true)
    public List<ActionResponseDTO> getAllActions() {
        List<Action> actions = actionRepository.findAll();
        return actions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActionResponseDTO getActionById(Long id) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found with id: " + id));
        return mapToResponseDTO(action);
    }

    @Transactional(readOnly = true)
    public ActionResponseDTO getActionByName(String name) {
        Action action = actionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("Action not found with name: " + name));
        return mapToResponseDTO(action);
    }

    public ActionResponseDTO updateAction(Long id, ActionRequestDTO actionRequestDTO) {
        Action existingAction = actionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found with id: " + id));

        // Check if name already exists for another action
        if (!existingAction.getName().equalsIgnoreCase(actionRequestDTO.getName()) &&
                actionRepository.existsByNameIgnoreCase(actionRequestDTO.getName())) {
            throw new IllegalArgumentException("Action with name '" + actionRequestDTO.getName() + "' already exists");
        }

        // Update action fields
        existingAction.setName(actionRequestDTO.getName());
        existingAction.setDescription(actionRequestDTO.getDescription());
        existingAction.setPrice(actionRequestDTO.getPrice());

        Action updatedAction = actionRepository.save(existingAction);
        return mapToResponseDTO(updatedAction);
    }

    public void deleteAction(Long id) {
        Action action = actionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action not found with id: " + id));

        // TODO: In future, check if action is used in any repairs
        // For now, we can safely delete
        actionRepository.delete(action);
    }

    @Transactional(readOnly = true)
    public List<ActionResponseDTO> searchActionsByName(String name) {
        List<Action> actions = actionRepository.findByNameContainingIgnoreCase(name);
        return actions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActionResponseDTO> searchActionsByDescription(String description) {
        List<Action> actions = actionRepository.findByDescriptionContainingIgnoreCase(description);
        return actions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method for DTO mapping
    private ActionResponseDTO mapToResponseDTO(Action action) {
        return new ActionResponseDTO(
                action.getId(),
                action.getName(),
                action.getDescription(),
                action.getPrice());
    }
}