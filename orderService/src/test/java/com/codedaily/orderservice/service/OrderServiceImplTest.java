package com.codedaily.orderservice.service;

import com.codedaily.orderservice.entity.Order;
import com.codedaily.orderservice.exception.CustomException;
import com.codedaily.orderservice.external.client.PaymentService;
import com.codedaily.orderservice.external.client.ProductService;
import com.codedaily.orderservice.external.request.PaymentRequest;
import com.codedaily.orderservice.external.response.ProductResponse;
import com.codedaily.orderservice.external.response.PaymentResponse;
import com.codedaily.orderservice.model.OrderRequest;
import com.codedaily.orderservice.model.OrderResponse;
import com.codedaily.orderservice.model.PaymentMode;
import com.codedaily.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @Value(value = "${microservices.product}")
    private String productServiceUrl;

    @Value(value = "${microservices.payment}")
    private String paymentServiceUrl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils
                .setField(orderService, "productServiceUrl", productServiceUrl);

        ReflectionTestUtils
                .setField(orderService, "paymentServiceUrl", paymentServiceUrl);
    }

    @DisplayName("Test_When_Order_Success")
    @Test
    void test_When_Order_Success() {
        // mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        ProductResponse productResponse = getMockProductResponse();
        when(restTemplate.getForObject(
                productServiceUrl + order.getProductId(),
                ProductResponse.class
        )).thenReturn(productResponse);

        PaymentResponse paymentResponse = getMockPaymentResponse();
        when(restTemplate.getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class
        )).thenReturn(paymentResponse);

        // actual
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        // verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(productServiceUrl + order.getProductId(),
                ProductResponse.class);
        verify(restTemplate, times(1)).getForObject(paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class);

        // assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    @DisplayName("Test_When_Order_NotFound")
    @Test
    void test_When_Order_NotFound() {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(
                CustomException.class,
                () -> orderService.getOrderDetails(1)
        );

        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Test_When_Place_Order_Success")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));
        verify(orderRepository, times(2)).save(any());

        assertEquals(orderId, order.getId());
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(2)
                .quantity(1)
                .paymentMode(PaymentMode.APPLE_PAY)
                .totalAmount(100)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productName("iPhone")
                .productId(2)
                .quantity(200)
                .price(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }


}