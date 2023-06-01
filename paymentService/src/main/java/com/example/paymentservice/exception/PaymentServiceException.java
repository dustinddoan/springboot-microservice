package com.example.paymentservice.exception;


import lombok.Data;

@Data
public class PaymentServiceException extends RuntimeException {
    private String errorCode;

    public PaymentServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
