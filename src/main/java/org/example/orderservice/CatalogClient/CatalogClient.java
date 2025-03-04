package org.example.orderservice.CatalogClient;

import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CatalogClient {
    private static final Logger log = LoggerFactory.getLogger(CatalogClient.class);
    private final RestTemplate restTemplate;
    private final String catalogServiceBaseUrl;

    public CatalogClient(RestTemplateBuilder restTemplateBuilder, @Value("${catalog.service.url}") String catalogServiceBaseUrl) {
        this.restTemplate = restTemplateBuilder.basicAuthentication("admin", "admin123").build();
        this.catalogServiceBaseUrl = catalogServiceBaseUrl;
    }

    public RestaurantDTO getRestaurantById(Long restaurantId) {
       try{
           String url = String.format("%s/restaurants/%d", catalogServiceBaseUrl, restaurantId);
           return restTemplate.getForObject(url, RestaurantDTO.class);

       }catch(HttpClientErrorException e){
           if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
               return null;
           }
           throw e;
       }
    }
    public MenuItemDTO getMenuItemById(Long restaurantId,Long menuItemId) {
        if (menuItemId == null) {
            log.error("MenuItem ID is null when calling CatalogService");
            return null;
        }
        try{
            String url = String.format("%s/restaurants/%d/menu-items/%d", catalogServiceBaseUrl,restaurantId, menuItemId);
            log.info("Calling CatalogService: {}", url);

            MenuItemDTO menuItem =  restTemplate.getForObject(url, MenuItemDTO.class);
            log.info("Received response: {}", menuItem);
           return menuItem;
        }catch (HttpClientErrorException e) {
            log.error("Error fetching menu item {}: {}", menuItemId, e.getMessage());

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }
    public List<RestaurantDTO> getAllRestaurants() {
        String url = catalogServiceBaseUrl + "/restaurants";
        ResponseEntity<List<RestaurantDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    public List<MenuItemDTO> getMenuItemsByRestaurantId(Long restaurantId) {
        String url = catalogServiceBaseUrl + "/restaurants/" + restaurantId + "/menu-items";
        ResponseEntity<List<MenuItemDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }


}