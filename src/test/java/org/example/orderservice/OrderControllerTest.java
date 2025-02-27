package org.example.orderservice;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.orderservice.Controller.OrderController;
import org.example.orderservice.DTO.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper; // To convert Java objects to JSON

    @Test
    public void shouldCreateOrderSuccessfully() throws Exception {
        // Given: Prepare request and response objects
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, "Pizza", 3)),
                "Extra spicy",
                "Leave at door"
        );
        BigDecimal expectedTotalPrice = new BigDecimal("600.0");

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                1L,
                1L,
                101L,
                List.of(new OrderItemDTO(2L, "Pizza", 3)),
                "Extra spicy",
                "Leave at door",
                OrderStatus.CREATED,
                expectedTotalPrice
        );

        // Mock the service behavior
        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(responseDTO);

        // When & Then: Perform POST request and verify the response
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))) // Convert request object to JSON
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.restaurantId").value(101L))
                .andExpect(jsonPath("$.items[0].menuItemId").value(2L))
                .andExpect(jsonPath("$.items[0].name").value("Pizza"))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.orderInstructions").value("Extra spicy"))
                .andExpect(jsonPath("$.deliveryInstructions").value("Leave at door"))
                .andExpect(jsonPath("$.status").value(OrderStatus.CREATED.name()))
                .andExpect(jsonPath("$.totalPrice").value(expectedTotalPrice.toString())); // Convert BigDecimal to String

        // Verify service was called
        Mockito.verify(orderService, Mockito.times(1)).createOrder(any(OrderRequestDTO.class));
    }
}
