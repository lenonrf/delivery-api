package com.lab13.delivery_api.service;

import java.math.BigDecimal;import java.math.RoundingMode;import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lab13.delivery_api.dto.PayoutRequest;
import com.lab13.delivery_api.dto.PayoutResponse;
import com.lab13.delivery_api.model.Delivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

    public final DeliveryService service;

    // Regras de Pagamento
    public static final BigDecimal BASE_PAY_PER_DELIVERY = new BigDecimal("10.00");
    public static final BigDecimal TIME_PAY_PER_MINUTE = new BigDecimal("0.50");
    public static final BigDecimal DISTANCE_PAY_PER_KM = new BigDecimal("1.00");
    public static final BigDecimal PEAK_HOURS_MULTIPLIER = new BigDecimal("1.5");

    public PayoutResponse calculatePayout(PayoutRequest request){

        log.info("Calculating payout for dasher: {} on date: {}", request.getDasherId(), request.getDate());

        List<Delivery> deliveries = service.getDeliveriesForDasher(
            request.getDasherId(), 
            request.getDate()
        );

        // Calcular Totais
        int totalDeliveries = deliveries.size();
        int normalMinutes = calculateMinutes(deliveries);
        int peakMinutes = calculatePeakMinutes(deliveries);
        int totalMinutes = normalMinutes + peakMinutes;
        double totalDistance = calculateTotalDistance(deliveries);
        
        // Calcular Pagamentos
        BigDecimal basePay = BASE_PAY_PER_DELIVERY.multiply(new BigDecimal(totalDeliveries)).setScale(2, RoundingMode.HALF_UP);

        // Time pay normal (minutos fora do pico)
        BigDecimal normalTimePay = TIME_PAY_PER_MINUTE.multiply(new BigDecimal(normalMinutes));

        // Peak time pay (minutos no pico × multiplicador)
        BigDecimal peakBaseTimePay = TIME_PAY_PER_MINUTE.multiply(new BigDecimal(peakMinutes));
        BigDecimal peakTimePay = peakBaseTimePay.multiply(PEAK_HOURS_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);

        // Time pay total
        BigDecimal timePay = normalTimePay.add(peakTimePay).setScale(2, RoundingMode.HALF_UP);

        BigDecimal distancePay = DISTANCE_PAY_PER_KM.multiply(new BigDecimal(totalDistance)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPayout = basePay.add(timePay).add(distancePay).setScale(2, RoundingMode.HALF_UP);

        log.info("Payout calculated - Total: {}", totalPayout);

        return PayoutResponse.builder()
            .dasherId(request.getDasherId())
            .date(request.getDate())
            .totalDeliveries(totalDeliveries)
            .totalMinutes(totalMinutes)
            .totalDistance(totalDistance)
            .basePay(basePay)
            .timePay(timePay)
            .peakTimePay(peakTimePay)
            .distancePay(distancePay)
            .totalPayout(totalPayout)
            .build(); 
    }

    private double calculateTotalDistance(List<Delivery> deliveries){
        return deliveries.stream()
            .mapToDouble(Delivery::getDistance)
            .sum();
    }

    private boolean isPeakHour(LocalDateTime time){
        int hour = time.getHour();
        return hour >= 18 && hour < 21;
    }

    private int calculateMinutes(List<Delivery> deliveries){

        return deliveries.stream()
            .mapToInt(d -> {
                LocalDateTime start = d.getStarTime();
                LocalDateTime end = d.getEndTime();

                if(!isPeakHour(start)){
                    return (int) Duration.between(start, end).toMinutes();
                }
                return 0;
            })
            .sum();

    }

    private int calculatePeakMinutes(List<Delivery> deliveries){

        return deliveries.stream()
            .mapToInt(d -> {
                LocalDateTime start = d.getStarTime();
                LocalDateTime end = d.getEndTime();

                if(isPeakHour(start)){
                    return (int) Duration.between(start, end).toMinutes();
                }
                return 0;
            })
            .sum();
    }
}
