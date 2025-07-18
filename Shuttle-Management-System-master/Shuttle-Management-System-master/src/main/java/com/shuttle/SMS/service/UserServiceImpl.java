package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.LoginDTO;
import com.shuttle.SMS.dto.UserRegistrationDTO;
import com.shuttle.SMS.dto.UserWalletResponseDTO;

import com.shuttle.SMS.dto.WalletRechargeDTO;

import com.shuttle.SMS.model.User;
import com.shuttle.SMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User registerUser(UserRegistrationDTO dto) {
        if (!dto.getEmail().toLowerCase().endsWith("@lpu.in")) {
//            throw new RuntimeException("University email id needed. Please use a valid @lpu.in email address.");
            throw new UniveristyEmailException("University email id needed. Please use a valid @lpu.in email address.");
        }


        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
//            throw new RuntimeException("User with email " + dto.getEmail() + " already exists");
            throw new UserAlreadyExistsException("User with email " + dto.getEmail() + " already exists");
        });

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : "STUDENT")
                .walletBalance(new BigDecimal("100.0"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
    @Override
    public User loginUser(LoginDTO loginDto) {

        Optional<User> userOpt = userRepository.findById(loginDto.getId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getEmail().equals(loginDto.getEmail()) &&
                    passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public UserWalletResponseDTO rechargeWallet(WalletRechargeDTO rechargeDTO) {

        User admin = userRepository.findById(rechargeDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Unauthorized: Only admins can recharge wallets");
        }


        User student = userRepository.findById(rechargeDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Student not found"));


        student.setWalletBalance(student.getWalletBalance().add(
                java.math.BigDecimal.valueOf(rechargeDTO.getAmount())
        ));
        userRepository.save(student);


        UserWalletResponseDTO response = new UserWalletResponseDTO();
        response.setUserId(student.getId());
        response.setBalance(student.getWalletBalance());
        return response;
    }

}
