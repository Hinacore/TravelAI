package com.travelai.trip.dto;

import com.travelai.trip.entity.TripDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDayResponse {

    private Long id;
    private Long tripId;
    private Integer dayNumber;
    private String summary;

    private List<TripActivityResponse> activities;

    public static TripDayResponse fromEntity(TripDay tripDay) {
        return TripDayResponse.builder()
                .id(tripDay.getId())
                .tripId(tripDay.getTripId())
                .dayNumber(tripDay.getDayNumber())
                .summary(tripDay.getSummary())
                .build();
    }
}