package com.travelai.recommend.client;

import com.travelai.common.config.EnvConfig;
import com.travelai.recommend.dto.AITripResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final EnvConfig envConfig;

    private static final String MODEL_NAME = "deepseek-v4-flash";

    public AITripResponse generateTripPlan(String destination, String budget, String days, String startDate, String preferences) {
        String prompt = buildTravelPrompt(destination, budget, days, preferences);
        String response = callAI(prompt);
        return parseTripResponse(response);
    }

    public AITripResponse optimizeTripPlan(Long tripId, String modifications, String preferences) {
        String prompt = buildOptimizePrompt(tripId, modifications, preferences);
        String response = callAI(prompt);
        return parseTripResponse(response);
    }

    private String callAI(String prompt) {
        if (!envConfig.hasApiKey()) {
            log.warn("DeepSeek API key not configured, using mock response");
            return generateMockTripResponse();
        }

        try {
            String requestBody = "{\"model\": \"" + MODEL_NAME + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}], \"temperature\": 0.7}";
            
            String response = webClient.post()
                    .uri(envConfig.getDeepseekBaseUrl() + "/chat/completions")
                    .header("Authorization", "Bearer " + envConfig.getDeepseekApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .onErrorResume(e -> {
                        log.warn("DeepSeek API调用失败，使用模拟响应: {}", e.getMessage());
                        return Mono.just(generateMockTripResponse());
                    })
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("DeepSeek API返回为空，使用模拟响应");
                return generateMockTripResponse();
            }

            String content = extractContentFromResponse(response);
            if (content == null || content.isEmpty()) {
                log.warn("提取内容失败，使用模拟响应");
                return generateMockTripResponse();
            }

            log.debug("AI response: {}", content);
            return content;
        } catch (Exception e) {
            log.error("AI调用失败: {}", e.getMessage());
            return generateMockTripResponse();
        }
    }

    private String extractContentFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode choice = choices.get(0);
                JsonNode message = choice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析DeepSeek响应失败: {}", e.getMessage());
        }
        return response;
    }

    private String buildTravelPrompt(String city, String budget, String days, String preferences) {
        return "你是一个专业的旅游规划师，擅长根据用户的需求生成详细的旅行行程。\n\n" +
                "请根据以下信息为用户生成一份详细的旅游规划：\n" +
                "- 目的地城市：" + city + "\n" +
                "- 预算：" + budget + "元\n" +
                "- 旅行天数：" + days + "天\n" +
                "- 旅行偏好：" + (preferences != null && !preferences.isEmpty() ? preferences : "无") + "\n\n" +
                "要求：\n" +
                "1. 每天的行程安排（上午、下午、晚上）\n" +
                "2. 每个景点的详细介绍\n" +
                "3. 交通建议\n" +
                "4. 预算分配明细\n" +
                "5. 注意事项\n\n" +
                "请以JSON格式输出，结构如下：\n" +
                "{\n" +
                "  \"success\": true,\n" +
                "  \"city\": \"城市名\",\n" +
                "  \"days\": 天数,\n" +
                "  \"totalBudget\": 总预算,\n" +
                "  \"dailyItinerary\": [\n" +
                "    {\n" +
                "      \"day\": 1,\n" +
                "      \"date\": \"第1天\",\n" +
                "      \"morning\": {\n" +
                "        \"spot\": \"景点名称\",\n" +
                "        \"duration\": \"游览时长\",\n" +
                "        \"ticket\": \"门票价格\",\n" +
                "        \"transportation\": \"交通方式\",\n" +
                "        \"description\": \"景点介绍\"\n" +
                "      },\n" +
                "      \"afternoon\": {\n" +
                "        \"spot\": \"景点名称\",\n" +
                "        \"duration\": \"游览时长\",\n" +
                "        \"ticket\": \"门票价格\",\n" +
                "        \"transportation\": \"交通方式\",\n" +
                "        \"description\": \"景点介绍\"\n" +
                "      },\n" +
                "      \"evening\": {\n" +
                "        \"spot\": \"活动名称\",\n" +
                "        \"duration\": \"活动时长\",\n" +
                "        \"ticket\": \"费用\",\n" +
                "        \"transportation\": \"交通方式\",\n" +
                "        \"description\": \"活动介绍\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"budgetBreakdown\": {\n" +
                "    \"accommodation\": 住宿费用,\n" +
                "    \"food\": 餐饮费用,\n" +
                "    \"transportation\": 交通费用,\n" +
                "    \"tickets\": 门票费用,\n" +
                "    \"other\": 其他费用\n" +
                "  },\n" +
                "  \"tips\": [\"提示1\", \"提示2\", \"提示3\"],\n" +
                "  \"warnings\": [\"注意事项1\", \"注意事项2\"]\n" +
                "}\n\n" +
                "请确保JSON格式正确，可以被解析。";
    }

    private String buildOptimizePrompt(Long tripId, String modifications, String preferences) {
        return "你是一个专业的旅游规划师，擅长根据用户的需求优化旅行行程。\n\n" +
                "请根据以下信息优化行程：\n" +
                "- 行程ID：" + tripId + "\n" +
                "- 修改需求：" + modifications + "\n" +
                "- 用户偏好：" + (preferences != null && !preferences.isEmpty() ? preferences : "无") + "\n\n" +
                "请以JSON格式输出优化后的行程规划：\n" +
                "{\n" +
                "  \"success\": true,\n" +
                "  \"city\": \"城市名\",\n" +
                "  \"days\": 天数,\n" +
                "  \"totalBudget\": 总预算,\n" +
                "  \"dailyItinerary\": [\n" +
                "    {\n" +
                "      \"day\": 1,\n" +
                "      \"date\": \"第1天\",\n" +
                "      \"morning\": {...},\n" +
                "      \"afternoon\": {...},\n" +
                "      \"evening\": {...}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"budgetBreakdown\": {...},\n" +
                "  \"tips\": [...],\n" +
                "  \"warnings\": [...]\n" +
                "}\n\n" +
                "请确保JSON格式正确，可以被解析。";
    }

    private AITripResponse parseTripResponse(String response) {
        if (response == null || response.isEmpty()) {
            log.warn("AI返回内容为空，使用模拟响应");
            response = generateMockTripResponse();
        }

        try {
            String jsonResponse = extractJson(response);
            AITripResponse aiResponse = objectMapper.readValue(jsonResponse, AITripResponse.class);

            if (aiResponse.getDailyItinerary() != null && !aiResponse.getDailyItinerary().isEmpty()) {
                aiResponse = convertToCompatibleFormat(aiResponse);
            }

            return aiResponse;
        } catch (Exception e) {
            log.error("解析AI响应失败: {}，使用模拟响应", e.getMessage());
            try {
                return objectMapper.readValue(generateMockTripResponse(), AITripResponse.class);
            } catch (Exception ex) {
                log.error("解析模拟响应失败: {}", ex.getMessage());
                return createDefaultTripResponse();
            }
        }
    }

    private AITripResponse convertToCompatibleFormat(AITripResponse response) {
        AITripResponse result = new AITripResponse();
        result.setTripName(response.getCity() + response.getDays() + "日游");
        result.setTips(response.getTips());

        List<AITripResponse.AIDayPlan> dayPlans = new ArrayList<>();
        for (AITripResponse.DailyItinerary itinerary : response.getDailyItinerary()) {
            AITripResponse.AIDayPlan dayPlan = new AITripResponse.AIDayPlan();
            dayPlan.setDayNumber(itinerary.getDay());
            dayPlan.setSummary(itinerary.getDate());

            dayPlan.setMorning(convertActivity(itinerary.getMorning()));
            dayPlan.setAfternoon(convertActivity(itinerary.getAfternoon()));
            dayPlan.setEvening(convertActivity(itinerary.getEvening()));

            dayPlans.add(dayPlan);
        }
        result.setDayPlans(dayPlans);

        List<AITripResponse.AIBudgetDetail> budgetDetails = new ArrayList<>();
        if (response.getBudgetBreakdown() != null) {
            AITripResponse.BudgetBreakdown breakdown = response.getBudgetBreakdown();
            addBudgetDetail(budgetDetails, "住宿", breakdown.getAccommodation(), "住宿费用");
            addBudgetDetail(budgetDetails, "餐饮", breakdown.getFood(), "餐饮费用");
            addBudgetDetail(budgetDetails, "交通", breakdown.getTransportation(), "交通费用");
            addBudgetDetail(budgetDetails, "门票", breakdown.getTickets(), "门票费用");
            addBudgetDetail(budgetDetails, "其他", breakdown.getOther(), "其他费用");
        }
        result.setBudgetDetails(budgetDetails);

        return result;
    }

    private AITripResponse.AIActivity convertActivity(AITripResponse.Activity activity) {
        if (activity == null) return null;

        AITripResponse.AIActivity aiActivity = new AITripResponse.AIActivity();
        aiActivity.setTitle(activity.getSpot());
        aiActivity.setDuration(activity.getDuration());
        aiActivity.setTransportation(activity.getTransportation());
        aiActivity.setDescription(activity.getDescription());

        try {
            String ticket = activity.getTicket();
            if (ticket != null && !ticket.isEmpty() && !ticket.contains("免费") && !ticket.contains("无")) {
                ticket = ticket.replace("元", "").replace("¥", "").trim();
                aiActivity.setTicketPrice(new BigDecimal(ticket));
            } else {
                aiActivity.setTicketPrice(BigDecimal.ZERO);
            }
        } catch (Exception e) {
            aiActivity.setTicketPrice(BigDecimal.ZERO);
        }

        return aiActivity;
    }

    private void addBudgetDetail(List<AITripResponse.AIBudgetDetail> details, String category, BigDecimal amount, String description) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            AITripResponse.AIBudgetDetail detail = new AITripResponse.AIBudgetDetail();
            detail.setCategory(category);
            detail.setAmount(amount);
            detail.setDescription(description);
            details.add(detail);
        }
    }

    private AITripResponse createDefaultTripResponse() {
        try {
            return objectMapper.readValue(generateMockTripResponse(), AITripResponse.class);
        } catch (Exception e) {
            AITripResponse defaultResponse = new AITripResponse();
            defaultResponse.setTripName("默认行程");
            return defaultResponse;
        }
    }

    private String extractJson(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start == -1 || end == -1 || start > end) {
            return response;
        }

        return response.substring(start, end + 1);
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String generateMockTripResponse() {
        return "{" +
                "\"success\": true," +
                "\"city\": \"北京\"," +
                "\"days\": 3," +
                "\"totalBudget\": 3000," +
                "\"dailyItinerary\": [" +
                "{" +
                "\"day\": 1," +
                "\"date\": \"第1天\"," +
                "\"morning\": {" +
                "\"spot\": \"故宫博物院\"," +
                "\"duration\": \"3小时\"," +
                "\"ticket\": \"60\"," +
                "\"transportation\": \"地铁1号线\"," +
                "\"description\": \"明清两代皇家宫殿，世界文化遗产\"" +
                "}," +
                "\"afternoon\": {" +
                "\"spot\": \"天安门广场\"," +
                "\"duration\": \"1.5小时\"," +
                "\"ticket\": \"0\"," +
                "\"transportation\": \"步行\"," +
                "\"description\": \"世界最大的城市广场\"" +
                "}," +
                "\"evening\": {" +
                "\"spot\": \"全聚德烤鸭\"," +
                "\"duration\": \"2小时\"," +
                "\"ticket\": \"200\"," +
                "\"transportation\": \"出租车\"," +
                "\"description\": \"品尝正宗北京烤鸭\"" +
                "}" +
                "}," +
                "{" +
                "\"day\": 2," +
                "\"date\": \"第2天\"," +
                "\"morning\": {" +
                "\"spot\": \"颐和园\"," +
                "\"duration\": \"3.5小时\"," +
                "\"ticket\": \"30\"," +
                "\"transportation\": \"地铁4号线\"," +
                "\"description\": \"中国现存规模最大的皇家园林\"" +
                "}," +
                "\"afternoon\": {" +
                "\"spot\": \"奥林匹克公园\"," +
                "\"duration\": \"2.5小时\"," +
                "\"ticket\": \"0\"," +
                "\"transportation\": \"地铁8号线\"," +
                "\"description\": \"参观鸟巢和水立方\"" +
                "}," +
                "\"evening\": {" +
                "\"spot\": \"王府井步行街\"," +
                "\"duration\": \"2.5小时\"," +
                "\"ticket\": \"0\"," +
                "\"transportation\": \"地铁1号线\"," +
                "\"description\": \"北京最繁华的商业街\"" +
                "}" +
                "}," +
                "{" +
                "\"day\": 3," +
                "\"date\": \"第3天\"," +
                "\"morning\": {" +
                "\"spot\": \"八达岭长城\"," +
                "\"duration\": \"4小时\"," +
                "\"ticket\": \"40\"," +
                "\"transportation\": \"旅游专线\"," +
                "\"description\": \"万里长城最著名的一段\"" +
                "}," +
                "\"afternoon\": {" +
                "\"spot\": \"长城脚下美食\"," +
                "\"duration\": \"1.5小时\"," +
                "\"ticket\": \"100\"," +
                "\"transportation\": \"步行\"," +
                "\"description\": \"品尝农家菜\"" +
                "}," +
                "\"evening\": {" +
                "\"spot\": \"返程准备\"," +
                "\"duration\": \"3小时\"," +
                "\"ticket\": \"100\"," +
                "\"transportation\": \"出租车\"," +
                "\"description\": \"整理行李，前往车站/机场\"" +
                "}" +
                "}" +
                "]," +
                "\"budgetBreakdown\": {" +
                "\"accommodation\": 1200," +
                "\"food\": 600," +
                "\"transportation\": 400," +
                "\"tickets\": 130," +
                "\"other\": 670" +
                "}," +
                "\"tips\": [\"建议提前预约故宫门票\", \"长城建议穿舒适的运动鞋\", \"注意防暑防晒\"]," +
                "\"warnings\": [\"注意保管个人财物\", \"遵守景区规定\"]" +
                "}";
    }
}
