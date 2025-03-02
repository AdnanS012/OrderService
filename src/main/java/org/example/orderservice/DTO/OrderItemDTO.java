package org.example.orderservice.DTO;

import org.jetbrains.annotations.NotNull;

public class OrderItemDTO {
    @NotNull
    private Long menuItemId;
    @NotNull
    private int quantity;
    public OrderItemDTO() {}

    public OrderItemDTO(Long menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;

    }

    public Long getMenuItemId() { return menuItemId; }
    public int getQuantity() { return quantity; }
}

