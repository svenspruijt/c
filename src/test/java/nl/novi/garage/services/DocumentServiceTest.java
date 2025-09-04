package nl.novi.garage.services;

import nl.novi.garage.dtos.document.CarDocumentResponseDTO;
import nl.novi.garage.dtos.document.CarDocumentUploadDTO;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.CarDocument;
import nl.novi.garage.models.Customer;
import nl.novi.garage.repositories.CarDocumentRepository;
import nl.novi.garage.repositories.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private CarDocumentRepository carDocumentRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private DocumentService documentService;

    private Car testCar;
    private Customer testCustomer;
    private CarDocument testDocument;
    private CarDocumentUploadDTO testUploadDTO;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Jan Jansen");
        testCustomer.setPhonenumber("+31612345678");

        testCar = new Car();
        testCar.setId(1L);
        testCar.setCustomer(testCustomer);
        testCar.setBrand("Toyota");
        testCar.setModel("Corolla");
        testCar.setLicensePlate("AB-123-CD");

        testDocument = new CarDocument();
        testDocument.setId(1L);
        testDocument.setCar(testCar);
        testDocument.setFilename("test-document.pdf");
        testDocument.setFilepath("car_1/test-document.pdf");
        testDocument.setData("test pdf content".getBytes());
        testDocument.setContentType("application/pdf");
        testDocument.setFileSize(16L);

        testUploadDTO = new CarDocumentUploadDTO();
        testUploadDTO.setCarId(1L);
        testUploadDTO.setFilename("test-document.pdf");
        testUploadDTO.setData("test pdf content".getBytes());
        testUploadDTO.setContentType("application/pdf");

        testFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test pdf content".getBytes());
    }

    @Test
    void uploadDocument_WithMultipartFile_ShouldReturnDocumentResponseDTO_WhenValidRequest() throws IOException {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carDocumentRepository.existsByCarIdAndFilename(1L, "test-document.pdf")).thenReturn(false);
        when(carDocumentRepository.save(any(CarDocument.class))).thenReturn(testDocument);

        // Act
        CarDocumentResponseDTO result = documentService.uploadDocument(1L, testFile);

        // Assert
        assertNotNull(result);
        assertEquals(testDocument.getId(), result.getId());
        assertEquals(testDocument.getCar().getId(), result.getCarId());
        assertEquals(testDocument.getFilename(), result.getFilename());
        assertEquals(testDocument.getContentType(), result.getContentType());
        assertEquals(testDocument.getFileSize(), result.getFileSize());
        assertEquals(testCar.getLicensePlate(), result.getCarLicensePlate());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).existsByCarIdAndFilename(1L, "test-document.pdf");
        verify(carDocumentRepository, times(1)).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_WithUploadDTO_ShouldReturnDocumentResponseDTO_WhenValidRequest() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carDocumentRepository.existsByCarIdAndFilename(1L, "test-document.pdf")).thenReturn(false);
        when(carDocumentRepository.save(any(CarDocument.class))).thenReturn(testDocument);

        // Act
        CarDocumentResponseDTO result = documentService.uploadDocument(testUploadDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testDocument.getId(), result.getId());
        assertEquals(testDocument.getCar().getId(), result.getCarId());
        assertEquals(testDocument.getFilename(), result.getFilename());
        assertEquals(testDocument.getContentType(), result.getContentType());
        assertEquals(testDocument.getFileSize(), result.getFileSize());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).existsByCarIdAndFilename(1L, "test-document.pdf");
        verify(carDocumentRepository, times(1)).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(999L, testFile));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenFilenameAlreadyExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carDocumentRepository.existsByCarIdAndFilename(1L, "test-document.pdf")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(testUploadDTO));

        assertEquals("Document with filename 'test-document.pdf' already exists for this car", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).existsByCarIdAndFilename(1L, "test-document.pdf");
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenInvalidContentType() {
        // Arrange
        testUploadDTO.setContentType("text/plain");
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(testUploadDTO));

        assertEquals("Invalid file type. Only PDF files are allowed.", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenFileSizeExceedsLimit() {
        // Arrange
        byte[] largeData = new byte[11 * 1024 * 1024]; // 11MB
        testUploadDTO.setData(largeData);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(testUploadDTO));

        assertEquals("File size exceeds maximum allowed size of 10MB", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_ShouldThrowException_WhenFileIsEmpty() throws IOException {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(1L, emptyFile));

        assertEquals("File cannot be empty", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void getDocumentsByCarId_ShouldReturnListOfDocuments_WhenCarExists() {
        // Arrange
        CarDocument document2 = new CarDocument();
        document2.setId(2L);
        document2.setCar(testCar);
        document2.setFilename("document2.pdf");
        document2.setContentType("application/pdf");
        document2.setFileSize(20L);

        List<CarDocument> documents = Arrays.asList(testDocument, document2);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carDocumentRepository.findByCarId(1L)).thenReturn(documents);

        // Act
        List<CarDocumentResponseDTO> result = documentService.getDocumentsByCarId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDocument.getFilename(), result.get(0).getFilename());
        assertEquals(document2.getFilename(), result.get(1).getFilename());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).findByCarId(1L);
    }

    @Test
    void getDocumentsByCarId_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.getDocumentsByCarId(999L));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
        verify(carDocumentRepository, never()).findByCarId(anyLong());
    }

    @Test
    void getDocumentById_ShouldReturnDocument_WhenDocumentExists() {
        // Arrange
        when(carDocumentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // Act
        CarDocument result = documentService.getDocumentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testDocument.getId(), result.getId());
        assertEquals(testDocument.getFilename(), result.getFilename());
        verify(carDocumentRepository, times(1)).findById(1L);
    }

    @Test
    void getDocumentById_ShouldThrowException_WhenDocumentNotFound() {
        // Arrange
        when(carDocumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.getDocumentById(999L));

        assertEquals("Document not found with id: 999", exception.getMessage());
        verify(carDocumentRepository, times(1)).findById(999L);
    }

    @Test
    void getDocumentResponseById_ShouldReturnDocumentResponseDTO_WhenDocumentExists() {
        // Arrange
        when(carDocumentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // Act
        CarDocumentResponseDTO result = documentService.getDocumentResponseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testDocument.getId(), result.getId());
        assertEquals(testDocument.getFilename(), result.getFilename());
        assertEquals(testDocument.getCar().getId(), result.getCarId());
        verify(carDocumentRepository, times(1)).findById(1L);
    }

    @Test
    void deleteDocument_ShouldDeleteSuccessfully_WhenDocumentExists() {
        // Arrange
        when(carDocumentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // Act
        documentService.deleteDocument(1L);

        // Assert
        verify(carDocumentRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).delete(testDocument);
    }

    @Test
    void deleteDocument_ShouldThrowException_WhenDocumentNotFound() {
        // Arrange
        when(carDocumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.deleteDocument(999L));

        assertEquals("Document not found with id: 999", exception.getMessage());
        verify(carDocumentRepository, times(1)).findById(999L);
        verify(carDocumentRepository, never()).delete(any(CarDocument.class));
    }

    @Test
    void deleteDocumentsByCarId_ShouldDeleteSuccessfully_WhenCarExists() {
        // Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act
        documentService.deleteDocumentsByCarId(1L);

        // Assert
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, times(1)).deleteByCarId(1L);
    }

    @Test
    void deleteDocumentsByCarId_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.deleteDocumentsByCarId(999L));

        assertEquals("Car not found with id: 999", exception.getMessage());
        verify(carRepository, times(1)).findById(999L);
        verify(carDocumentRepository, never()).deleteByCarId(anyLong());
    }

    @Test
    void getAllDocuments_ShouldReturnListOfAllDocuments() {
        // Arrange
        CarDocument document2 = new CarDocument();
        document2.setId(2L);
        document2.setCar(testCar);
        document2.setFilename("document2.pdf");
        document2.setContentType("application/pdf");
        document2.setFileSize(25L);

        List<CarDocument> documents = Arrays.asList(testDocument, document2);
        when(carDocumentRepository.findAll()).thenReturn(documents);

        // Act
        List<CarDocumentResponseDTO> result = documentService.getAllDocuments();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDocument.getFilename(), result.get(0).getFilename());
        assertEquals(document2.getFilename(), result.get(1).getFilename());
        verify(carDocumentRepository, times(1)).findAll();
    }

    @Test
    void uploadDocument_WithMultipartFile_ShouldThrowException_WhenInvalidFileType() throws IOException {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test-document.txt",
                "text/plain",
                "test content".getBytes());
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(1L, invalidFile));

        assertEquals("Invalid file type. Only PDF files are allowed.", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }

    @Test
    void uploadDocument_WithMultipartFile_ShouldThrowException_WhenFileSizeExceedsLimit() throws IOException {
        // Arrange
        byte[] largeData = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-document.pdf",
                "application/pdf",
                largeData);
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(1L, largeFile));

        assertEquals("File size exceeds maximum allowed size of 10MB", exception.getMessage());
        verify(carRepository, times(1)).findById(1L);
        verify(carDocumentRepository, never()).save(any(CarDocument.class));
    }
}