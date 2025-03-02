package org.example.orderservice.Controller;

import org.example.orderservice.DTO.OrderRequestDTO;
import org.example.orderservice.DTO.OrderResponseDTO;
import org.example.orderservice.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Validated @RequestBody OrderRequestDTO request){
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        OrderResponseDTO response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
      try{
          List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);
          return ResponseEntity.ok(orders);
      } catch (RuntimeException e) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Service error", e);
      }
    }



}
