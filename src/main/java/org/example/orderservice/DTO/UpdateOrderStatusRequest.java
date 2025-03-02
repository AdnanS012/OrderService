package org.example.orderservice.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.example.orderservice.Enum.OrderStatus;

public class UpdateOrderStatusRequest {
    @NotNull(message = "status parameter is required")
    private OrderStatus status;

    @JsonCreator
    public UpdateOrderStatusRequest(@JsonProperty("status") OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() { return status; }

}
