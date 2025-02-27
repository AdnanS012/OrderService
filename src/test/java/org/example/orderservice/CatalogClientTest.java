package org.example.orderservice;


import org.example.orderservice.CatalogClient.CatalogClient;
import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder; // Mock RestTemplateBuilder

    @Mock
    private RestTemplate restTemplate; // Mock RestTemplate

    private CatalogClient catalogClient; // Instance to test
    private final String baseUrl = "http://catalog-service";

    @BeforeEach
    void setUp() {
        // Mock RestTemplateBuilder to return a mock RestTemplate
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        // Manually instantiate CatalogClient with the mocked RestTemplateBuilder
        catalogClient = new CatalogClient(restTemplateBuilder,baseUrl);
    }

    @Test
    public void shouldFetchRestaurantById() {
        // Given
        Long restaurantId = 101L;
        RestaurantDTO mockResponse = new RestaurantDTO(101L, "Test Restaurant", "Test Address");

        // Mock RestTemplate behavior
        when(restTemplate.getForObject("http://catalog-service/restaurants/" + restaurantId, RestaurantDTO.class))
                .thenReturn(mockResponse);

        // When
        RestaurantDTO response = catalogClient.getRestaurantById(restaurantId);

        // Then
        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals("Test Restaurant", response.getName());
        assertEquals("Test Address", response.getAddress());

        // Verify that the RestTemplate method was called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(RestaurantDTO.class));
    }
    @Test
    public void shouldReturnNullWhenMenuItemNotFound() {
        // Given
        Long menuItemId = 999L;

        // Mock RestTemplate behavior to throw HttpClientErrorException.NotFound
        when(restTemplate.getForObject("http://catalog-service/menu-items/" + menuItemId, MenuItemDTO.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        MenuItemDTO response = catalogClient.getMenuItemById(menuItemId);

        // Then
        assertNull(response);

        // Verify that the RestTemplate method was called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(MenuItemDTO.class));
    }
    @Test
    public void shouldFetchMenuItemById() {
        // Given
        Long menuItemId = 202L;
        MenuItemDTO mockResponse = new MenuItemDTO(202L, "Burger", "Delicious burger", new BigDecimal("10.00"), 101L);

        // Mock RestTemplate behavior
        when(restTemplate.getForObject("http://catalog-service/menu-items/" + menuItemId, MenuItemDTO.class))
                .thenReturn(mockResponse);

        // When
        MenuItemDTO response = catalogClient.getMenuItemById(menuItemId);

        // Then
        assertNotNull(response);
        assertEquals(202L, response.getId());
        assertEquals("Burger", response.getName());
        assertEquals("Delicious burger", response.getDescription());
        assertEquals(new BigDecimal("10.00"), response.getPrice());
        assertEquals(101L, response.getRestaurantId());

        // Verify that the RestTemplate method was called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(MenuItemDTO.class));
    }

}
