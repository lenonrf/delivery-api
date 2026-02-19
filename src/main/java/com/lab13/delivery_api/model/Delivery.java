package com.lab13.delivery_api.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    private String deliveryId;
    private LocalDateTime starTime;
    private LocalDateTime endTime;
    private Double distance;
}
