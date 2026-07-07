package com.travelai.trip.repository;

import com.travelai.trip.entity.TripActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripActivityRepository extends JpaRepository<TripActivity, Long> {

    List<TripActivity> findByTripDayId(Long tripDayId);

    void deleteByTripDayId(Long tripDayId);

    void deleteByTripDayIdIn(List<Long> tripDayIds);
}