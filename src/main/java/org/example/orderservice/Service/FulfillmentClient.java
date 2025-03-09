package org.example.orderservice.Service;

import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.orderservice.AuthenticationInterceptor.BasicAuthInterceptor;
import org.springframework.stereotype.Service;
import pb.FulfillmentServiceGrpc;

@Service
public class FulfillmentClient {
private final FulfillmentServiceGrpc.FulfillmentServiceBlockingStub fulfillmentStub;

public FulfillmentClient(String username, String password) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50056)
            .usePlaintext()
            .build();
    BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor(username, password);
    fulfillmentStub = FulfillmentServiceGrpc.newBlockingStub(ClientInterceptors.intercept(channel,authInterceptor));

}

    public pb.AssignOrderResponse assignOrder(String orderId, String location) {
        pb.AssignOrderRequest request = pb.AssignOrderRequest.newBuilder()
                .setOrderId(orderId)
                .setLocation(location)
                .build();

        return fulfillmentStub.assignOrder(request);

}
}
