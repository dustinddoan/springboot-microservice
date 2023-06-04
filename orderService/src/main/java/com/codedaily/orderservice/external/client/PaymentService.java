package com.codedaily.orderservice.external.client;

import com.codedaily.orderservice.exception.CustomException;
import com.codedaily.orderservice.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


// need to add INTERCEPTOR to handle request to PRODUCT-SERVICE/product
@FeignClient(name = "PAYMENT-SERVICE/payment")
@CircuitBreaker(name="external", fallbackMethod = "doPaymentFallback")
public interface PaymentService {
    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request);


    default ResponseEntity<Long> doPaymentFallback(PaymentRequest request, Exception ex) {
        // Fallback logic to handle the failure
        throw new CustomException("PaymentService is not available", "UNAVAILABLE", 500);
//        return ResponseEntity.status(500).build();
    }
}
