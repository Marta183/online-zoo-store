package kms.onlinezoostore.config;

import kms.onlinezoostore.security.AccessDeniedHandlerImpl;
import kms.onlinezoostore.security.authentication.AuthenticationFilter;
import kms.onlinezoostore.security.authentication.AuthenticationMainEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static kms.onlinezoostore.entities.enums.UserRole.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationMainEntryPoint mainAuthEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final AuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;

    private static final String[] PUBLIC_URLs = {
            "/api/v1/auth/**",
//            "/v1/api-docs",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_URLs).permitAll()
                               .requestMatchers(PATCH, "/api/v1/users/profile").hasAnyAuthority(ADMIN.name(), CLIENT.name())
                               .requestMatchers(PATCH, "/api/v1/users/password").hasAnyAuthority(ADMIN.name(), CLIENT.name())
                               .requestMatchers(GET, "/api/v1/users").hasAuthority(ADMIN.name())
                               .requestMatchers(GET, "/api/v1/**").permitAll()
                               .requestMatchers(POST, "/api/v1/**").hasAuthority(ADMIN.name())
                               .requestMatchers(PUT, "/api/v1/**").hasAuthority(ADMIN.name())
                               .requestMatchers(PATCH, "/api/v1/**").hasAuthority(ADMIN.name())
                               .requestMatchers(DELETE, "/api/v1/**").hasAuthority(ADMIN.name())
                               .anyRequest()
                               .authenticated()
                )
                .exceptionHandling(c -> c.authenticationEntryPoint(mainAuthEntryPoint))
                .exceptionHandling(c -> c.accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                              .addLogoutHandler(logoutHandler)
                              .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
