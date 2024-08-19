package oss.jmarsic.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
