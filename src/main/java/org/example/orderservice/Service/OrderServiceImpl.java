package org.example.orderservice.Service;

import org.example.orderservice.CatalogClient.CatalogClient;
import org.example.orderservice.DTO.*;
import org.example.orderservice.Enum.OrderStatus;
import org.example.orderservice.InvalidOrderException;
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
          validItems.add(new OrderItemDTO(menuItem.getId(), menuItem.getName(), item.getQuantity()));
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
    public List<RestaurantDTO> getAllRestaurants() {
        return catalogClient.getAllRestaurants();
    }

    public List<MenuItemDTO> getMenuItemsByRestaurant(Long restaurantId) {
        return catalogClient.getMenuItemsByRestaurantId(restaurantId);
    }

}
