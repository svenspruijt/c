package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.document.CarDocumentResponseDTO;
import nl.novi.garage.dtos.document.CarDocumentUploadDTO;
import nl.novi.garage.models.CarDocument;
import nl.novi.garage.models.Car;
import nl.novi.garage.models.Customer;
import nl.novi.garage.services.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DocumentController.class, excludeFilters = {
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
class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarDocumentResponseDTO documentResponseDTO;
    private CarDocumentUploadDTO documentUploadDTO;
    private CarDocument carDocument;
    private Car testCar;
    private Customer testCustomer;

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

        documentResponseDTO = new CarDocumentResponseDTO();
        documentResponseDTO.setId(1L);
        documentResponseDTO.setCarId(1L);
        documentResponseDTO.setCarLicensePlate("AB-123-CD");
        documentResponseDTO.setFilename("test-document.pdf");
        documentResponseDTO.setContentType("application/pdf");
        documentResponseDTO.setFileSize(1024L);

        documentUploadDTO = new CarDocumentUploadDTO();
        documentUploadDTO.setCarId(1L);
        documentUploadDTO.setFilename("test-document.pdf");
        documentUploadDTO.setData("test pdf content".getBytes());
        documentUploadDTO.setContentType("application/pdf");

        carDocument = new CarDocument();
        carDocument.setId(1L);
        carDocument.setCar(testCar);
        carDocument.setFilename("test-document.pdf");
        carDocument.setFilepath("car_1/test-document.pdf");
        carDocument.setData("test pdf content".getBytes());
        carDocument.setContentType("application/pdf");
        carDocument.setFileSize(16L);
    }

    @Test
    void uploadDocumentWithFile_ShouldReturnCreatedDocument_WhenValidRequest() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test pdf content".getBytes());
        when(documentService.uploadDocument(eq(1L), any())).thenReturn(documentResponseDTO);

        // Act & Assert
        mockMvc.perform(multipart("/documents/cars/1/upload")
                .file(file))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.carLicensePlate").value("AB-123-CD"))
                .andExpect(jsonPath("$.filename").value("test-document.pdf"))
                .andExpect(jsonPath("$.contentType").value("application/pdf"))
                .andExpect(jsonPath("$.fileSize").value(1024));

        verify(documentService, times(1)).uploadDocument(eq(1L), any());
    }

    @Test
    void uploadDocumentWithDTO_ShouldReturnCreatedDocument_WhenValidRequest() throws Exception {
        // Arrange
        when(documentService.uploadDocument(any(CarDocumentUploadDTO.class))).thenReturn(documentResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/documents/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentUploadDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.filename").value("test-document.pdf"));

        verify(documentService, times(1)).uploadDocument(any(CarDocumentUploadDTO.class));
    }

    @Test
    void uploadDocumentWithDTO_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Arrange
        CarDocumentUploadDTO invalidDTO = new CarDocumentUploadDTO();
        invalidDTO.setCarId(null); // Invalid: null car ID
        invalidDTO.setFilename(""); // Invalid: blank filename
        invalidDTO.setData(null); // Invalid: null data
        invalidDTO.setContentType(""); // Invalid: blank content type

        // Act & Assert
        mockMvc.perform(post("/documents/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(documentService, never()).uploadDocument(any(CarDocumentUploadDTO.class));
    }

    @Test
    void getDocumentsByCarId_ShouldReturnDocumentList() throws Exception {
        // Arrange
        CarDocumentResponseDTO document2 = new CarDocumentResponseDTO();
        document2.setId(2L);
        document2.setCarId(1L);
        document2.setCarLicensePlate("AB-123-CD");
        document2.setFilename("second-document.pdf");
        document2.setContentType("application/pdf");
        document2.setFileSize(2048L);

        List<CarDocumentResponseDTO> documents = Arrays.asList(documentResponseDTO, document2);
        when(documentService.getDocumentsByCarId(1L)).thenReturn(documents);

        // Act & Assert
        mockMvc.perform(get("/documents/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].filename").value("test-document.pdf"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].filename").value("second-document.pdf"));

        verify(documentService, times(1)).getDocumentsByCarId(1L);
    }

    @Test
    void getDocumentById_ShouldReturnDocument_WhenDocumentExists() throws Exception {
        // Arrange
        when(documentService.getDocumentResponseById(1L)).thenReturn(documentResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/documents/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.filename").value("test-document.pdf"))
                .andExpect(jsonPath("$.carId").value(1));

        verify(documentService, times(1)).getDocumentResponseById(1L);
    }

    @Test
    void downloadDocument_ShouldReturnFileContent_WhenDocumentExists() throws Exception {
        // Arrange
        when(documentService.getDocumentById(1L)).thenReturn(carDocument);

        // Act & Assert
        mockMvc.perform(get("/documents/1/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test-document.pdf\""))
                .andExpect(content().bytes("test pdf content".getBytes()));

        verify(documentService, times(1)).getDocumentById(1L);
    }

    @Test
    void downloadDocumentByFilename_ShouldReturnFileContent_WhenDocumentExists() throws Exception {
        // Arrange
        List<CarDocumentResponseDTO> documents = Arrays.asList(documentResponseDTO);
        when(documentService.getDocumentsByCarId(1L)).thenReturn(documents);
        when(documentService.getDocumentById(1L)).thenReturn(carDocument);

        // Act & Assert
        mockMvc.perform(get("/documents/cars/1/download/test-document.pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test-document.pdf\""))
                .andExpect(content().bytes("test pdf content".getBytes()));

        verify(documentService, times(1)).getDocumentsByCarId(1L);
        verify(documentService, times(1)).getDocumentById(1L);
    }

    @Test
    void downloadDocumentByFilename_ShouldReturnNotFound_WhenDocumentNotExists() throws Exception {
        // Arrange
        List<CarDocumentResponseDTO> documents = Arrays.asList(documentResponseDTO);
        when(documentService.getDocumentsByCarId(1L)).thenReturn(documents);

        // Act & Assert
        mockMvc.perform(get("/documents/cars/1/download/non-existent.pdf"))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).getDocumentsByCarId(1L);
        verify(documentService, never()).getDocumentById(anyLong());
    }

    @Test
    void deleteDocument_ShouldReturnNoContent_WhenDocumentExists() throws Exception {
        // Arrange
        doNothing().when(documentService).deleteDocument(1L);

        // Act & Assert
        mockMvc.perform(delete("/documents/1"))
                .andExpect(status().isNoContent());

        verify(documentService, times(1)).deleteDocument(1L);
    }

    @Test
    void deleteAllDocumentsByCarId_ShouldReturnNoContent_WhenCarExists() throws Exception {
        // Arrange
        doNothing().when(documentService).deleteDocumentsByCarId(1L);

        // Act & Assert
        mockMvc.perform(delete("/documents/cars/1"))
                .andExpect(status().isNoContent());

        verify(documentService, times(1)).deleteDocumentsByCarId(1L);
    }

    @Test
    void getAllDocuments_ShouldReturnAllDocuments() throws Exception {
        // Arrange
        CarDocumentResponseDTO document2 = new CarDocumentResponseDTO();
        document2.setId(2L);
        document2.setCarId(2L);
        document2.setCarLicensePlate("XY-456-ZW");
        document2.setFilename("another-document.pdf");
        document2.setContentType("application/pdf");
        document2.setFileSize(1536L);

        List<CarDocumentResponseDTO> allDocuments = Arrays.asList(documentResponseDTO, document2);
        when(documentService.getAllDocuments()).thenReturn(allDocuments);

        // Act & Assert
        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].carId").value(1))
                .andExpect(jsonPath("$[1].carId").value(2));

        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    void uploadDocument_ShouldReturnBadRequest_WhenCarNotFound() throws Exception {
        // Arrange
        when(documentService.uploadDocument(any(CarDocumentUploadDTO.class)))
                .thenThrow(new IllegalArgumentException("Car not found with id: 999"));

        documentUploadDTO.setCarId(999L);

        // Act & Assert
        mockMvc.perform(post("/documents/cars/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentUploadDTO)))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).uploadDocument(any(CarDocumentUploadDTO.class));
    }

    @Test
    void uploadDocument_ShouldReturnBadRequest_WhenFilenameAlreadyExists() throws Exception {
        // Arrange
        when(documentService.uploadDocument(any(CarDocumentUploadDTO.class)))
                .thenThrow(new IllegalArgumentException(
                        "Document with filename 'test-document.pdf' already exists for this car"));

        // Act & Assert
        mockMvc.perform(post("/documents/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentUploadDTO)))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).uploadDocument(any(CarDocumentUploadDTO.class));
    }

    @Test
    void uploadDocument_ShouldReturnBadRequest_WhenInvalidFileType() throws Exception {
        // Arrange
        when(documentService.uploadDocument(any(CarDocumentUploadDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid file type. Only PDF files are allowed."));

        documentUploadDTO.setContentType("text/plain");

        // Act & Assert
        mockMvc.perform(post("/documents/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentUploadDTO)))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).uploadDocument(any(CarDocumentUploadDTO.class));
    }

    @Test
    void getDocumentById_ShouldReturnBadRequest_WhenDocumentNotFound() throws Exception {
        // Arrange
        when(documentService.getDocumentResponseById(999L))
                .thenThrow(new IllegalArgumentException("Document not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/documents/999"))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).getDocumentResponseById(999L);
    }

    @Test
    void deleteDocument_ShouldReturnBadRequest_WhenDocumentNotFound() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Document not found with id: 999"))
                .when(documentService).deleteDocument(999L);

        // Act & Assert
        mockMvc.perform(delete("/documents/999"))
                .andExpect(status().isBadRequest());

        verify(documentService, times(1)).deleteDocument(999L);
    }
}