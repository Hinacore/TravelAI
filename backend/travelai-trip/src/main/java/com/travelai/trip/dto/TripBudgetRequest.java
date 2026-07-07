package com.travelai.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripBudgetRequest {

    private String category;
    private BigDecimal amount;
    private String description;
}