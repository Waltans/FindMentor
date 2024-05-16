package com.CodeBuddy.CodeBuddy.extern.configuration;


import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                .authorizeHttpRequests(authorizeHttp -> {
                    authorizeHttp.requestMatchers("/").permitAll();
                    authorizeHttp.requestMatchers(HttpMethod.POST,"/mentors").anonymous();
                    authorizeHttp.requestMatchers(HttpMethod.POST, "/students").anonymous();
                    authorizeHttp.requestMatchers("/students/**").hasAnyAuthority("ROLE_STUDENT");
                    authorizeHttp.requestMatchers("/mentors/**").hasAnyAuthority("ROLE_MENTOR");
                    // пока не создали сущность админа
                    authorizeHttp.requestMatchers("/keywords/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_MENTOR");
                    authorizeHttp.anyRequest().authenticated();
                })
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
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
