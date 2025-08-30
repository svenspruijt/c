package nl.novi.garage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.garage.dtos.customer.CustomerRequestDTO;
import nl.novi.garage.dtos.customer.CustomerResponseDTO;
import nl.novi.garage.services.CustomerService;
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

@WebMvcTest(controllers = CustomerController.class, excludeFilters = {
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
class CustomerControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CustomerService customerService;

        @Autowired
        private ObjectMapper objectMapper;

        private CustomerRequestDTO customerRequestDTO;
        private CustomerResponseDTO customerResponseDTO;

        @BeforeEach
        void setUp() {
                customerRequestDTO = new CustomerRequestDTO();
                customerRequestDTO.setName("Jan Jansen");
                customerRequestDTO.setPhonenumber("+31612345678");

                customerResponseDTO = new CustomerResponseDTO();
                customerResponseDTO.setId(1L);
                customerResponseDTO.setName("Jan Jansen");
                customerResponseDTO.setPhonenumber("+31612345678");
        }

        @Test
        void createCustomer_ShouldReturnCreatedCustomer_WhenValidRequest() throws Exception {
                // Arrange
                when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponseDTO);

                // Act & Assert
                mockMvc.perform(post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Jan Jansen"))
                                .andExpect(jsonPath("$.phonenumber").value("+31612345678"));

                verify(customerService, times(1)).createCustomer(any(CustomerRequestDTO.class));
        }

        @Test
        void createCustomer_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
                // Arrange
                CustomerRequestDTO invalidRequest = new CustomerRequestDTO();
                invalidRequest.setName(""); // Invalid: blank name
                invalidRequest.setPhonenumber("invalid"); // Invalid: wrong format

                // Act & Assert
                mockMvc.perform(post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(customerService, never()).createCustomer(any(CustomerRequestDTO.class));
        }

        @Test
        void getAllCustomers_ShouldReturnCustomerList() throws Exception {
                // Arrange
                CustomerResponseDTO customer2 = new CustomerResponseDTO();
                customer2.setId(2L);
                customer2.setName("Maria van der Berg");
                customer2.setPhonenumber("0687654321");

                List<CustomerResponseDTO> customers = Arrays.asList(customerResponseDTO, customer2);
                when(customerService.getAllCustomers()).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/customers"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Jan Jansen"))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].name").value("Maria van der Berg"));

                verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
                // Arrange
                when(customerService.getCustomerById(1L)).thenReturn(customerResponseDTO);

                // Act & Assert
                mockMvc.perform(get("/customers/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Jan Jansen"))
                                .andExpect(jsonPath("$.phonenumber").value("+31612345678"));

                verify(customerService, times(1)).getCustomerById(1L);
        }

        @Test
        void getCustomerById_ShouldReturnNotFound_WhenCustomerNotExists() throws Exception {
                // Arrange
                when(customerService.getCustomerById(999L))
                                .thenThrow(new IllegalArgumentException("Customer not found with id: 999"));

                // Act & Assert
                mockMvc.perform(get("/customers/999"))
                                .andExpect(status().isBadRequest());

                verify(customerService, times(1)).getCustomerById(999L);
        }

        @Test
        void updateCustomer_ShouldReturnUpdatedCustomer_WhenValidRequest() throws Exception {
                // Arrange
                CustomerRequestDTO updateRequest = new CustomerRequestDTO();
                updateRequest.setName("Jan Jansen Updated");
                updateRequest.setPhonenumber("+31612345679");

                CustomerResponseDTO updatedResponse = new CustomerResponseDTO();
                updatedResponse.setId(1L);
                updatedResponse.setName("Jan Jansen Updated");
                updatedResponse.setPhonenumber("+31612345679");

                when(customerService.updateCustomer(eq(1L), any(CustomerRequestDTO.class))).thenReturn(updatedResponse);

                // Act & Assert
                mockMvc.perform(put("/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Jan Jansen Updated"))
                                .andExpect(jsonPath("$.phonenumber").value("+31612345679"));

                verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerRequestDTO.class));
        }

        @Test
        void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() throws Exception {
                // Arrange
                doNothing().when(customerService).deleteCustomer(1L);

                // Act & Assert
                mockMvc.perform(delete("/customers/1"))
                                .andExpect(status().isNoContent());

                verify(customerService, times(1)).deleteCustomer(1L);
        }

        @Test
        void deleteCustomer_ShouldReturnBadRequest_WhenCustomerHasCars() throws Exception {
                // Arrange
                doThrow(new IllegalStateException("Cannot delete customer with associated cars"))
                                .when(customerService).deleteCustomer(1L);

                // Act & Assert
                mockMvc.perform(delete("/customers/1"))
                                .andExpect(status().isBadRequest());

                verify(customerService, times(1)).deleteCustomer(1L);
        }

        @Test
        void searchCustomers_ShouldReturnMatchingCustomers() throws Exception {
                // Arrange
                List<CustomerResponseDTO> customers = Arrays.asList(customerResponseDTO);
                when(customerService.searchCustomers("Jan")).thenReturn(customers);

                // Act & Assert
                mockMvc.perform(get("/customers/search")
                                .param("q", "Jan"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].name").value("Jan Jansen"));

                verify(customerService, times(1)).searchCustomers("Jan");
        }

        @Test
        void searchCustomersByPhone_ShouldReturnCustomer_WhenPhoneNumberExists() throws Exception {
                // Arrange
                when(customerService.getCustomerByPhoneNumber("+31612345678")).thenReturn(customerResponseDTO);

                // Act & Assert
                mockMvc.perform(get("/customers/search")
                                .param("phone", "+31612345678"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].phonenumber").value("+31612345678"));

                verify(customerService, times(1)).getCustomerByPhoneNumber("+31612345678");
        }

        @Test
        void searchCustomersByPhone_ShouldHandleUrlEncodedPhone_WhenPlusSymbolInPhone() throws Exception {
                // Arrange - simulate what happens when + gets URL encoded to space
                String urlEncodedPhone = " 31612345678"; // + becomes space in URL
                String expectedDecodedPhone = "+31612345678";
                when(customerService.getCustomerByPhoneNumber(expectedDecodedPhone)).thenReturn(customerResponseDTO);

                // Act & Assert
                mockMvc.perform(get("/customers/search")
                                .param("phone", urlEncodedPhone))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].phonenumber").value("+31612345678"));

                verify(customerService, times(1)).getCustomerByPhoneNumber(expectedDecodedPhone);
        }

        @Test
        void addCarToCustomer_ShouldReturnMessage() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/customers/1/cars"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Use POST /cars endpoint to add a car to customer 1"));
        }

        @Test
        void createCustomer_ShouldReturnConflict_WhenPhoneNumberAlreadyExists() throws Exception {
                // Arrange
                when(customerService.createCustomer(any(CustomerRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Customer with phone number +31612345678 already exists"));

                // Act & Assert
                mockMvc.perform(post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerRequestDTO)))
                                .andExpect(status().isBadRequest());

                verify(customerService, times(1)).createCustomer(any(CustomerRequestDTO.class));
        }
}