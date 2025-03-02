package org.example.orderservice.Service;

import org.example.orderservice.CatalogClient.CatalogClient;
import org.example.orderservice.DTO.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.ExceptionHandler.InvalidOrderException;
import org.example.orderservice.ExceptionHandler.OrderNotFoundException;
import org.example.orderservice.Models.Order;
import org.example.orderservice.OrderItem.OrderItem;
import org.example.orderservice.Repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderServiceImpl(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }


    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        //validate the Restaurant
        RestaurantDTO restaurant = catalogClient.getRestaurantById(orderRequest.getRestaurantId());
      if(restaurant == null){
          throw new InvalidOrderException("Restaurant not found");
      }
      //validate each MenuItem
      List<OrderItemDTO> validItems = new ArrayList<>();
      List<OrderItem> orderItem = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

      for(OrderItemDTO item: orderRequest.getItems()){
          MenuItemDTO menuItem = catalogClient.getMenuItemById(item.getMenuItemId());

      if(menuItem!=null){
          BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())); //Calculate total price
            totalPrice = totalPrice.add(itemTotal); //Add item total to order total
          validItems.add(new OrderItemDTO(menuItem.getId(), item.getQuantity()));
            orderItem.add(new OrderItem(menuItem.getId(), item.getQuantity(), menuItem.getPrice()));
      }
      }
        if (validItems.isEmpty()) {
            throw new InvalidOrderException("No valid menu items found in the order.");
        }


        // Create and save the Order
        Order order = new Order(
        orderRequest.getUserId(),
        orderRequest.getRestaurantId(),
        orderItem, // Now correctly storing OrderItem list
        orderRequest.getOrderInstructions(),
        orderRequest.getDeliveryInstructions(),
        OrderStatus.CREATED,
        totalPrice
                );
        Order savedOrder = orderRepository.save(order);

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getRestaurantId(),
                validItems,
                savedOrder.getOrderInstructions(),
                savedOrder.getDeliveryInstructions(),
                savedOrder.getStatus(),
                savedOrder.getTotalPrice()
        );

}

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponseDTO> orderResponseDTOs = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemDTO> orderItems = new ArrayList<>();
            for (OrderItem item : order.getOrderItems()) {
                orderItems.add(new OrderItemDTO(item.getMenuItemId(), item.getQuantity()));
            }

            OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                    order.getId(),
                    order.getUserId(),
                    order.getRestaurantId(),
                    orderItems,
                    order.getOrderInstructions(),
                    order.getDeliveryInstructions(),
                    order.getStatus(),
                    order.getTotalPrice()
            );
            orderResponseDTOs.add(orderResponseDTO);
        }

        return orderResponseDTOs;
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return catalogClient.getAllRestaurants();
    }

    public List<MenuItemDTO> getMenuItemsByRestaurant(Long restaurantId) {
        return catalogClient.getMenuItemsByRestaurantId(restaurantId);
    }
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.updateStatus(newStatus);
        orderRepository.save(order);
        return null;
    }


}
