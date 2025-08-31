package nl.novi.garage.services;

import nl.novi.garage.dtos.part.PartRequestDTO;
import nl.novi.garage.dtos.part.PartResponseDTO;
import nl.novi.garage.dtos.part.PartStockUpdateDTO;
import nl.novi.garage.models.Part;
import nl.novi.garage.repositories.PartRepository;
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
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartService partService;

    private Part testPart;
    private PartRequestDTO testPartRequestDTO;
    private PartStockUpdateDTO testStockUpdateDTO;

    @BeforeEach
    void setUp() {
        testPart = new Part();
        testPart.setId(1L);
        testPart.setName("Remblokken set");
        testPart.setPrice(new BigDecimal("45.99"));
        testPart.setStock(25);

        testPartRequestDTO = new PartRequestDTO();
        testPartRequestDTO.setName("Remblokken set");
        testPartRequestDTO.setPrice(new BigDecimal("45.99"));
        testPartRequestDTO.setStock(25);

        testStockUpdateDTO = new PartStockUpdateDTO();
        testStockUpdateDTO.setStock(30);
    }

    @Test
    void createPart_ShouldReturnPartResponseDTO_WhenValidRequest() {
        // Arrange
        when(partRepository.existsByNameIgnoreCase("Remblokken set")).thenReturn(false);
        when(partRepository.save(any(Part.class))).thenReturn(testPart);

        // Act
        PartResponseDTO result = partService.createPart(testPartRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testPart.getId(), result.getId());
        assertEquals(testPart.getName(), result.getName());
        assertEquals(testPart.getPrice(), result.getPrice());
        assertEquals(testPart.getStock(), result.getStock());
        verify(partRepository, times(1)).existsByNameIgnoreCase("Remblokken set");
        verify(partRepository, times(1)).save(any(Part.class));
    }

    @Test
    void createPart_ShouldThrowException_WhenPartNameAlreadyExists() {
        // Arrange
        when(partRepository.existsByNameIgnoreCase("Remblokken set")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> partService.createPart(testPartRequestDTO));

        assertEquals("Part with name 'Remblokken set' already exists", exception.getMessage());
        verify(partRepository, times(1)).existsByNameIgnoreCase("Remblokken set");
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    void getAllParts_ShouldReturnListOfParts() {
        // Arrange
        Part part2 = new Part();
        part2.setId(2L);
        part2.setName("Motorolie 5W-30");
        part2.setPrice(new BigDecimal("35.50"));
        part2.setStock(40);

        List<Part> parts = Arrays.asList(testPart, part2);
        when(partRepository.findAll()).thenReturn(parts);

        // Act
        List<PartResponseDTO> result = partService.getAllParts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testPart.getName(), result.get(0).getName());
        assertEquals(part2.getName(), result.get(1).getName());
        verify(partRepository, times(1)).findAll();
    }

    @Test
    void getPartById_ShouldReturnPart_WhenPartExists() {
        // Arrange
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));

        // Act
        PartResponseDTO result = partService.getPartById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testPart.getId(), result.getId());
        assertEquals(testPart.getName(), result.getName());
        assertEquals(testPart.getPrice(), result.getPrice());
        assertEquals(testPart.getStock(), result.getStock());
        verify(partRepository, times(1)).findById(1L);
    }

    @Test
    void getPartById_ShouldThrowException_WhenPartNotFound() {
        // Arrange
        when(partRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> partService.getPartById(999L));

        assertEquals("Part not found with id: 999", exception.getMessage());
        verify(partRepository, times(1)).findById(999L);
    }

    @Test
    void getPartByName_ShouldReturnPart_WhenNameExists() {
        // Arrange
        when(partRepository.findByNameIgnoreCase("remblokken set")).thenReturn(Optional.of(testPart));

        // Act
        PartResponseDTO result = partService.getPartByName("remblokken set");

        // Assert
        assertNotNull(result);
        assertEquals(testPart.getName(), result.getName());
        verify(partRepository, times(1)).findByNameIgnoreCase("remblokken set");
    }

    @Test
    void getPartByName_ShouldThrowException_WhenNameNotFound() {
        // Arrange
        when(partRepository.findByNameIgnoreCase("Nonexistent part")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> partService.getPartByName("Nonexistent part"));

        assertEquals("Part not found with name: Nonexistent part", exception.getMessage());
        verify(partRepository, times(1)).findByNameIgnoreCase("Nonexistent part");
    }

    @Test
    void updatePart_ShouldReturnUpdatedPart_WhenValidRequest() {
        // Arrange
        PartRequestDTO updateDTO = new PartRequestDTO();
        updateDTO.setName("Updated part");
        updateDTO.setPrice(new BigDecimal("50.00"));
        updateDTO.setStock(20);

        Part updatedPart = new Part();
        updatedPart.setId(1L);
        updatedPart.setName("Updated part");
        updatedPart.setPrice(new BigDecimal("50.00"));
        updatedPart.setStock(20);

        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partRepository.existsByNameIgnoreCase("Updated part")).thenReturn(false);
        when(partRepository.save(any(Part.class))).thenReturn(updatedPart);

        // Act
        PartResponseDTO result = partService.updatePart(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPart.getName(), result.getName());
        assertEquals(updatedPart.getPrice(), result.getPrice());
        assertEquals(updatedPart.getStock(), result.getStock());
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).save(any(Part.class));
    }

    @Test
    void updatePart_ShouldThrowException_WhenNewNameAlreadyExists() {
        // Arrange
        PartRequestDTO updateDTO = new PartRequestDTO();
        updateDTO.setName("Existing part");
        updateDTO.setPrice(new BigDecimal("50.00"));
        updateDTO.setStock(20);

        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partRepository.existsByNameIgnoreCase("Existing part")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> partService.updatePart(1L, updateDTO));

        assertEquals("Part with name 'Existing part' already exists", exception.getMessage());
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, never()).save(any(Part.class));
    }

    @Test
    void updatePartStock_ShouldReturnUpdatedPart_WhenValidRequest() {
        // Arrange
        Part updatedPart = new Part();
        updatedPart.setId(1L);
        updatedPart.setName("Remblokken set");
        updatedPart.setPrice(new BigDecimal("45.99"));
        updatedPart.setStock(30);

        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partRepository.save(any(Part.class))).thenReturn(updatedPart);

        // Act
        PartResponseDTO result = partService.updatePartStock(1L, testStockUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(30, result.getStock());
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).save(any(Part.class));
    }

    @Test
    void deletePart_ShouldDeleteSuccessfully_WhenPartExists() {
        // Arrange
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));

        // Act
        partService.deletePart(1L);

        // Assert
        verify(partRepository, times(1)).findById(1L);
        verify(partRepository, times(1)).delete(testPart);
    }

    @Test
    void deletePart_ShouldThrowException_WhenPartNotFound() {
        // Arrange
        when(partRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> partService.deletePart(999L));

        assertEquals("Part not found with id: 999", exception.getMessage());
        verify(partRepository, times(1)).findById(999L);
        verify(partRepository, never()).delete(any(Part.class));
    }

    @Test
    void searchPartsByName_ShouldReturnMatchingParts() {
        // Arrange
        String searchName = "rem";
        List<Part> parts = Arrays.asList(testPart);
        when(partRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(parts);

        // Act
        List<PartResponseDTO> result = partService.searchPartsByName(searchName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPart.getName(), result.get(0).getName());
        verify(partRepository, times(1)).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void getPartsWithLowStock_ShouldReturnPartsWithLowStock() {
        // Arrange
        Integer threshold = 10;
        List<Part> parts = Arrays.asList(testPart);
        when(partRepository.findByStockLessThanEqual(threshold)).thenReturn(parts);

        // Act
        List<PartResponseDTO> result = partService.getPartsWithLowStock(threshold);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partRepository, times(1)).findByStockLessThanEqual(threshold);
    }

    @Test
    void getPartsInStock_ShouldReturnPartsWithStock() {
        // Arrange
        List<Part> parts = Arrays.asList(testPart);
        when(partRepository.findByStockGreaterThan(0)).thenReturn(parts);

        // Act
        List<PartResponseDTO> result = partService.getPartsInStock();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partRepository, times(1)).findByStockGreaterThan(0);
    }

    @Test
    void getOutOfStockParts_ShouldReturnPartsWithZeroStock() {
        // Arrange
        List<Part> parts = Arrays.asList();
        when(partRepository.findByStockEquals(0)).thenReturn(parts);

        // Act
        List<PartResponseDTO> result = partService.getOutOfStockParts();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(partRepository, times(1)).findByStockEquals(0);
    }
}