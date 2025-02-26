package org.example.orderservice.DTO;
import java.math.BigDecimal;

public class RestaurantDTO {
    private Long id;
    private String name;
    private String address;

    public RestaurantDTO() {
    }

    public RestaurantDTO(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
