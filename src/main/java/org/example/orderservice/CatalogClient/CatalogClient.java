package org.example.orderservice.CatalogClient;

import org.example.orderservice.DTO.MenuItemDTO;
import org.example.orderservice.DTO.RestaurantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
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
       }catch(HttpClientErrorException e){
           if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
               return null;
           }
           throw e;
       }
    }
    public MenuItemDTO getMenuItemById(Long menuItemId) {
        try{
            String url = String.format("%s/menu-items/%d", catalogServiceBaseUrl, menuItemId);
            return restTemplate.getForObject(url, MenuItemDTO.class);

        }catch (HttpClientErrorException e) {
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
        String url = catalogServiceBaseUrl + "/restaurants/" + restaurantId + "/menu";
        ResponseEntity<List<MenuItemDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }


}