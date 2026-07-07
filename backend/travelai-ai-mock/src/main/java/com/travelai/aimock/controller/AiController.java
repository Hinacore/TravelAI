package com.travelai.aimock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        log.info("Received AI request: {}", prompt);

        if (prompt != null && prompt.contains("行程规划")) {
            return generateMockTripResponse();
        }

        if (prompt != null && (prompt.contains("旅游") || prompt.contains("旅行"))) {
            return generateMockChatResponse();
        }

        return "{\"response\": \"您好！我是TravelAI智能助手，请问有什么可以帮您的？\"}";
    }

    private String generateMockTripResponse() {
        return "{" +
            "\"tripName\": \"北京三日精华游\"," +
            "\"days\": [" +
                "{" +
                    "\"dayNumber\": 1," +
                    "\"summary\": \"历史文化探索日\"," +
                    "\"morning\": {" +
                        "\"title\": \"故宫博物院\"," +
                        "\"location\": \"北京市东城区景山前街4号\"," +
                        "\"duration\": \"3小时\"," +
                        "\"ticketPrice\": 60," +
                        "\"transportation\": \"地铁1号线\"," +
                        "\"description\": \"明清两代皇家宫殿，世界文化遗产\"," +
                        "\"budget\": 60" +
                    "}," +
                    "\"afternoon\": {" +
                        "\"title\": \"天安门广场\"," +
                        "\"location\": \"北京市东城区\"," +
                        "\"duration\": \"1.5小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"步行\"," +
                        "\"description\": \"世界最大的城市广场\"," +
                        "\"budget\": 0" +
                    "}," +
                    "\"evening\": {" +
                        "\"title\": \"全聚德烤鸭\"," +
                        "\"location\": \"北京市前门大街30号\"," +
                        "\"duration\": \"2小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"出租车\"," +
                        "\"description\": \"品尝正宗北京烤鸭\"," +
                        "\"budget\": 200" +
                    "}" +
                "}," +
                "{" +
                    "\"dayNumber\": 2," +
                    "\"summary\": \"皇家园林与现代都市日\"," +
                    "\"morning\": {" +
                        "\"title\": \"颐和园\"," +
                        "\"location\": \"北京市海淀区新建宫门路19号\"," +
                        "\"duration\": \"3.5小时\"," +
                        "\"ticketPrice\": 30," +
                        "\"transportation\": \"地铁4号线\"," +
                        "\"description\": \"中国现存规模最大、保存最完整的皇家园林\"," +
                        "\"budget\": 30" +
                    "}," +
                    "\"afternoon\": {" +
                        "\"title\": \"奥林匹克公园\"," +
                        "\"location\": \"北京市朝阳区科荟路33号\"," +
                        "\"duration\": \"2.5小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"地铁8号线\"," +
                        "\"description\": \"参观鸟巢和水立方\"," +
                        "\"budget\": 0" +
                    "}," +
                    "\"evening\": {" +
                        "\"title\": \"王府井步行街\"," +
                        "\"location\": \"北京市东城区王府井大街\"," +
                        "\"duration\": \"2.5小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"地铁1号线\"," +
                        "\"description\": \"北京最繁华的商业街\"," +
                        "\"budget\": 300" +
                    "}" +
                "}," +
                "{" +
                    "\"dayNumber\": 3," +
                    "\"summary\": \"长城一日游\"," +
                    "\"morning\": {" +
                        "\"title\": \"八达岭长城\"," +
                        "\"location\": \"北京市延庆区\"," +
                        "\"duration\": \"4小时\"," +
                        "\"ticketPrice\": 40," +
                        "\"transportation\": \"旅游专线\"," +
                        "\"description\": \"万里长城最著名的一段\"," +
                        "\"budget\": 40" +
                    "}," +
                    "\"afternoon\": {" +
                        "\"title\": \"长城脚下美食\"," +
                        "\"location\": \"八达岭景区附近\"," +
                        "\"duration\": \"1.5小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"步行\"," +
                        "\"description\": \"品尝农家菜\"," +
                        "\"budget\": 100" +
                    "}," +
                    "\"evening\": {" +
                        "\"title\": \"返程准备\"," +
                        "\"location\": \"酒店\"," +
                        "\"duration\": \"3小时\"," +
                        "\"ticketPrice\": 0," +
                        "\"transportation\": \"出租车\"," +
                        "\"description\": \"整理行李，前往车站/机场\"," +
                        "\"budget\": 100" +
                    "}" +
                "}" +
            "]," +
            "\"budgetDetails\": [" +
                "{\"category\": \"住宿\", \"amount\": 1200, \"description\": \"三星级酒店\"}," +
                "{\"category\": \"餐饮\", \"amount\": 600, \"description\": \"三餐及小吃\"}," +
                "{\"category\": \"交通\", \"amount\": 400, \"description\": \"地铁、公交、打车\"}," +
                "{\"category\": \"门票\", \"amount\": 130, \"description\": \"景点门票\"}," +
                "{\"category\": \"其他\", \"amount\": 670, \"description\": \"购物及杂项\"}" +
            "]," +
            "\"tips\": [\"建议提前预约故宫门票\", \"长城建议穿舒适的运动鞋\", \"注意防暑防晒\"]" +
        "}";
    }

    private String generateMockChatResponse() {
        return "您好！我是您的专属旅游顾问TravelAI。关于旅游，我可以为您提供以下帮助：\n\n1. 行程规划 - 根据您的目的地、预算和天数，为您生成详细的旅行计划\n\n2. 住宿推荐 - 根据您的偏好推荐合适的酒店或民宿\n\n3. 美食推荐 - 当地特色美食和必吃餐厅推荐\n\n4. 景点攻略 - 热门景点介绍、门票信息和游玩建议\n\n5. 旅行小贴士 - 出行准备、天气情况、当地文化习俗等\n\n请问您想去哪里旅游？或者有什么具体的问题想问我？";
    }
}