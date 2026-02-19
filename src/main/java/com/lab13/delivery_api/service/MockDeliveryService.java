package com.lab13.delivery_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lab13.delivery_api.model.Delivery;

@Service
public class MockDeliveryService implements DeliveryService {

    @Override
    public List<Delivery> getDeliveriesForDasher(String dasherId, LocalDate date) {

        return List.of(
            new Delivery(
                "d1",
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 10, 0),
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 10, 30),
                5.5
            ),
            new Delivery(
                "d2",
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 14, 0),
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 14, 45),
                8.2
            ),
            new Delivery(
                "d3",
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 19, 0),
                LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 19, 30),
                3.5
            )
        );

    }

}

