package org.example.orderservice;

import org.example.orderservice.Service.FulfillmentClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pb.FulfillmentServiceGrpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class FulfillmentClientTest {
    @Mock
    private FulfillmentServiceGrpc.FulfillmentServiceBlockingStub fulfillmentStub;

    @InjectMocks
    private FulfillmentClient fulfillmentClient;

    @BeforeEach
    void setUp() {
        // Use reflection to inject the mocked stub since it's initialized in the constructor
        ReflectionTestUtils.setField(fulfillmentClient, "fulfillmentStub", fulfillmentStub);
    }

    @Test
    public void testAssignOrder() {
        // Given
        pb.AssignOrderRequest request = pb.AssignOrderRequest.newBuilder()
                .setOrderId("1")
                .setLocation("Test Location")
                .build();

        pb.AssignOrderResponse expectedResponse = pb.AssignOrderResponse.newBuilder()
                .setDeliveryPersonnelId("DE-101")
                .build();

        // Mock the gRPC stub response
        when(fulfillmentStub.assignOrder(request)).thenReturn(expectedResponse);

        // When
        pb.AssignOrderResponse actualResponse = fulfillmentClient.assignOrder("1", "Test Location");

        // Then
        assertNotNull(actualResponse);
        assertEquals("DE-101", actualResponse.getDeliveryPersonnelId());

        // Verify that the stub was called correctly
        verify(fulfillmentStub, times(1)).assignOrder(request);
    }


}
