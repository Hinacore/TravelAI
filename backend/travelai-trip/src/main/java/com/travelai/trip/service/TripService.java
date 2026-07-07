package com.travelai.trip.service;

import com.travelai.common.exception.BusinessException;
import com.travelai.trip.dto.*;
import com.travelai.trip.entity.*;
import com.travelai.trip.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripDayRepository tripDayRepository;
    private final TripActivityRepository tripActivityRepository;
    private final TripBudgetRepository tripBudgetRepository;

    @Transactional
    public TripResponse createTrip(Long userId, CreateTripRequest request) {
        Trip trip = Trip.builder()
                .userId(userId)
                .name(request.getName())
                .destination(request.getDestination())
                .budget(request.getBudget())
                .days(request.getDays())
                .startDate(request.getStartDate())
                .description(request.getDescription())
                .status(0)
                .shareStatus(0)
                .version(1)
                .build();

        Trip savedTrip = tripRepository.save(trip);

        if (request.getTripDays() != null) {
            saveTripDays(savedTrip.getId(), request.getTripDays());
        }

        if (request.getBudgetDetails() != null) {
            saveTripBudget(savedTrip.getId(), request.getBudgetDetails());
        }

        log.info("行程创建成功: {}", savedTrip.getName());
        return getTripResponse(savedTrip);
    }

    private void saveTripDays(Long tripId, List<TripDayRequest> tripDays) {
        for (TripDayRequest dayRequest : tripDays) {
            TripDay tripDay = TripDay.builder()
                    .tripId(tripId)
                    .dayNumber(dayRequest.getDayNumber())
                    .summary(dayRequest.getSummary())
                    .build();

            TripDay savedDay = tripDayRepository.save(tripDay);

            if (dayRequest.getActivities() != null) {
                saveTripActivities(savedDay.getId(), dayRequest.getActivities());
            }
        }
    }

    private void saveTripActivities(Long tripDayId, List<TripActivityRequest> activities) {
        for (TripActivityRequest activityRequest : activities) {
            TripActivity activity = TripActivity.builder()
                    .tripDayId(tripDayId)
                    .type(activityRequest.getType())
                    .title(activityRequest.getTitle())
                    .location(activityRequest.getLocation())
                    .duration(activityRequest.getDuration())
                    .ticketPrice(activityRequest.getTicketPrice())
                    .transportation(activityRequest.getTransportation())
                    .description(activityRequest.getDescription())
                    .budget(activityRequest.getBudget())
                    .build();

            tripActivityRepository.save(activity);
        }
    }

    private void saveTripBudget(Long tripId, List<TripBudgetRequest> budgetDetails) {
        for (TripBudgetRequest budgetRequest : budgetDetails) {
            TripBudget budget = TripBudget.builder()
                    .tripId(tripId)
                    .category(budgetRequest.getCategory())
                    .amount(budgetRequest.getAmount())
                    .description(budgetRequest.getDescription())
                    .build();

            tripBudgetRepository.save(budget);
        }
    }

    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "行程不存在"));
        return getTripResponse(trip);
    }

    public TripResponse getTripByShareToken(String shareToken) {
        Trip trip = tripRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new BusinessException(404, "分享链接无效"));

        if (trip.getShareStatus() != 1) {
            throw new BusinessException(403, "该行程未公开分享");
        }

        return getTripResponse(trip);
    }

    private TripResponse getTripResponse(Trip trip) {
        TripResponse response = TripResponse.fromEntity(trip);

        List<TripDay> tripDays = tripDayRepository.findByTripIdOrderByDayNumber(trip.getId());
        List<TripDayResponse> dayResponses = tripDays.stream()
                .map(day -> {
                    TripDayResponse dayResponse = TripDayResponse.fromEntity(day);
                    List<TripActivity> activities = tripActivityRepository.findByTripDayId(day.getId());
                    dayResponse.setActivities(activities.stream()
                            .map(TripActivityResponse::fromEntity)
                            .collect(Collectors.toList()));
                    return dayResponse;
                })
                .collect(Collectors.toList());

        response.setTripDays(dayResponses);

        List<TripBudget> budgets = tripBudgetRepository.findByTripId(trip.getId());
        response.setBudgetDetails(budgets.stream()
                .map(TripBudgetResponse::fromEntity)
                .collect(Collectors.toList()));

        return response;
    }

    public Page<TripResponse> getTripsByUser(Long userId, Pageable pageable) {
        return tripRepository.findByUserId(userId, pageable)
                .map(TripResponse::fromEntity);
    }

    public Page<TripResponse> getTripsByUser(Long userId, String keyword, Pageable pageable) {
        return tripRepository.findByUserIdAndKeyword(userId, keyword, pageable)
                .map(TripResponse::fromEntity);
    }

    @Transactional
    public TripResponse updateTrip(Long id, Long userId, CreateTripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "行程不存在"));

        if (!trip.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改此行程");
        }

        trip.setName(request.getName());
        trip.setDestination(request.getDestination());
        trip.setBudget(request.getBudget());
        trip.setDays(request.getDays());
        trip.setStartDate(request.getStartDate());
        trip.setDescription(request.getDescription());
        trip.setVersion(trip.getVersion() + 1);

        Trip updatedTrip = tripRepository.save(trip);

        if (request.getTripDays() != null) {
            tripActivityRepository.deleteByTripDayIdIn(
                    tripDayRepository.findByTripIdOrderByDayNumber(id)
                            .stream()
                            .map(TripDay::getId)
                            .collect(Collectors.toList())
            );
            tripDayRepository.deleteByTripId(id);
            saveTripDays(id, request.getTripDays());
        }

        if (request.getBudgetDetails() != null) {
            tripBudgetRepository.deleteByTripId(id);
            saveTripBudget(id, request.getBudgetDetails());
        }

        log.info("行程更新成功: {}", updatedTrip.getName());
        return getTripResponse(updatedTrip);
    }

    @Transactional
    public void deleteTrip(Long id, Long userId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "行程不存在"));

        if (!trip.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除此行程");
        }

        tripBudgetRepository.deleteByTripId(id);
        tripActivityRepository.deleteByTripDayIdIn(
                tripDayRepository.findByTripIdOrderByDayNumber(id)
                        .stream()
                        .map(TripDay::getId)
                        .collect(Collectors.toList())
        );
        tripDayRepository.deleteByTripId(id);
        tripRepository.delete(trip);

        log.info("行程删除成功: {}", trip.getName());
    }

    @Transactional
    public TripResponse updateStatus(Long id, Long userId, Integer status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "行程不存在"));

        if (!trip.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权修改此行程");
        }

        if (status != 0 && status != 1 && status != 2 && status != 3) {
            throw new BusinessException(400, "无效的状态值");
        }

        trip.setStatus(status);
        Trip updatedTrip = tripRepository.save(trip);

        log.info("行程状态更新成功: {} -> {}", id, status);
        return getTripResponse(updatedTrip);
    }

    @Transactional
    public TripResponse generateShareToken(Long id, Long userId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "行程不存在"));

        if (!trip.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权分享此行程");
        }

        String shareToken;
        do {
            shareToken = UUID.randomUUID().toString().replace("-", "");
        } while (tripRepository.existsByShareToken(shareToken));

        trip.setShareToken(shareToken);
        trip.setShareStatus(1);
        Trip updatedTrip = tripRepository.save(trip);

        log.info("行程分享链接生成成功: {}", shareToken);
        return TripResponse.fromEntity(updatedTrip);
    }
}