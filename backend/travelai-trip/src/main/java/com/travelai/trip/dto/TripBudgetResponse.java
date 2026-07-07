package com.travelai.trip.dto;

import com.travelai.trip.entity.TripBudget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBudgetResponse {

    private Long id;
    private Long tripId;
    private String category;
    private BigDecimal amount;
    private String description;

    public static TripBudgetResponse fromEntity(TripBudget budget) {
        return TripBudgetResponse.builder()
                .id(budget.getId())
                .tripId(budget.getTripId())
                .category(budget.getCategory())
                .amount(budget.getAmount())
                .description(budget.getDescription())
                .build();
    }
}