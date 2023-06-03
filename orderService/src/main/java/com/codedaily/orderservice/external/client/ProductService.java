package com.codedaily.orderservice.external.client;

import com.codedaily.orderservice.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// need to add INTERCEPTOR to handle request to PRODUCT-SERVICE/product
@CircuitBreaker(name="external", fallbackMethod = "fallback")
@FeignClient(name = "PRODUCT-SERVICE/product")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity);

    default void fallback(Exception exception) {
        throw new CustomException("ProductService is not available", "UNAVAILABLE", 500);
    }

}
