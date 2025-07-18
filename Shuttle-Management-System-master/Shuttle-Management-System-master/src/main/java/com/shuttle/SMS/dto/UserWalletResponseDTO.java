package com.shuttle.SMS.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserWalletResponseDTO {
    private Long userId;
    private BigDecimal balance;
}
