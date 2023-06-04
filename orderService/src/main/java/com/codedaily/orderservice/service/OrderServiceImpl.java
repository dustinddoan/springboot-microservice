package com.codedaily.orderservice.service;

import com.codedaily.orderservice.entity.Order;
import com.codedaily.orderservice.exception.CustomException;
import com.codedaily.orderservice.external.client.PaymentService;
import com.codedaily.orderservice.external.client.ProductService;
import com.codedaily.orderservice.external.request.PaymentRequest;
import com.codedaily.orderservice.external.response.PaymentResponse;
import com.codedaily.orderservice.external.response.ProductResponse;
import com.codedaily.orderservice.model.OrderResponse;
import com.codedaily.orderservice.model.OrderRequest;
import com.codedaily.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        // ProductService -> Block Products (Reduce the quantity)
        log.info("Placing Order Request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        // Order Entity -> Save the data with Status Order Created
        log.info("Creting Order with Status: {}", "CREATED");
        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .amount(orderRequest.getTotalAmount())
                .build();
        order = orderRepository.save(order);

        //         // Payment Service -> Payment -> Success -> COMPLETED, Else -> CANCELLED
        log.info("Calling Payment Service to complete the order");
        PaymentRequest payment = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amout(orderRequest.getTotalAmount())
                .build();

        String orderStatus = "";
        try {
            paymentService.doPayment(payment);
            orderStatus = "PLACED";
            log.info("Payment Successful. Changing Order Status to: {}", orderStatus);
        } catch (Exception e) {
            orderStatus = "FAILED";
            log.info("Payment Failed. Changing Order Status to: {}", orderStatus);
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order Created with Id: {}", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Getting Order Details with Id: {}", orderId);
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found with id: " + orderId, "NOT_FOUND", 404));

        log.info("Invoke Product Service to fetch the product details for id: {}", order.getProductId());
        ProductResponse productResponse =
                restTemplate.getForObject(
                  "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                  ProductResponse.class
                );
        assert productResponse != null;
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productId(productResponse.getProductId())
                .productName(productResponse.getProductName())
                .quantity(productResponse.getQuantity())
                .price(productResponse.getPrice())
                .build();


        log.info("Getting Payment Details from the Payment Service");
        PaymentResponse paymentResponse =
                restTemplate.getForObject(
                  "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                  PaymentResponse.class
                );
        assert paymentResponse != null;
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .orderId(paymentResponse.getOrderId())
                .status(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentDate(paymentResponse.getPaymentDate())
                .amount(paymentResponse.getAmount())
                .build();


        log.info("Getting Order Details from the Order Service");

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
