package com.smartparking.service;
import com.smartparking.model.User;
import com.smartparking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already registered");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(User::isVerified)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public void activateUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        user.setVerified(true);
        userRepository.save(user);
    }

    public User updateProfile(Long userId, String name, String contactNumber, String password) {
        User user = getById(userId);
        if (name != null && !name.isBlank()) user.setName(name);
        if (contactNumber != null) user.setContactNumber(contactNumber);
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}
