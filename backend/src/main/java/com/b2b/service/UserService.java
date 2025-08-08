package com.b2b.service;

import com.b2b.dto.request.UserRegistrationRequest;
import com.b2b.dto.request.UserUpdateRequest;
import com.b2b.dto.response.UserResponse;
import com.b2b.entity.User;
import com.b2b.exception.ResourceAlreadyExistsException;
import com.b2b.exception.ResourceNotFoundException;
import com.b2b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsername(usernameOrEmail)
            .or(() -> userRepository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));
    }

    public UserResponse createUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getActiveUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable)
            .map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
            .map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        return userRepository.findBySearchTerm(search, pageable)
            .map(UserResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchActiveUsers(String search, Pageable pageable) {
        return userRepository.findByIsActiveAndSearchTerm(true, search, pageable)
            .map(UserResponse::new);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ResourceAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setIsActive(true);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}