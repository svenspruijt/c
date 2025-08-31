package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.action.ActionRequestDTO;
import nl.novi.garage.dtos.action.ActionResponseDTO;
import nl.novi.garage.services.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actions")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class ActionController {

    private final ActionService actionService;

    @Autowired
    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<ActionResponseDTO> createAction(@Valid @RequestBody ActionRequestDTO actionRequestDTO) {
        ActionResponseDTO response = actionService.createAction(actionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ActionResponseDTO>> getAllActions() {
        List<ActionResponseDTO> actions = actionService.getAllActions();
        return ResponseEntity.ok(actions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponseDTO> getActionById(@PathVariable Long id) {
        ActionResponseDTO action = actionService.getActionById(id);
        return ResponseEntity.ok(action);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<ActionResponseDTO> updateAction(
            @PathVariable Long id,
            @Valid @RequestBody ActionRequestDTO actionRequestDTO) {
        ActionResponseDTO response = actionService.updateAction(id, actionRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        actionService.deleteAction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActionResponseDTO>> searchActions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {

        if (name != null && !name.trim().isEmpty()) {
            List<ActionResponseDTO> actions = actionService.searchActionsByName(name);
            return ResponseEntity.ok(actions);
        }

        if (description != null && !description.trim().isEmpty()) {
            List<ActionResponseDTO> actions = actionService.searchActionsByDescription(description);
            return ResponseEntity.ok(actions);
        }

        // If no search parameters provided, return all actions
        List<ActionResponseDTO> actions = actionService.getAllActions();
        return ResponseEntity.ok(actions);
    }
}