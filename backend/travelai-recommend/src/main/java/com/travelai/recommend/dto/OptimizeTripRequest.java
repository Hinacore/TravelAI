package com.travelai.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizeTripRequest {

    private Long tripId;
    private String destination;
    private BigDecimal budget;
    private Integer days;
    private String modifications;
    private List<String> preferences;
}