package com.dailycode.productservice.service;

import com.dailycode.productservice.entity.Product;
import com.dailycode.productservice.exception.ProductServiceException;
import com.dailycode.productservice.model.ProductRequest;
import com.dailycode.productservice.model.ProductResponse;
import com.dailycode.productservice.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product: " + productRequest.toString());

        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);

        log.info("Product created");

        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductServiceException("Product not found with given Id not found", "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reducing quantity {} of productId: {} ", quantity, productId);
        Product product = productRepository
              .findById(productId)
              .orElseThrow(() -> new ProductServiceException("Product not found with given Id not found", "PRODUCT_NOT_FOUND"));

        if (product.getQuantity() < quantity) {
            throw new ProductServiceException("Product quantity is not enough", "INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        log.info("Product quantity update successfully");
    }
}
