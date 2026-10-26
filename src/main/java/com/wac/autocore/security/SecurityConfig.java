package com.wac.autocore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Inställningar/userdetails för de tre kontona: admin verkstad och läsare
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){

        String adminPassword = System.getenv("AUTOCORE_ADMIN_PASSWORD");
        String workshopPassword = System.getenv("AUTOCORE_WORKSHOP_PASSWORD");
        String readerPassword = System.getenv("AUTOCORE_READER_PASSWORD");

        if (adminPassword == null || adminPassword.isBlank() ||
            workshopPassword == null || workshopPassword.isBlank() ||
            readerPassword == null || readerPassword.isBlank()){

            throw new IllegalStateException("Lösenord saknas för ett eller flera konton");

        }

        UserDetails admin = User.withUsername("Admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetails workshop = User.withUsername("Verkstad")
                .password(passwordEncoder.encode(workshopPassword))
                .roles("WORKSHOP")
                .build();

        UserDetails reader = User.withUsername("Läsare")
                .password(passwordEncoder.encode(readerPassword))
                .roles("READ_ONLY")
                .build();

        return new InMemoryUserDetailsManager(admin, workshop, reader);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}
