package oss.jmarsic.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.Dive;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.repository.DiveRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiveService {
    @Autowired
    private DiveRepository diveRepository;

    public void saveDive(Dive dive) {
        diveRepository.save(dive);
    }

    public Page<Dive> getDivesBtUserIdSortedByDate(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return diveRepository.findByUserIdOrderByDateAsc(userId, pageable);
    }

    public List<Dive> findByDateAndUser(Date date, User user) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return diveRepository.findByUserIdAndDate(user.getId(), localDate);
    }

    public double calculateAverageDepth() {
        List<Dive> dives = diveRepository.findAll();
        return dives.stream()
                .mapToDouble(Dive::getDepth)
                .average()
                .orElse(0.0);
    }

    public List<Integer> getAllDepths() {
        return diveRepository.findAll().stream()
                .map(Dive::getDepth)
                .collect(Collectors.toList());
    }

    public List<Integer> getAllDurations() {
        return diveRepository.findAll().stream()
                .map(Dive::getDuration)
                .collect(Collectors.toList());
    }

    public Dive findById(UUID id) {
        return diveRepository.findById(id).orElse(null);
    }

    public void deleteDive(UUID id) {
        diveRepository.deleteById(id);
    }
}
