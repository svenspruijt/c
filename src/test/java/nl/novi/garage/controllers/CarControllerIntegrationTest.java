package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.car.CarRequestDTO;
import nl.novi.garage.dtos.car.CarResponseDTO;
import nl.novi.garage.services.CarService;
import nl.novi.garage.security.JwtService;
import nl.novi.garage.services.UserDetailsServiceImpl;
import nl.novi.garage.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarController.class, excludeFilters = {
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
class CarControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CarService carService;

        @Autowired
        private ObjectMapper objectMapper;

        private CarRequestDTO carRequestDTO;
        private CarResponseDTO carResponseDTO;

        @BeforeEach
        void setUp() {
                carRequestDTO = new CarRequestDTO();
                carRequestDTO.setCustomerId(1L);
                carRequestDTO.setBrand("Toyota");
                carRequestDTO.setModel("Corolla");
                carRequestDTO.setLicensePlate("AB-123-CD");

                carResponseDTO = new CarResponseDTO();
                carResponseDTO.setId(1L);
                carResponseDTO.setCustomerId(1L);
                carResponseDTO.setCustomerName("Jan Jansen");
                carResponseDTO.setBrand("Toyota");
                carResponseDTO.setModel("Corolla");
                carResponseDTO.setLicensePlate("AB-123-CD");
        }

        @Test
        void createCar_ShouldReturnCreatedCar_WhenValidRequest() throws Exception {
                // Arrange
                when(carService.createCar(any(CarRequestDTO.class))).thenReturn(carResponseDTO);

                // Act & Assert
                mockMvc.perform(post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(carRequestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.brand").value("Toyota"))
                                .andExpect(jsonPath("$.model").value("Corolla"))
                                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                                .andExpect(jsonPath("$.customerId").value(1))
                                .andExpect(jsonPath("$.customerName").value("Jan Jansen"));

                verify(carService, times(1)).createCar(any(CarRequestDTO.class));
        }

        @Test
        void createCar_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
                // Arrange
                CarRequestDTO invalidRequest = new CarRequestDTO();
                invalidRequest.setCustomerId(null); // Invalid: null customer ID
                invalidRequest.setBrand(""); // Invalid: blank brand
                invalidRequest.setModel(""); // Invalid: blank model
                invalidRequest.setLicensePlate("invalid plate"); // Invalid: wrong format

                // Act & Assert
                mockMvc.perform(post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(carService, never()).createCar(any(CarRequestDTO.class));
        }

        @Test
        void getAllCars_ShouldReturnCarList() throws Exception {
                // Arrange
                CarResponseDTO car2 = new CarResponseDTO();
                car2.setId(2L);
                car2.setCustomerId(1L);
                car2.setCustomerName("Jan Jansen");
                car2.setBrand("Honda");
                car2.setModel("Civic");
                car2.setLicensePlate("XY-456-ZW");

                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO, car2);
                when(carService.getAllCars()).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].brand").value("Honda"));

                verify(carService, times(1)).getAllCars();
        }

        @Test
        void getCarById_ShouldReturnCar_WhenCarExists() throws Exception {
                // Arrange
                when(carService.getCarById(1L)).thenReturn(carResponseDTO);

                // Act & Assert
                mockMvc.perform(get("/cars/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.brand").value("Toyota"))
                                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"));

                verify(carService, times(1)).getCarById(1L);
        }

        @Test
        void getCarByLicensePlate_ShouldReturnCar_WhenLicensePlateExists() throws Exception {
                // Arrange
                when(carService.getCarByLicensePlate("AB-123-CD")).thenReturn(carResponseDTO);

                // Act & Assert
                mockMvc.perform(get("/cars/license-plate/AB-123-CD"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.licensePlate").value("AB-123-CD"))
                                .andExpect(jsonPath("$.brand").value("Toyota"));

                verify(carService, times(1)).getCarByLicensePlate("AB-123-CD");
        }

        @Test
        void getCarsByCustomerId_ShouldReturnCustomerCars() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.getCarsByCustomerId(1L)).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/customer/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].customerId").value(1))
                                .andExpect(jsonPath("$[0].customerName").value("Jan Jansen"));

                verify(carService, times(1)).getCarsByCustomerId(1L);
        }

        @Test
        void updateCar_ShouldReturnUpdatedCar_WhenValidRequest() throws Exception {
                // Arrange
                CarRequestDTO updateRequest = new CarRequestDTO();
                updateRequest.setCustomerId(1L);
                updateRequest.setBrand("Honda");
                updateRequest.setModel("Civic");
                updateRequest.setLicensePlate("XY-456-ZW");

                CarResponseDTO updatedResponse = new CarResponseDTO();
                updatedResponse.setId(1L);
                updatedResponse.setCustomerId(1L);
                updatedResponse.setCustomerName("Jan Jansen");
                updatedResponse.setBrand("Honda");
                updatedResponse.setModel("Civic");
                updatedResponse.setLicensePlate("XY-456-ZW");

                when(carService.updateCar(eq(1L), any(CarRequestDTO.class))).thenReturn(updatedResponse);

                // Act & Assert
                mockMvc.perform(put("/cars/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.brand").value("Honda"))
                                .andExpect(jsonPath("$.model").value("Civic"))
                                .andExpect(jsonPath("$.licensePlate").value("XY-456-ZW"));

                verify(carService, times(1)).updateCar(eq(1L), any(CarRequestDTO.class));
        }

        @Test
        void deleteCar_ShouldReturnNoContent_WhenCarExists() throws Exception {
                // Arrange
                doNothing().when(carService).deleteCar(1L);

                // Act & Assert
                mockMvc.perform(delete("/cars/1"))
                                .andExpect(status().isNoContent());

                verify(carService, times(1)).deleteCar(1L);
        }

        @Test
        void searchCarsByBrand_ShouldReturnMatchingCars() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.searchCarsByBrand("Toyota")).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search/brand/Toyota"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].brand").value("Toyota"));

                verify(carService, times(1)).searchCarsByBrand("Toyota");
        }

        @Test
        void searchCarsByModel_ShouldReturnMatchingCars() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.searchCarsByModel("Corolla")).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search/model/Corolla"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].model").value("Corolla"));

                verify(carService, times(1)).searchCarsByModel("Corolla");
        }

        @Test
        void searchCars_ShouldReturnAllCars_WhenNoParameters() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.getAllCars()).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1));

                verify(carService, times(1)).getAllCars();
        }

        @Test
        void searchCars_ShouldSearchByBrand_WhenBrandParameterProvided() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.searchCarsByBrand("Toyota")).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search")
                                .param("brand", "Toyota"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].brand").value("Toyota"));

                verify(carService, times(1)).searchCarsByBrand("Toyota");
        }

        @Test
        void searchCars_ShouldSearchByBrandAndModel_WhenBothParametersProvided() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.searchCarsByBrandAndModel("Toyota", "Corolla")).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search")
                                .param("brand", "Toyota")
                                .param("model", "Corolla"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                                .andExpect(jsonPath("$[0].model").value("Corolla"));

                verify(carService, times(1)).searchCarsByBrandAndModel("Toyota", "Corolla");
        }

        @Test
        void searchCars_ShouldSearchByLicensePlate_WhenLicensePlateParameterProvided() throws Exception {
                // Arrange
                List<CarResponseDTO> cars = Arrays.asList(carResponseDTO);
                when(carService.searchCarsByLicensePlate("AB-123")).thenReturn(cars);

                // Act & Assert
                mockMvc.perform(get("/cars/search")
                                .param("licensePlate", "AB-123"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].licensePlate").value("AB-123-CD"));

                verify(carService, times(1)).searchCarsByLicensePlate("AB-123");
        }

        @Test
        void createCar_ShouldReturnBadRequest_WhenLicensePlateAlreadyExists() throws Exception {
                // Arrange
                when(carService.createCar(any(CarRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Car with license plate AB-123-CD already exists"));

                // Act & Assert
                mockMvc.perform(post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(carRequestDTO)))
                                .andExpect(status().isBadRequest());

                verify(carService, times(1)).createCar(any(CarRequestDTO.class));
        }

        @Test
        void createCar_ShouldReturnBadRequest_WhenCustomerNotFound() throws Exception {
                // Arrange
                when(carService.createCar(any(CarRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Customer not found with id: 999"));

                // Act & Assert
                mockMvc.perform(post("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(carRequestDTO)))
                                .andExpect(status().isBadRequest());

                verify(carService, times(1)).createCar(any(CarRequestDTO.class));
        }
}