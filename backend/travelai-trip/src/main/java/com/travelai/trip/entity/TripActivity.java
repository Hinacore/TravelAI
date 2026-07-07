package com.travelai.trip.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trip_activities", indexes = {
        @Index(name = "idx_trip_day_id", columnList = "trip_day_id")
})
public class TripActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_day_id", nullable = false)
    private Long tripDayId;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 200)
    private String location;

    @Column(length = 50)
    private String duration;

    @Column(name = "ticket_price", precision = 8, scale = 2)
    private BigDecimal ticketPrice;

    @Column(length = 200)
    private String transportation;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 8, scale = 2)
    private BigDecimal budget;
}