package org.example.orderservice.Service;

import org.example.orderservice.DTO.OrderRequestDTO;
import org.example.orderservice.DTO.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    List<OrderResponseDTO> getOrdersByUserId(Long userId);

}
