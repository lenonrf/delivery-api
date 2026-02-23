package com.lab13.delivery_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequest {

    @NotNull(message = "Dasher id is required")
    @NotBlank(message = "Dasher id cannot be null")
    private String dasherId;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Cannot be a future date")
    private LocalDate date;
}
