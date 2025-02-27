package org.example.orderservice.Controller;

import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.example.orderservice.Service.OrderService;
import org.example.orderservice.Service.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final OrderServiceImpl orderService;


    public RestaurantController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurant(){
        return ResponseEntity.ok(orderService.getAllRestaurants());
    }
    @GetMapping("/{restaurantId}/menu-items")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getMenuItemsByRestaurant(restaurantId));
    }

}
