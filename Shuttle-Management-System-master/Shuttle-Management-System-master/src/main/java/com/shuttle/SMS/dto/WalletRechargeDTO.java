package com.shuttle.SMS.dto;

import lombok.Data;

@Data
public class WalletRechargeDTO {
    private Long adminId;
    private Long userId;
    private double amount;
}
