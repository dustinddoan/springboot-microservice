package com.codedaily.orderservice.external.client;

import com.codedaily.orderservice.exception.CustomException;
import com.codedaily.orderservice.external.response.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// need to add INTERCEPTOR to handle request to PRODUCT-SERVICE/product
@FeignClient(name = "PRODUCT-SERVICE/product")
@CircuitBreaker(name="external", fallbackMethod = "fallback")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity);

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long productId);

    default void fallback(Exception exception) {
        throw new CustomException("ProductService is not available", "UNAVAILABLE", 500);
    }

}
