package com.codedaily.orderservice.service;

import com.codedaily.orderservice.model.OrderResponse;
import com.codedaily.orderservice.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
