package com.example.StarterPack.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final ApplicationProperties _ApplicationProperties;
        private final UserDetailsService userDetailsService; 
        private final String[] Public_POST_Endpoint = { "/users", "/auth/login","/auth/me","/auth/logout" };
        private final String[] Public_GET_Endpoint = { "/auth/me","/users/verify-email" };
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(csrf -> csrf.disable()) // tắt csrf cho test API
                        .authorizeHttpRequests(request -> request
                                .requestMatchers(HttpMethod.POST, Public_POST_Endpoint).permitAll()
                                .requestMatchers(HttpMethod.GET, Public_GET_Endpoint).permitAll()
                                // .requestMatchers(HttpMethod.GET, "/users").hasRole(Roles.ADMIN.name())
                        .anyRequest().authenticated()
                        )
                        // .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler))
                        // .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                // jwt -> jwt.decoder(customJwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter())).authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                ;
                http.cors(customizer -> {
                        customizer.configurationSource(corsConfigurationSource());
                });
                // http.addFilterBefore(new UsernamePasswordAuthenticationFilter(), LogoutFilter.class);
                return http.build();
        }

        private CorsConfigurationSource corsConfigurationSource() {
                return new CorsConfigurationSource() {
                        @Override
                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(_ApplicationProperties.getAllowedOrigins());
                                System.out.println("--------------------Security------------------------");
                                System.out.println("_ApplicationProperties.getAllowedOrigins(): "
                                                + _ApplicationProperties.getAllowedOrigins());
                                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                config.setAllowedHeaders(List.of("*"));
                                config.setAllowCredentials(true);
                                return config;
                        }
                };
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }
        @Bean
        AuthenticationManager authenticationManager() {
                DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
                daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
                return new ProviderManager(daoAuthenticationProvider);
        }
}
