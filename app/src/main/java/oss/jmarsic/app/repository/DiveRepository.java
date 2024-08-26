package oss.jmarsic.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.Dive;

import java.util.List;
import java.util.UUID;

public interface DiveRepository extends JpaRepository<Dive, UUID> {
    List<Dive> findByUserId(UUID userId);
}
