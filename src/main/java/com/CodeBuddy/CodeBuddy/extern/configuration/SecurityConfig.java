//package com.CodeBuddy.CodeBuddy.extern.configuration;
//
//
//import org.springframework.context.annotation.configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//
//    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeRequests(authorizeHttp -> {
//                    authorizeHttp.requestMatchers("/**").permitAll();
//                    authorizeHttp.anyRequest().permitAll();
//                });
//
//        return http.build();
//    }
//}
