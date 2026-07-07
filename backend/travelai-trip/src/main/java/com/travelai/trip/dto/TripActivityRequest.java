package com.travelai.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripActivityRequest {

    private String type;
    private String title;
    private String location;
    private String duration;
    private BigDecimal ticketPrice;
    private String transportation;
    private String description;
    private BigDecimal budget;
}