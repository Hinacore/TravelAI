package com.travelai.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDayRequest {

    private Integer dayNumber;
    private String summary;
    private List<TripActivityRequest> activities;
}