package oss.jmarsic.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import oss.jmarsic.app.model.Role;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.repository.RoleRepository;
import oss.jmarsic.app.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> { //bilo findByName("ROLE_USER")
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        if(userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setFullName("App Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);

            System.out.println("Admin was created with role: " + adminRole.getName());
        }

        if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
            User user = new User();
            user.setFullName("First User");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user1234"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);
            userRepository.save(user);

            System.out.println("User was created with role: " + userRole.getName());
        }
    }
}
