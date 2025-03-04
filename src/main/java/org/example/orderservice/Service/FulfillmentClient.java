package org.example.orderservice.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;
import pb.FulfillmentServiceGrpc;

@Service
public class FulfillmentClient {
private final FulfillmentServiceGrpc.FulfillmentServiceBlockingStub fulfillmentStub;

public FulfillmentClient(){
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50053)
            .usePlaintext()
            .build();
    fulfillmentStub = FulfillmentServiceGrpc.newBlockingStub(channel);

}

    public pb.AssignOrderResponse assignOrder(String orderId, String location) {
        pb.AssignOrderRequest request = pb.AssignOrderRequest.newBuilder()
                .setOrderId(orderId)
                .setLocation(location)
                .build();

        return fulfillmentStub.assignOrder(request);

}
}
