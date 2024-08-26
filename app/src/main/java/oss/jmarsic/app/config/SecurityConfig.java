package oss.jmarsic.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import oss.jmarsic.app.service.UserDetailsServiceImpl;

import java.io.IOException;
import java.util.Set;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception {
        http
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/user/**").hasAuthority("USER")
                    .requestMatchers("/", "/home").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                    .loginPage("/login")
//                    .defaultSuccessUrl("/home", true) // ako ne prode prijava idi na home
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler((request, response, exception) -> {
                        System.out.println("Login failed: " + exception.getMessage());
                        response.sendRedirect("/login?error");
                    })
                    .permitAll()
            )
            .logout((logout) -> logout
                    .permitAll()
            )
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                if (roles.contains("ADMIN")) {
                    System.out.println("Trying to get admin page...");
                    System.out.println("User roles: " + AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                    response.sendRedirect("/admin");
                } else if (roles.contains("USER")) {
                    System.out.println("Trying to get user page...");
                    System.out.println("User roles: " + AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                    response.sendRedirect("/user");
                } else {
                    System.out.println("Wrong username or password, please try again..");
                    response.sendRedirect("/login");
            }
        };
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserDetailsServiceImpl userDetailsService) {

//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder.encode("password"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
        return this.userDetailsService;
    }
}
