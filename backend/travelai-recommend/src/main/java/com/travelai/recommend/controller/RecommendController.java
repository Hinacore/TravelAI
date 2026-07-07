package com.travelai.recommend.controller;

import com.travelai.common.result.Result;
import com.travelai.recommend.dto.AITripResponse;
import com.travelai.recommend.dto.GenerateTripRequest;
import com.travelai.recommend.dto.OptimizeTripRequest;
import com.travelai.recommend.service.RecommendService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/trip")
    public ResponseEntity<Result<AITripResponse>> generateTrip(
            @Valid @RequestBody GenerateTripRequest request) {
        Long userId = 0L;
        AITripResponse response = recommendService.generateTrip(userId, request);
        return ResponseEntity.ok(Result.success("行程规划生成成功", response));
    }

    @PostMapping("/trip/{id}/optimize")
    public ResponseEntity<Result<AITripResponse>> optimizeTrip(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody OptimizeTripRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        request.setTripId(id);
        AITripResponse response = recommendService.optimizeTrip(userId, id, request);
        return ResponseEntity.ok(Result.success("行程优化成功", response));
    }
}