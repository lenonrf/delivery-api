package com.lab13.delivery_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutResponse {

    private String dasherId;
    private LocalDate date;
    private Integer totalDeliveries;
    private Integer totalMinutes;
    private Double totalDistance;
    private BigDecimal basePay;
    private BigDecimal timePay;
    private BigDecimal distancePay;
    private BigDecimal totalPayout;

    private BigDecimal peakTimePay;

}
