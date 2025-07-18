package com.shuttle.SMS.controller;

import com.shuttle.SMS.dto.UserWalletResponseDTO;
import com.shuttle.SMS.model.User;

import com.shuttle.SMS.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class WalletController {

    private final UserRepository userRepository;

    @Autowired
    public WalletController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/wallet")
    public ResponseEntity<UserWalletResponseDTO> getWalletBalance(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->new RuntimeException("User not found"));
        UserWalletResponseDTO dto =new UserWalletResponseDTO();
        dto.setUserId(user.getId());
        dto.setBalance(user.getWalletBalance());
        return ResponseEntity.ok(dto);
    }
}
