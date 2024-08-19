package oss.jmarsic.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
}
