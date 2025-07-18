package com.shuttle.SMS.service;



import com.shuttle.SMS.dto.LoginDTO;
import com.shuttle.SMS.dto.UserRegistrationDTO;
import com.shuttle.SMS.dto.UserWalletResponseDTO;
import com.shuttle.SMS.dto.WalletRechargeDTO;
import com.shuttle.SMS.model.User;

public interface UserService {
    User registerUser(UserRegistrationDTO userRegistrationDTO);
    User loginUser(LoginDTO loginDto);
    UserWalletResponseDTO rechargeWallet(WalletRechargeDTO rechargeDTO);
}
