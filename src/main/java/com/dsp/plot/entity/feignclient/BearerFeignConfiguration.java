// package com.dsp.plot.entity.feignclient;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import feign.RequestInterceptor;
// public class BearerFeignConfiguration {
//     @Bean
//     public RequestInterceptor bearerTokenRequestInterceptor() {
//         return requestTemplate -> {
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication != null && authentication.getCredentials() instanceof String) {
//                 String currentToken = (String) authentication.getCredentials();
//                 requestTemplate.header("Authorization", "Bearer " + currentToken);
//             }
//         };
//     }
// }