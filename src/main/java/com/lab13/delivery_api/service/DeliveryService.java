package com.lab13.delivery_api.service;

import java.time.LocalDate;
import java.util.List;

import com.lab13.delivery_api.model.Delivery;

public interface DeliveryService {

    List<Delivery> getDeliveriesForDasher(String dasherId, LocalDate date);
}
