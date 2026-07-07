package com.travelai.trip.dto;

import com.travelai.trip.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private Long id;
    private Long userId;
    private String name;
    private String destination;
    private BigDecimal budget;
    private Integer days;
    private LocalDate startDate;
    private String description;
    private Integer status;
    private String statusText;
    private String shareToken;
    private Integer shareStatus;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TripDayResponse> tripDays;
    private List<TripBudgetResponse> budgetDetails;

    public static TripResponse fromEntity(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .userId(trip.getUserId())
                .name(trip.getName())
                .destination(trip.getDestination())
                .budget(trip.getBudget())
                .days(trip.getDays())
                .startDate(trip.getStartDate())
                .description(trip.getDescription())
                .status(trip.getStatus())
                .statusText(getStatusText(trip.getStatus()))
                .shareToken(trip.getShareToken())
                .shareStatus(trip.getShareStatus())
                .version(trip.getVersion())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }

    private static String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "DRAFT";
            case 1: return "ACTIVE";
            case 2: return "COMPLETED";
            case 3: return "CANCELLED";
            default: return "UNKNOWN";
        }
    }
}