package nl.novi.garage.services;

import nl.novi.garage.dtos.document.CarDocumentResponseDTO;
import nl.novi.garage.dtos.document.CarDocumentUploadDTO;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.CarDocument;
import nl.novi.garage.repositories.CarDocumentRepository;
import nl.novi.garage.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentService {

    private final CarDocumentRepository carDocumentRepository;
    private final CarRepository carRepository;

    private static final String[] ALLOWED_CONTENT_TYPES = {
            "application/pdf"
    };

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Autowired
    public DocumentService(CarDocumentRepository carDocumentRepository, CarRepository carRepository) {
        this.carDocumentRepository = carDocumentRepository;
        this.carRepository = carRepository;
    }

    public CarDocumentResponseDTO uploadDocument(Long carId, MultipartFile file) throws IOException {
        // Validate car exists
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));

        // Validate file
        validateFile(file);

        // Check if document with same filename already exists for this car
        if (carDocumentRepository.existsByCarIdAndFilename(carId, file.getOriginalFilename())) {
            throw new IllegalArgumentException(
                    "Document with filename '" + file.getOriginalFilename() + "' already exists for this car");
        }

        // Create document entity
        CarDocument document = new CarDocument();
        document.setCar(car);
        document.setFilename(file.getOriginalFilename());
        document.setFilepath("car_" + carId + "/" + file.getOriginalFilename());
        document.setData(file.getBytes());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());

        // Save document
        CarDocument savedDocument = carDocumentRepository.save(document);

        return mapToResponseDTO(savedDocument);
    }

    public CarDocumentResponseDTO uploadDocument(CarDocumentUploadDTO uploadDTO) {
        // Validate car exists
        Car car = carRepository.findById(uploadDTO.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + uploadDTO.getCarId()));

        // Validate content type
        if (!isValidContentType(uploadDTO.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PDF files are allowed.");
        }

        // Validate file size
        if (uploadDTO.getData().length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        // Check if document with same filename already exists for this car
        if (carDocumentRepository.existsByCarIdAndFilename(uploadDTO.getCarId(), uploadDTO.getFilename())) {
            throw new IllegalArgumentException(
                    "Document with filename '" + uploadDTO.getFilename() + "' already exists for this car");
        }

        // Create document entity
        CarDocument document = new CarDocument();
        document.setCar(car);
        document.setFilename(uploadDTO.getFilename());
        document.setFilepath("car_" + uploadDTO.getCarId() + "/" + uploadDTO.getFilename());
        document.setData(uploadDTO.getData());
        document.setContentType(uploadDTO.getContentType());
        document.setFileSize((long) uploadDTO.getData().length);

        // Save document
        CarDocument savedDocument = carDocumentRepository.save(document);

        return mapToResponseDTO(savedDocument);
    }

    @Transactional(readOnly = true)
    public List<CarDocumentResponseDTO> getDocumentsByCarId(Long carId) {
        // Validate car exists
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));

        List<CarDocument> documents = carDocumentRepository.findByCarId(carId);
        return documents.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarDocument getDocumentById(Long documentId) {
        return carDocumentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));
    }

    @Transactional(readOnly = true)
    public CarDocumentResponseDTO getDocumentResponseById(Long documentId) {
        CarDocument document = getDocumentById(documentId);
        return mapToResponseDTO(document);
    }

    public void deleteDocument(Long documentId) {
        CarDocument document = carDocumentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        carDocumentRepository.delete(document);
    }

    public void deleteDocumentsByCarId(Long carId) {
        // Validate car exists
        carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + carId));

        carDocumentRepository.deleteByCarId(carId);
    }

    @Transactional(readOnly = true)
    public List<CarDocumentResponseDTO> getAllDocuments() {
        List<CarDocument> documents = carDocumentRepository.findAll();
        return documents.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }

        if (!isValidContentType(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PDF files are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
    }

    private boolean isValidContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }

    private CarDocumentResponseDTO mapToResponseDTO(CarDocument document) {
        return new CarDocumentResponseDTO(
                document.getId(),
                document.getCar().getId(),
                document.getCar().getLicensePlate(),
                document.getFilename(),
                document.getContentType(),
                document.getFileSize());
    }
}