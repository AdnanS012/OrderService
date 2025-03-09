package org.example.orderservice.AuthenticationInterceptor;

import io.grpc.*;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class BasicAuthInterceptor implements ClientInterceptor {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BasicAuthInterceptor.class);
    private final String username;
    private final String password;

    public BasicAuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String authHeader = "Basic " + java.util.Base64.getEncoder()
                        .encodeToString((username + ":" + password).getBytes());
                log.info("Adding Authorization header: {}", authHeader);
                headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), authHeader);
                super.start(responseListener, headers);
            }
        };
    }
}
