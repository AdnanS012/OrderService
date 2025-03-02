package org.example.orderservice.Service;

import org.example.orderservice.DTO.OrderRequestDTO;
import org.example.orderservice.DTO.OrderResponseDTO;
import org.example.orderservice.Enum.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    List<OrderResponseDTO> getOrdersByUserId(Long userId);
  OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
}
