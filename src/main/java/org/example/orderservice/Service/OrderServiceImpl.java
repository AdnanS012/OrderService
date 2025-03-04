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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final FulfillmentClient fulfillmentClient;

    public OrderServiceImpl(OrderRepository orderRepository, CatalogClient catalogClient, FulfillmentClient fulfillmentClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.fulfillmentClient = fulfillmentClient;
    }


    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        log.info("Received order request: {}", orderRequest);

        //validate the Restaurant
        RestaurantDTO restaurant = catalogClient.getRestaurantById(orderRequest.getRestaurantId());
      if(restaurant == null){
          throw new InvalidOrderException("Restaurant not found");
      }
        log.info("Restaurant found: {}", restaurant);
      //validate each MenuItem
      List<OrderItemDTO> validItems = new ArrayList<>();
      List<OrderItem> orderItem = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

      for(OrderItemDTO item: orderRequest.getItems()){
          if (item.getMenuItemId() == null) {
              log.error("MenuItem ID is null in order request: {}", item);
              throw new InvalidOrderException("Menu item ID cannot be null.");
          }
          log.info("Fetching menu item with ID: {}", item.getMenuItemId());
          MenuItemDTO menuItem = catalogClient.getMenuItemById(orderRequest.getRestaurantId(), item.getMenuItemId());

      if(menuItem!=null){
          log.info("Fetched menu item: {}", menuItem);

          //Ensure the menu item belongs to the correct restaurant
          if (!menuItem.getRestaurantId().equals(orderRequest.getRestaurantId())) {
              log.warn("Menu item {} does not belong to restaurant {}", menuItem.getId(), orderRequest.getRestaurantId());
              continue; // Skip this item
          }

          BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())); //Calculate total price
            totalPrice = totalPrice.add(itemTotal); //Add item total to order total
          validItems.add(new OrderItemDTO(menuItem.getId(), item.getQuantity()));
            orderItem.add(new OrderItem(menuItem.getId(), item.getQuantity(), menuItem.getPrice()));
      }else {
          log.warn("Menu item {} not found", item.getMenuItemId());
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
        //Step 4: Call FulfillmentService via gRPC to Assign Delivery Personnel
        pb.AssignOrderResponse fulfillmentResponse = fulfillmentClient.assignOrder(
                savedOrder.getId().toString(),
                orderRequest.getDeliveryInstructions() // Assuming location is in delivery instructions
        );
        // 🔹 Step 5: Update Order Status to "ASSIGNED"
        savedOrder.updateStatus(OrderStatus.ASSIGNED);
        orderRepository.save(savedOrder);
        log.info("Order {} assigned to delivery personnel: {}", savedOrder.getId(), fulfillmentResponse.getDeliveryPersonnelId());

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getRestaurantId(),
                validItems,
                savedOrder.getOrderInstructions(),
                savedOrder.getDeliveryInstructions(),
                savedOrder.getStatus(),
                savedOrder.getTotalPrice(),
                fulfillmentResponse.getDeliveryPersonnelId()
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
                    order.getTotalPrice(),
                    null

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
