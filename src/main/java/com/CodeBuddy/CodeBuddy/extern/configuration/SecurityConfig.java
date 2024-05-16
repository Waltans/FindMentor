package com.CodeBuddy.CodeBuddy.extern.configuration;


import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final StudentService studentService;

    public SecurityConfig(StudentService studentService) {
        this.studentService = studentService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(new UrlBasedCorsConfigurationSource() {
                            {
                                CorsConfiguration config = new CorsConfiguration();
                                config.applyPermitDefaultValues();
                                config.setAllowCredentials(true);
                                config.addAllowedOriginPattern("*");
                                config.setAllowedMethods(Collections.singletonList("*"));
                                registerCorsConfiguration("/**", config);
                            }
                        })
                )
                .authorizeHttpRequests(authorizeHttp -> {
                    authorizeHttp.requestMatchers("/").permitAll();
                    authorizeHttp.requestMatchers("/mentors").anonymous();
                    authorizeHttp.requestMatchers("/students").anonymous();
                    authorizeHttp.requestMatchers("/students/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_MENTOR");
                    authorizeHttp.requestMatchers("/mentors/**").authenticated();
                })
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                )
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationStudentProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(studentService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
