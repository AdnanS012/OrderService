package org.example.orderservice.Models;

import jakarta.persistence.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.OrderItem.OrderItem;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders_tbl")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @NotNull
    private Long restaurantId;

    @ElementCollection
    private List<OrderItem> items;

    private String orderInstructions;
    private String deliveryInstructions;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    public Order(){}

    public Order(Long userId,Long restaurantId,List<OrderItem> items,String orderInstructions,String deliveryInstructions,OrderStatus status,BigDecimal totalPrice){
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.orderInstructions = orderInstructions;
        this.deliveryInstructions = deliveryInstructions;
        this.status = status;
        this.totalPrice = totalPrice;
}

    public void updateStatus(OrderStatus newStatus) {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot change status of a delivered order.");
        }
        this.status = newStatus;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRestaurantId() { return restaurantId; }
    public List<OrderItem> getOrderItems() { return items; }
    public String getOrderInstructions() { return orderInstructions; }
    public String getDeliveryInstructions() { return deliveryInstructions; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }


}
