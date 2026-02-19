package com.lab13.delivery_api.service;

import com.lab13.delivery_api.dto.PayoutRequest;
import com.lab13.delivery_api.dto.PayoutResponse;
import com.lab13.delivery_api.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayoutService Tests")
class PayoutServiceTest {
    
    @Mock
    private DeliveryService deliveryService;
    
    @InjectMocks
    private PayoutService payoutService;
    
    private PayoutRequest request;
    private LocalDate testDate;
    
    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 2, 14);
        request = new PayoutRequest("dasher-123", testDate);
    }
    
    @Test
    @DisplayName("Should calculate payout correctly without peak hours")
    void testCalculatePayoutWithoutPeakHours() {
        // ARRANGE
        List<Delivery> deliveries = List.of(
            new Delivery("d1",
                LocalDateTime.of(2024, 2, 14, 10, 0),
                LocalDateTime.of(2024, 2, 14, 10, 30),
                5.5),
            new Delivery("d2",
                LocalDateTime.of(2024, 2, 14, 14, 0),
                LocalDateTime.of(2024, 2, 14, 14, 45),
                8.2)
        );
        
        when(deliveryService.getDeliveriesForDasher(anyString(), any(LocalDate.class)))
            .thenReturn(deliveries);
        
        // ACT
        PayoutResponse response = payoutService.calculatePayout(request);
        
        // ASSERT
        assertNotNull(response);
        assertEquals("dasher-123", response.getDasherId());
        assertEquals(testDate, response.getDate());
        assertEquals(2, response.getTotalDeliveries());
        assertEquals(75, response.getTotalMinutes());
        assertEquals(13.7, response.getTotalDistance(), 0.01);
        assertEquals(new BigDecimal("20.00"), response.getBasePay());
        assertEquals(new BigDecimal("37.50"), response.getTimePay());
        assertEquals(new BigDecimal("13.70"), response.getDistancePay());
        assertEquals(new BigDecimal("71.20"), response.getTotalPayout());
    }
    
    @Test
    @DisplayName("Should calculate payout correctly with peak hours")
    void testCalculatePayoutWithPeakHours() {
        // ARRANGE
        List<Delivery> deliveries = List.of(
            new Delivery("d1",
                LocalDateTime.of(2024, 2, 14, 10, 0),
                LocalDateTime.of(2024, 2, 14, 10, 30),
                5.5),
            new Delivery("d2",
                LocalDateTime.of(2024, 2, 14, 19, 0),
                LocalDateTime.of(2024, 2, 14, 19, 30),
                3.5)
        );
        
        when(deliveryService.getDeliveriesForDasher(anyString(), any(LocalDate.class)))
            .thenReturn(deliveries);
        
        // ACT
        PayoutResponse response = payoutService.calculatePayout(request);
        
        // ASSERT
        assertEquals(2, response.getTotalDeliveries());
        assertEquals(60, response.getTotalMinutes());
        assertEquals(new BigDecimal("37.50"), response.getTimePay());
        assertEquals(new BigDecimal("22.50"), response.getPeakTimePay());
        assertEquals(new BigDecimal("66.50"), response.getTotalPayout());
    }
    
    @Test
    @DisplayName("Should correctly identify peak hours")
    void testIsPeakHour() {
        List<Delivery> peakDeliveries = List.of(
            new Delivery("d1",
                LocalDateTime.of(2024, 2, 14, 18, 0),
                LocalDateTime.of(2024, 2, 14, 18, 30),
                1.0),
            new Delivery("d2",
                LocalDateTime.of(2024, 2, 14, 19, 30),
                LocalDateTime.of(2024, 2, 14, 20, 0),
                1.0),
            new Delivery("d3",
                LocalDateTime.of(2024, 2, 14, 20, 0),
                LocalDateTime.of(2024, 2, 14, 20, 30),
                1.0)
        );
        
        when(deliveryService.getDeliveriesForDasher(anyString(), any(LocalDate.class)))
            .thenReturn(peakDeliveries);
        
        PayoutResponse response = payoutService.calculatePayout(request);
        
        // 90 minutos × 0.50 × 1.5 = 67.50
        assertEquals(new BigDecimal("67.50"), response.getPeakTimePay());
    }
    
    @Test
    @DisplayName("Should correctly identify non-peak hours")
    void testIsNotPeakHour() {
        List<Delivery> normalDeliveries = List.of(
            new Delivery("d1",
                LocalDateTime.of(2024, 2, 14, 17, 59),
                LocalDateTime.of(2024, 2, 14, 18, 29),
                1.0),
            new Delivery("d2",
                LocalDateTime.of(2024, 2, 14, 21, 0),
                LocalDateTime.of(2024, 2, 14, 21, 30),
                1.0)
        );
        
        when(deliveryService.getDeliveriesForDasher(anyString(), any(LocalDate.class)))
            .thenReturn(normalDeliveries);
        
        PayoutResponse response = payoutService.calculatePayout(request);
        
        assertEquals(new BigDecimal("0.00"), response.getPeakTimePay());
        assertEquals(new BigDecimal("30.00"), response.getTimePay());
    }
}
