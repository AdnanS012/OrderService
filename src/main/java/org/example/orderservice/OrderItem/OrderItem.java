package org.example.orderservice.OrderItem;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class OrderItem {
    private Long menuItemId;
    private int quantity;
    private  BigDecimal price;

    protected OrderItem() {}


    public OrderItem(Long menuItemId, int quantity, BigDecimal price){
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getMenuItemId(){ return menuItemId;}
    public int getQuantity(){ return quantity;}
    public BigDecimal getPrice() { return price; }
}
