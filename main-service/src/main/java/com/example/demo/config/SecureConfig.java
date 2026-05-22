package com.example.demo.config;

import com.example.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecureConfig {
    public final UserRepository userRepository;
    public SecureConfig(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/books/**", "/loans/**").hasAnyRole("ADMIN", "READER")
                        .requestMatchers("/", "/index.html")
                        .hasAnyRole("READER", "ADMIN")
                        .requestMatchers("/api/user/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books/**", "/loans/**", "/users/**", "/recommendations/**").hasAnyRole("READER", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form.defaultSuccessUrl("/", true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll());

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {

            com.example.demo.model.User dbUser = userRepository.findByEmail(email);

            if (dbUser == null) {
                throw new UsernameNotFoundException("User not found: " + email);
            }


            return User.withUsername(dbUser.getEmail())
                    .password(dbUser.getPassword())
                    .roles(dbUser.getRole().name())
                    .disabled(!dbUser.getActive())
                    .build();
        };

    }
}
