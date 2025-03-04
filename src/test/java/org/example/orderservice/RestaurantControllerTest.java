package org.example.orderservice;

import org.example.orderservice.Controller.RestaurantController;
import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.example.orderservice.Service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServiceImpl orderService;

    @Test
    @WithMockUser
    void shouldReturnAllRestaurants() throws Exception {
        List<RestaurantDTO> restaurants = List.of(new RestaurantDTO(1L, "Test Restaurant", "Test Address"));

        when(orderService.getAllRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));
    }
    @Test
    @WithMockUser
    void shouldReturnMenuItemsForRestaurant() throws Exception {
        List<MenuItemDTO> menuItems = List.of(new MenuItemDTO(1L, "Pizza", "Delicious Pizza", new BigDecimal("200.00"), 1L));

        when(orderService.getMenuItemsByRestaurant(1L)).thenReturn(menuItems);

        mockMvc.perform(get("/restaurants/1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }


}
