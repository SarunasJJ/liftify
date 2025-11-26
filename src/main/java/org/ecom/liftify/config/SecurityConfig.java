package org.ecom.liftify.config;

import org.ecom.liftify.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/login/**", "/oauth2/**").permitAll();

                    auth.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll();

                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN");

                    auth.requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN");

                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(userService)
                        )
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .build();
    }
}
