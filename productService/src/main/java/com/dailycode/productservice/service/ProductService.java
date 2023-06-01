package com.dailycode.productservice.service;

import com.dailycode.productservice.model.ProductRequest;
import com.dailycode.productservice.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long productId);

    void reduceQuantity(long productId, long quantity);
}
