package org.example.orderservice.DTO;

import java.math.BigDecimal;
import java.util.List;


public class OrderRequestDTO {
    private Long userId;
    private Long restaurantId;
    private List<OrderItemDTO> items;
    private String orderInstructions;
    private String deliveryInstructions;
    private BigDecimal totalPrice;

    public OrderRequestDTO() {}

    public OrderRequestDTO(Long userId, Long restaurantId, List<OrderItemDTO> items,
                           String orderInstructions, String deliveryInstructions) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.orderInstructions = orderInstructions;
        this.deliveryInstructions = deliveryInstructions;
    }

    public Long getUserId() { return userId; }
    public Long getRestaurantId() { return restaurantId; }
    public List<OrderItemDTO> getItems() { return items; }
    public String getOrderInstructions() { return orderInstructions; }
    public String getDeliveryInstructions() { return deliveryInstructions; }
    public BigDecimal getTotalPrice() { return totalPrice; }

}
