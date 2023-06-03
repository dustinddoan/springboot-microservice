package com.codedaily.orderservice.external.intercept;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
@Log4j2
public class OAuthRequestInterceptor implements RequestInterceptor {

    @Autowired
    private OAuth2AuthorizedClientManager authorizationManager; // to get token from client

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String accessToken = authorizationManager
                .authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("internal-client") // id in applications.yaml
                        .principal("internal") //scope
                        .build())
                .getAccessToken().getTokenValue();

        log.info("---DUSTIN---token {}", accessToken);

        requestTemplate.header("Authorization", "Bearer " + accessToken);


    }
}
