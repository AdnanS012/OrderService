package org.example.orderservice;

import org.example.orderservice.CatalogClient.CatalogClient;
import org.example.orderservice.DTO.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.ExceptionHandler.InvalidOrderException;
import org.example.orderservice.Models.Order;
import org.example.orderservice.OrderItem.OrderItem;
import org.example.orderservice.Repository.OrderRepository;
import org.example.orderservice.Service.FulfillmentClient;
import org.example.orderservice.Service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private FulfillmentClient fulfillmentClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void testCreateOrderSuccessfully() {
        // Given
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );
        RestaurantDTO restaurantDTO = new RestaurantDTO(101L, "Test Restaurant", "Test Address");
        MenuItemDTO menuItemDTO = new MenuItemDTO(2L, "Pizza", "Delicious pizza", new BigDecimal("200.00"), 101L);

        BigDecimal expectedTotalPrice = menuItemDTO.getPrice().multiply(new BigDecimal(requestDTO.getItems().get(0).getQuantity()));

        Order savedOrderBeforeFulfillment = new Order(
                1L, 101L,
                List.of(new OrderItem(2L, 3, new BigDecimal("200.00"))),
                "Extra spicy",
                "Leave at door",
                OrderStatus.CREATED,
                expectedTotalPrice
        );
        ReflectionTestUtils.setField(savedOrderBeforeFulfillment, "id", 1001L); // Ensure ID is set

        Order savedOrderAfterFulfillment = new Order(
                1L, 101L,
                List.of(new OrderItem(2L, 3, new BigDecimal("200.00"))),
                "Extra spicy",
                "Leave at door",
                OrderStatus.ASSIGNED,
                expectedTotalPrice
        );
        ReflectionTestUtils.setField(savedOrderAfterFulfillment, "id", 1001L);

        pb.AssignOrderResponse fulfillmentResponse = pb.AssignOrderResponse.newBuilder()
                .setDeliveryPersonnelId("DE-101")
                .build();

        // Mocking
        when(catalogClient.getRestaurantById(101L)).thenReturn(restaurantDTO);
        when(catalogClient.getMenuItemById(101L, 2L)).thenReturn(menuItemDTO);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> savedOrderBeforeFulfillment) // First save (CREATED status)
                .thenAnswer(invocation -> savedOrderAfterFulfillment); // Second save (ASSIGNED status)
        when(fulfillmentClient.assignOrder(anyString(), anyString())).thenReturn(fulfillmentResponse);

        // When
        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getUserId());
        assertEquals(101L, responseDTO.getRestaurantId());
        assertEquals(1, responseDTO.getItems().size());
        assertEquals(3, responseDTO.getItems().get(0).getQuantity());
        assertEquals("Extra spicy", responseDTO.getOrderInstructions());
        assertEquals("Leave at door", responseDTO.getDeliveryInstructions());
        assertEquals(OrderStatus.ASSIGNED, responseDTO.getStatus()); // Verify final status
        assertEquals(expectedTotalPrice, responseDTO.getTotalPrice());

        // Verify interactions
        verify(orderRepository, times(2)).save(any(Order.class)); // Should be saved twice
        verify(fulfillmentClient, times(1)).assignOrder(anyString(), anyString()); // Ensure fulfillment is triggered
    }


    @Test
    public void shouldThrowExceptionWhenItemDoesNotExistInCatalog() {
        // Given
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );
        RestaurantDTO restaurantDTO = new RestaurantDTO(101L, "Test Restaurant", "Test Address");

        // Mocking
        when(catalogClient.getRestaurantById(101L)).thenReturn(restaurantDTO);
        when(catalogClient.getMenuItemById(101L, 2L)).thenReturn(null);

        // When & Then
        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(requestDTO));
    }
    @Test
    public void shouldThrowExceptionWhenRestaurantNotFound() {
        // Given
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L,
                101L,
                List.of(new OrderItemDTO(2L, 3)),
                "Extra spicy",
                "Leave at door"
        );

        // Mocking
        when(catalogClient.getRestaurantById(101L)).thenReturn(null);

        // When & Then
        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(requestDTO));
    }

    @Test
    public void testGetOrdersByUserId() {
        // Given
        Long userId = 1L;
        List<Order> orders = List.of(
                new Order(1L, 1L, List.of(new OrderItem(2L, 3, new BigDecimal("200.00"))), "Extra spicy", "Leave at door", OrderStatus.CREATED, new BigDecimal("600.00"))
        );

        // Mocking
        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        // When
        List<OrderResponseDTO> responseDTOs = orderService.getOrdersByUserId(userId);

        // Then
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertEquals(1L, responseDTOs.get(0).getUserId());
        assertEquals(1L, responseDTOs.get(0).getRestaurantId());
        assertEquals(1, responseDTOs.get(0).getItems().size());
        assertEquals(3, responseDTOs.get(0).getItems().get(0).getQuantity());
        assertEquals("Extra spicy", responseDTOs.get(0).getOrderInstructions());
        assertEquals("Leave at door", responseDTOs.get(0).getDeliveryInstructions());
        assertEquals(OrderStatus.CREATED, responseDTOs.get(0).getStatus());
        assertEquals(new BigDecimal("600.00"), responseDTOs.get(0).getTotalPrice());
    }

}
