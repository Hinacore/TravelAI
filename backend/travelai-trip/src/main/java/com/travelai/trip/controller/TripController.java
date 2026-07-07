package com.travelai.trip.controller;

import com.travelai.common.result.Result;
import com.travelai.trip.dto.CreateTripRequest;
import com.travelai.trip.dto.TripResponse;
import com.travelai.trip.service.TripService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<Result<TripResponse>> createTrip(
            Authentication authentication,
            @Valid @RequestBody CreateTripRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TripResponse response = tripService.createTrip(userId, request);
        return ResponseEntity.ok(Result.success("行程创建成功", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<TripResponse>> getTripById(@PathVariable Long id) {
        TripResponse response = tripService.getTripById(id);
        return ResponseEntity.ok(Result.success(response));
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<Result<TripResponse>> getTripByShareToken(@PathVariable String token) {
        TripResponse response = tripService.getTripByShareToken(token);
        return ResponseEntity.ok(Result.success(response));
    }

    @GetMapping
    public ResponseEntity<Result<Page<TripResponse>>> getTripByUser(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        Long userId = (Long) authentication.getPrincipal();
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TripResponse> response = tripService.getTripsByUser(userId, keyword, pageable);
        return ResponseEntity.ok(Result.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<TripResponse>> updateTrip(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody CreateTripRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TripResponse response = tripService.updateTrip(id, userId, request);
        return ResponseEntity.ok(Result.success("行程更新成功", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteTrip(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        tripService.deleteTrip(id, userId);
        return ResponseEntity.ok(Result.success("行程删除成功", null));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Result<TripResponse>> generateShareToken(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        TripResponse response = tripService.generateShareToken(id, userId);
        return ResponseEntity.ok(Result.success("分享链接生成成功", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Result<TripResponse>> updateTripStatus(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Integer status) {
        Long userId = (Long) authentication.getPrincipal();
        TripResponse response = tripService.updateStatus(id, userId, status);
        return ResponseEntity.ok(Result.success("行程状态更新成功", response));
    }
}