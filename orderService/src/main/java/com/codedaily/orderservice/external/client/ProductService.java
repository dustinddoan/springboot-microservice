package com.codedaily.orderservice.external.client;

import com.codedaily.orderservice.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// need to add INTERCEPTOR to handle request to PRODUCT-SERVICE/product
@FeignClient(name = "PRODUCT-SERVICE/product")
@CircuitBreaker(name="external", fallbackMethod = "reduceQuantityFallback")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity);


    default ResponseEntity<Void> reduceQuantityFallback(long productId, long quantity, Exception ex) {
        // Fallback logic to handle the failure
        throw new CustomException("PaymentService is not available", "UNAVAILABLE", 500);

//        return ResponseEntity.status(500).build();
    }

}
