package com.travelai.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AITripResponse {

    private Boolean success;
    private String city;
    private Integer days;
    private BigDecimal totalBudget;
    private List<DailyItinerary> dailyItinerary;
    private BudgetBreakdown budgetBreakdown;
    private List<String> tips;
    private List<String> warnings;

    private String tripName;
    private List<AIDayPlan> dayPlans;
    private List<AIBudgetDetail> budgetDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyItinerary {
        private Integer day;
        private String date;
        private Activity morning;
        private Activity afternoon;
        private Activity evening;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Activity {
        private String spot;
        private String duration;
        private String ticket;
        private String transportation;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetBreakdown {
        private BigDecimal accommodation;
        private BigDecimal food;
        private BigDecimal transportation;
        private BigDecimal tickets;
        private BigDecimal other;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIDayPlan {
        private Integer dayNumber;
        private String summary;
        private AIActivity morning;
        private AIActivity afternoon;
        private AIActivity evening;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIActivity {
        private String title;
        private String location;
        private String duration;
        private BigDecimal ticketPrice;
        private String transportation;
        private String description;
        private BigDecimal budget;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIBudgetDetail {
        private String category;
        private BigDecimal amount;
        private String description;
    }
}
