package com.lab13.delivery_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab13.delivery_api.dto.PayoutRequest;
import com.lab13.delivery_api.dto.PayoutResponse;
import com.lab13.delivery_api.service.PayoutService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dasher")
@RequiredArgsConstructor
@Slf4j
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping("/payout")
    public ResponseEntity<PayoutResponse> calculatePayout(@Valid @RequestBody PayoutRequest request){
        log.info("Received request to calculate payout for dasher: {}", request.getDasherId());         

        PayoutResponse response = payoutService.calculatePayout(request);
        return ResponseEntity.ok(response);
    }
}
