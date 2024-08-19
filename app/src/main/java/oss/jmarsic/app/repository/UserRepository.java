package oss.jmarsic.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
