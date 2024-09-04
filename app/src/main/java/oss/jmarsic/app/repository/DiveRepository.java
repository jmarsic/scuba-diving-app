package oss.jmarsic.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.Dive;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DiveRepository extends JpaRepository<Dive, UUID> {
    Page<Dive> findByUserId(UUID userId, Pageable pageable);

    List<Dive> findByUserIdAndDate(UUID userId, LocalDate date);
}
