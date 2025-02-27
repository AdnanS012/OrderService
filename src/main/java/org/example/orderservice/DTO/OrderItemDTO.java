package org.example.orderservice.DTO;

public class OrderItemDTO {
    private Long menuItemId;
    private int quantity;
    private String name;
    public OrderItemDTO() {}

    public OrderItemDTO(Long menuItemId, String name,int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.name = name;

    }

    public Long getMenuItemId() { return menuItemId; }
    public int getQuantity() { return quantity; }
    public String getName() { return name; }
}
