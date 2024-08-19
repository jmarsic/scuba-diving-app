package oss.jmarsic.app.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.Role;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.repository.UserRepository;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Trying to get user by email:" + email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User email not found"));

        System.out.println("User found: " + user.getEmail());
        System.out.println("User password: " + user.getPassword());
        System.out.println("User role: " + user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));

        Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());



        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
        //provjerit return tj sta vracamo (user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
