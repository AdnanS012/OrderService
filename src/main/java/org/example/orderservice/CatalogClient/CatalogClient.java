package org.example.orderservice.CatalogClient;

import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class CatalogClient {
    private final RestTemplate restTemplate;
    private final String catalogServiceBaseUrl;

    public CatalogClient(RestTemplateBuilder restTemplateBuilder, @Value("${catalog.service.url}") String catalogServiceBaseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.catalogServiceBaseUrl = catalogServiceBaseUrl;
    }

    public RestaurantDTO getRestaurantById(Long restaurantId) {
       try{
           String url = String.format("%s/restaurants/%d", catalogServiceBaseUrl, restaurantId);
           return restTemplate.getForObject(url, RestaurantDTO.class);
       }catch(HttpClientErrorException.NotFound e){
           return null;
       }
    }
    public MenuItemDTO getMenuItemById(Long menuItemId) {
        try{
            String url = String.format("%s/menu-items/%d", catalogServiceBaseUrl, menuItemId);
            return restTemplate.getForObject(url, MenuItemDTO.class);

        }catch (HttpClientErrorException.NotFound e){
            return null;
        }
    }


}