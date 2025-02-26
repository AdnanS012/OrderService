package org.example.orderservice.DTO;

import java.math.BigDecimal;

public class MenuItemDTO {
    private  Long restaurantId;
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    public MenuItemDTO(Long id, String name, String description, BigDecimal price, Long restaurantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

}
