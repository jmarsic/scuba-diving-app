package oss.jmarsic.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.Dive;
import oss.jmarsic.app.repository.DiveRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DiveService {
    @Autowired
    private DiveRepository diveRepository;

    public void saveDive(Dive dive) {
        diveRepository.save(dive);
    }

    public List<Dive> getDivesBtUserId(UUID userId) {
        return diveRepository.findByUserId(userId);
    }

    public Dive findById(UUID id) {
        return diveRepository.findById(id).orElse(null);
    }

    public void deleteDive(UUID id) {
        diveRepository.deleteById(id);
    }
}
