package com.lab13.delivery_api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Payout Controller Integration Tests")
public class PayoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCalculatePayoutSuccessfully() throws Exception{

        String requestBody = """
            {
                "dasherId": "dasher-123",
                "date": "2024-02-14"
            }            
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.dasherId").value("dasher-123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPayout").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalDeliveries").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.basePay").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.timePay").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.distancePay").exists());
    }

    @Test
    @DisplayName("Should return 400 when dasherId is null")
    void shouldReturn400WhenDasherIdIsNull() throws Exception {
        String requestBody = """
            {
                "dasherId": null,
                "date": "2024-02-14"
            }
            """;
        
        mockMvc.perform(post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.validationErrors").isArray());
    }
    
    @Test
    @DisplayName("Should return 400 when dasherId is blank")
    void shouldReturn400WhenDasherIdIsBlank() throws Exception {
        String requestBody = """
            {
                "dasherId": "",
                "date": "2024-02-14"
            }
            """;
        
        mockMvc.perform(post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.validationErrors").isArray());
    }
    
    @Test
    @DisplayName("Should return 400 when date is null")
    void shouldReturn400WhenDateIsNull() throws Exception {
        String requestBody = """
            {
                "dasherId": "dasher-123",
                "date": null
            }
            """;
        
        mockMvc.perform(post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Error"));
    }
    
    @Test
    @DisplayName("Should return 400 when date is in the future")
    void shouldReturn400WhenDateIsInFuture() throws Exception {
        String requestBody = """
            {
                "dasherId": "dasher-123",
                "date": "2099-12-31"
            }
            """;
        
        mockMvc.perform(post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Error"))
            .andExpect(jsonPath("$.validationErrors[0]").value(org.hamcrest.Matchers.containsString("date")));
    }
    
    @Test
    @DisplayName("Should return 400 when request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        String requestBody = """
            {
                "dasherId": "dasher-123"
                INVALID JSON
            }
            """;
        
        mockMvc.perform(post("/api/dasher/payout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }
}
