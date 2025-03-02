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
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void shouldReturnBadRequestForInvalidOrderData() throws Exception {
        // Given: Prepare invalid request object (e.g., missing restaurantId)
        OrderRequestDTO invalidRequestDTO = new OrderRequestDTO(
                1L,
                null, // Invalid: restaurantId is null
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );

        // When & Then: Perform POST request and verify the response
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO))) // Convert request object to JSON
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request
    }

    @Test
    public void shouldReturnNotFoundWhenRestaurantNotFound() throws Exception {
        // Given: Prepare request object
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );

        // Mock the service behavior to throw InvalidOrderException
        when(orderService.createOrder(any(OrderRequestDTO.class)))
                .thenThrow(new InvalidOrderException("Restaurant not found"));

        // When & Then: Perform POST request and verify the response
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))) // Convert request object to JSON
                .andExpect(status().isNotFound()); // Expect 404 Not Found
    }


    @Test
    public void shouldCreateOrderSuccessfully() throws Exception {
        // Given: Prepare request and response objects
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );
        BigDecimal expectedTotalPrice = new BigDecimal("600.0");

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                1L,
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
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

    @Test
    public void shouldReturnAllOrdersForUser() throws Exception {
        // Given: User ID and mock order responses
        Long userId = 1L;
        List<OrderResponseDTO> mockOrders = List.of(
                new OrderResponseDTO(1L, 1L, 101L, List.of(new OrderItemDTO(2L, 3)),
                        "Extra spicy", "Leave at door", OrderStatus.CREATED, new BigDecimal("600.00")),
                new OrderResponseDTO(2L, 1L, 102L, List.of(new OrderItemDTO(3L, 2)),
                        "No onions", "Hand it to me", OrderStatus.CREATED, new BigDecimal("300.00"))
        );

        // Mock service response
        when(orderService.getOrdersByUserId(userId)).thenReturn(mockOrders);

        // When & Then: Perform GET request and verify the response
        mockMvc.perform(get("/orders/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[1].orderId").value(2L));

        // Verify the service was called once
        verify(orderService, times(1)).getOrdersByUserId(userId);
    }
    @Test
    public void shouldReturnBadRequestForEmptyOrderItems() throws Exception {
        // Given: Request with empty items list
        OrderRequestDTO invalidRequestDTO = new OrderRequestDTO(
                1L, 101L, Collections.emptyList(), "Extra spicy", "Leave at door"
        );

        // When & Then: Perform POST request and expect 400
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnEmptyListWhenNoOrdersFoundForUser() throws Exception {
        // Given: User ID with no orders
        Long userId = 2L;
        List<OrderResponseDTO> emptyOrders = List.of();

        // Mock service response
        when(orderService.getOrdersByUserId(userId)).thenReturn(emptyOrders);

        // When & Then: Perform GET request and verify the response
        mockMvc.perform(get("/orders/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify the service was called once
        verify(orderService, times(1)).getOrdersByUserId(userId);
    }
    @Test
    public void shouldReturnBadRequestForInvalidUserId() throws Exception {
        // Given: Invalid User ID
        String invalidUserId = "invalid";

        // When & Then: Perform GET request and verify the response
        mockMvc.perform(get("/orders/user/{userId}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void shouldReturnInternalServerErrorOnServiceException() throws Exception {
        // Given: User ID and service exception
        Long userId = 1L;

        // Mock service to throw an exception
        when(orderService.getOrdersByUserId(userId)).thenThrow(new RuntimeException("Service error"));

        // When & Then: Perform GET request and verify the response
        mockMvc.perform(get("/orders/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        // Verify the service was called once
        verify(orderService, times(1)).getOrdersByUserId(userId);
    }

}
