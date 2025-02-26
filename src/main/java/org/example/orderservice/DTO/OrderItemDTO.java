package org.example.orderservice.DTO;


import java.math.BigDecimal;

public class OrderItemDTO {
    private Long menuItemId;
    private int quantity;
    private String name;
    private BigDecimal price;
    public OrderItemDTO() {}

    public OrderItemDTO(Long menuItemId, String name,int quantity, BigDecimal price) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.name = name;
        this.price = price;

    }

    public Long getMenuItemId() { return menuItemId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public String getName() { return name; }
}
