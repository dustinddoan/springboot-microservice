package com.example.paymentservice.service;

import com.example.paymentservice.entity.TransactionDetails;
import com.example.paymentservice.exception.PaymentServiceException;
import com.example.paymentservice.model.PaymentMode;
import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.model.PaymentResponse;
import com.example.paymentservice.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {


    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public long doPayment(PaymentRequest request) {
        log.info("Recording transaction {}", request);
        TransactionDetails transactionDetails = TransactionDetails.builder()
                .orderId(request.getOrderId())
                .amout(request.getAmount())
                .referenceNumber(request.getReferenceNumber())
                .paymentMode(request.getPaymentMode().name())
                .paymentDate(Instant.now())
                .paymentStatus("SUCCESS")
                .build();

        transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction completed with id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String id) {
        log.info("Fetching transaction {}", id);
        TransactionDetails transactionDetails = transactionDetailsRepository
                .findByOrderId(Long.parseLong(id));
        log.info("Transaction details - : {}", transactionDetails);

        return PaymentResponse.builder()
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmout())
                .orderId(transactionDetails.getOrderId())
                .paymentDate(transactionDetails.getPaymentDate())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .build();
    }
}
