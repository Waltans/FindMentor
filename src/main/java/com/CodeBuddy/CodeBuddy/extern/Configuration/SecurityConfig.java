//package com.CodeBuddy.CodeBuddy.extern.Configuration;
//
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
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
