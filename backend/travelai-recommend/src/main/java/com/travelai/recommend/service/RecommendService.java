package com.travelai.recommend.service;

import com.travelai.common.exception.BusinessException;
import com.travelai.recommend.client.AIClient;
import com.travelai.recommend.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final AIClient aiClient;

    public AITripResponse generateTrip(Long userId, GenerateTripRequest request) {
        try {
            String preferences = "";
            if (request.getPreferences() != null && !request.getPreferences().isEmpty()) {
                preferences = String.join(",", request.getPreferences());
            }

            AITripResponse aiResponse = aiClient.generateTripPlan(
                    request.getDestination(),
                    request.getBudget().toString(),
                    request.getDays().toString(),
                    request.getStartDate() != null ? request.getStartDate().toString() : "",
                    preferences
            );

            if (aiResponse == null) {
                log.warn("AI返回为空，使用模拟数据");
                aiResponse = aiClient.generateTripPlan("北京", "3000", "3", "", "");
            }

            log.info("AI行程生成成功，目的地: {}", request.getDestination());
            return aiResponse;
        } catch (Exception e) {
            log.error("AI行程生成失败: {}", e.getMessage());
            throw new BusinessException(500, "AI服务暂时不可用，请稍后重试");
        }
    }

    public AITripResponse optimizeTrip(Long userId, Long tripId, OptimizeTripRequest request) {
        try {
            AITripResponse aiResponse = aiClient.optimizeTripPlan(
                    tripId,
                    request.getModifications(),
                    request.getPreferences() != null ? String.join(",", request.getPreferences()) : ""
            );

            if (aiResponse == null) {
                log.warn("AI返回为空，使用模拟数据");
                aiResponse = aiClient.generateTripPlan("北京", "3000", "3", "", "");
            }

            log.info("AI行程优化成功，行程ID: {}", tripId);
            return aiResponse;
        } catch (Exception e) {
            log.error("AI行程优化失败: {}", e.getMessage());
            throw new BusinessException(500, "AI服务暂时不可用，请稍后重试");
        }
    }
}