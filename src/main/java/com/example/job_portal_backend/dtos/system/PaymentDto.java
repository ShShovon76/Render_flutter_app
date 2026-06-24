package com.example.job_portal_backend.dtos.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private Long userId;
    private Long subscriptionId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime paymentDate;
    private String description;
}
