package com.travelai.trip.dto;

import com.travelai.trip.entity.TripActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripActivityResponse {

    private Long id;
    private Long tripDayId;
    private String type;
    private String title;
    private String location;
    private String duration;
    private BigDecimal ticketPrice;
    private String transportation;
    private String description;
    private BigDecimal budget;

    public static TripActivityResponse fromEntity(TripActivity activity) {
        return TripActivityResponse.builder()
                .id(activity.getId())
                .tripDayId(activity.getTripDayId())
                .type(activity.getType())
                .title(activity.getTitle())
                .location(activity.getLocation())
                .duration(activity.getDuration())
                .ticketPrice(activity.getTicketPrice())
                .transportation(activity.getTransportation())
                .description(activity.getDescription())
                .budget(activity.getBudget())
                .build();
    }
}