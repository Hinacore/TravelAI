package com.travelai.trip.repository;

import com.travelai.trip.entity.TripBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripBudgetRepository extends JpaRepository<TripBudget, Long> {

    List<TripBudget> findByTripId(Long tripId);

    void deleteByTripId(Long tripId);
}