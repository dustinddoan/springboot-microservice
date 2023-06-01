package com.codedaily.orderservice.external.request;

import com.codedaily.orderservice.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private long orderId;
    private long amout;
    private String referenceNumber;
    private PaymentMode paymentMode;
}
