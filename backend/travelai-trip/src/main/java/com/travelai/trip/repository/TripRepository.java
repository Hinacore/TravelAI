package com.travelai.trip.repository;

import com.travelai.trip.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findByUserId(Long userId, Pageable pageable);

    List<Trip> findByUserIdAndStatus(Long userId, Integer status);

    Optional<Trip> findByShareToken(String shareToken);

    boolean existsByShareToken(String shareToken);

    @Query("SELECT t FROM Trip t WHERE t.userId = :userId AND " +
           "(:keyword IS NULL OR :keyword = '' OR t.destination LIKE %:keyword% OR t.name LIKE %:keyword%)")
    Page<Trip> findByUserIdAndKeyword(@Param("userId") Long userId, 
                                      @Param("keyword") String keyword, 
                                      Pageable pageable);
}