package com.shuttle.SMS.controller;

import com.shuttle.SMS.dto.LoginDTO;
import com.shuttle.SMS.dto.UserRegistrationDTO;
import com.shuttle.SMS.dto.UserWalletResponseDTO;
import com.shuttle.SMS.dto.WalletRechargeDTO;

import com.shuttle.SMS.model.User;
import com.shuttle.SMS.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService =userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            User user =userService.registerUser(registrationDTO);

            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDto) {
        try {
            User user =userService.loginUser(loginDto);
            if (user==null) {
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }

            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/recharge")
    public ResponseEntity<UserWalletResponseDTO> rechargeWallet(@RequestBody WalletRechargeDTO rechargeDTO) {
        UserWalletResponseDTO response = userService.rechargeWallet(rechargeDTO);
        return ResponseEntity.ok(response);
    }

}
