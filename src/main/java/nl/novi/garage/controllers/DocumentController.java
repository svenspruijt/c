package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.document.CarDocumentResponseDTO;
import nl.novi.garage.dtos.document.CarDocumentUploadDTO;
import nl.novi.garage.models.CarDocument;
import nl.novi.garage.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@PreAuthorize("hasRole('MEDEWERKER') or hasRole('MONTEUR') or hasRole('BEHEER')")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/cars/{carId}/upload")
    public ResponseEntity<CarDocumentResponseDTO> uploadDocument(
            @PathVariable Long carId,
            @RequestParam("file") MultipartFile file) {
        try {
            CarDocumentResponseDTO response = documentService.uploadDocument(carId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
        }
    }

    @PostMapping("/cars/{carId}")
    public ResponseEntity<CarDocumentResponseDTO> uploadDocumentData(
            @PathVariable Long carId,
            @Valid @RequestBody CarDocumentUploadDTO uploadDTO) {
        // Set the car ID from path variable (override any ID in the DTO)
        uploadDTO.setCarId(carId);
        CarDocumentResponseDTO response = documentService.uploadDocument(uploadDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<List<CarDocumentResponseDTO>> getDocumentsByCarId(@PathVariable Long carId) {
        List<CarDocumentResponseDTO> documents = documentService.getDocumentsByCarId(carId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<CarDocumentResponseDTO> getDocumentById(@PathVariable Long documentId) {
        CarDocumentResponseDTO document = documentService.getDocumentResponseById(documentId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        CarDocument document = documentService.getDocumentById(documentId);

        ByteArrayResource resource = new ByteArrayResource(document.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .contentLength(document.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/cars/{carId}/download/{filename}")
    public ResponseEntity<Resource> downloadDocumentByFilename(
            @PathVariable Long carId,
            @PathVariable String filename) {
        List<CarDocumentResponseDTO> documents = documentService.getDocumentsByCarId(carId);
        CarDocumentResponseDTO foundDocument = documents.stream()
                .filter(doc -> doc.getFilename().equals(filename))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document not found with filename: " + filename + " for car ID: " + carId));

        CarDocument document = documentService.getDocumentById(foundDocument.getId());
        ByteArrayResource resource = new ByteArrayResource(document.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .contentLength(document.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cars/{carId}")
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<Void> deleteAllDocumentsByCarId(@PathVariable Long carId) {
        documentService.deleteDocumentsByCarId(carId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('BEHEER')")
    public ResponseEntity<List<CarDocumentResponseDTO>> getAllDocuments() {
        List<CarDocumentResponseDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
}