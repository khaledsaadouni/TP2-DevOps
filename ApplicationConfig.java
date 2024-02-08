package com.garkclub.config;

import com.garkclub.dto.authentication.AuthenticationResponse;
import com.garkclub.dto.authentication.RegisterRequest;
import com.garkclub.modal.User;
import com.garkclub.modal.enums.Role;
import com.garkclub.repository.UserRepository;
import com.garkclub.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationResponse createAdmin (){
        if(repository.findByEmail("garkadmin@gark.com") == null) {
            PasswordEncoder passwordEncoder = passwordEncoder();
            var user = User.builder()
                    .firstname("admin")
                    .lastname("gark")
                    .createdAt(Instant.now())
                    .email("garkadmin@gark.com")
                    .password(passwordEncoder.encode("garkadmin"))
                    .role(Role.ADMINGARK)
                    .build();
            repository.save(user);
            return AuthenticationResponse.builder()
                    .user(null)
                    .build();
        }
        return null;
    }
}