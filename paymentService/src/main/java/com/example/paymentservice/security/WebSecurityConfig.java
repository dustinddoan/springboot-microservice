package com.example.paymentservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .antMatchers("/payment/**")
                        .hasAuthority("SCOPE_internal")// need to add the scope in  the CLOUD-GATEWAY
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        
        return http.build();
    }
}
//okta:
//    oauth2:
//    issuer: https://dev-86709474.okta.com/oauth2/default
//    audience: api://default
//    client-id: 0oa9s8330oCKK4EYo5d7
//    client-secret: bxZADHRv0vejpDMIsLSwaAMe1Ir0p4TJGmb9zY1D
//    scopes: openid, profile, email, offline_access, internal
//    redirect-uri: /login/oauth2/code/okta