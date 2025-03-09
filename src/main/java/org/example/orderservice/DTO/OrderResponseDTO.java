package org.example.orderservice.DTO;

import org.example.orderservice.Enum.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private List<OrderItemDTO> items;
    private String orderInstructions;
    private String deliveryInstructions;
    private OrderStatus status;
    private BigDecimal totalPrice;
    //private String deliveryPersonnelId;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Long orderId, Long userId, Long restaurantId, List<OrderItemDTO> items,
                            String orderInstructions, String deliveryInstructions, OrderStatus status, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.orderInstructions = orderInstructions;
        this.deliveryInstructions = deliveryInstructions;
        this.status = status;
        this.totalPrice = totalPrice;
        //this.deliveryPersonnelId = deliveryPersonnelId;
    }
    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public Long getRestaurantId() { return restaurantId; }
    public List<OrderItemDTO> getItems() { return items; }
    public String getOrderInstructions() { return orderInstructions; }
    public String getDeliveryInstructions() { return deliveryInstructions; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    //public String getDeliveryPersonnelId() { return deliveryPersonnelId; }
}
