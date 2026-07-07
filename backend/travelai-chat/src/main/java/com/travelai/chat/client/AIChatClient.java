package com.travelai.chat.client;

import com.travelai.common.config.EnvConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIChatClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final EnvConfig envConfig;
    private final Random random = new Random();

    private static final String MODEL_NAME = "deepseek-v4-flash";

    public String chatWithAI(String userMessage, String context) {
        String prompt = buildChatPrompt(userMessage, context);

        if (!envConfig.hasApiKey()) {
            log.warn("DeepSeek API key not configured, using mock response");
            return generateMockResponse(userMessage);
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
                        return Mono.just(generateMockResponse(userMessage));
                    })
                    .block();

            if (response == null || response.isEmpty()) {
                log.warn("DeepSeek API返回为空，使用模拟响应");
                return generateMockResponse(userMessage);
            }

            String content = extractContentFromResponse(response);
            if (content == null || content.isEmpty()) {
                log.warn("提取内容失败，使用模拟响应");
                return generateMockResponse(userMessage);
            }

            log.debug("AI chat response: {}", content);
            return content;
        } catch (Exception e) {
            log.error("AI聊天调用失败: {}", e.getMessage());
            return generateMockResponse(userMessage);
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

    private String generateMockResponse(String userMessage) {
        String[] responses = {
                "您好！感谢您的提问。作为专业的旅游顾问，我很乐意为您提供帮助。关于您提到的\"" + userMessage + "\"，我建议您：提前做好行程规划，预订好住宿和交通，了解目的地的天气情况，准备好必要的证件和物品。祝您旅途愉快！",
                "很高兴为您服务！关于\"" + userMessage + "\"这个问题，我可以给您一些建议。首先，您可以通过旅游网站了解目的地的热门景点和当地特色美食。其次，建议您合理安排时间，避免行程过于紧凑。最后，出行前记得检查天气和交通状况。",
                "您好！针对\"" + userMessage + "\"，我有以下建议：1. 提前查询目的地的旅游攻略；2. 预订合适的住宿；3. 了解当地的交通方式；4. 准备好必要的物品。如果您需要更详细的信息，欢迎继续提问！",
                "感谢您的咨询！关于\"" + userMessage + "\"，我建议您：提前规划好路线，预订好门票，注意安全，尊重当地文化习俗。祝您旅途愉快！"
        };
        return responses[random.nextInt(responses.length)];
    }

    private String buildChatPrompt(String userMessage, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的旅游顾问AI助手，名为TravelAI。请根据用户的问题提供专业、友好的旅游建议。\n");
        prompt.append("以下是对话历史（如果有）：\n");
        prompt.append(context != null ? context : "无\n");
        prompt.append("\n用户当前问题：").append(userMessage).append("\n");
        prompt.append("\n请给出详细、实用的回答。");

        return prompt.toString();
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
}
