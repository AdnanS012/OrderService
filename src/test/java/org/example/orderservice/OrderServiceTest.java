package org.example.orderservice;

import org.example.orderservice.CatalogClient.CatalogClient;
import org.example.orderservice.DTO.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.ExceptionHandler.InvalidOrderException;
import org.example.orderservice.Models.Order;
import org.example.orderservice.OrderItem.OrderItem;
import org.example.orderservice.Repository.OrderRepository;
import org.example.orderservice.Service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        BigDecimal expectedTotalPrice = new BigDecimal("600.00");

        Order order = new Order(
                1L,
                101L,
                List.of(new OrderItem(2L, 3, new BigDecimal("200.00"))),
                "Extra spicy",
                "Leave at door",
                OrderStatus.CREATED,
                expectedTotalPrice
        );

        Order savedOrder = new Order(
                1L,
                101L,
                List.of(new OrderItem(2L, 3, new BigDecimal("200.00"))),
                "Extra spicy",
                "Leave at door",
                OrderStatus.CREATED,
                expectedTotalPrice
        );

        // Mocking
        when(catalogClient.getRestaurantById(101L)).thenReturn(restaurantDTO);
        when(catalogClient.getMenuItemById(2L)).thenReturn(menuItemDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

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
        assertEquals(OrderStatus.CREATED, responseDTO.getStatus());
        assertEquals(expectedTotalPrice, responseDTO.getTotalPrice());

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
        when(catalogClient.getMenuItemById(2L)).thenReturn(null);

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
