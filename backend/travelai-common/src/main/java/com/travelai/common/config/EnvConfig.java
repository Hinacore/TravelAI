package com.travelai.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
@Configuration
public class EnvConfig {

    private String deepseekApiKey;
    private String deepseekBaseUrl;

    @Value("${ai.deepseek.key:}")
    private String configuredApiKey;

    @Value("${ai.deepseek.url:https://api.deepseek.com}")
    private String configuredBaseUrl;

    @PostConstruct
    public void init() {
        try {
            Path envPath = Paths.get(System.getProperty("user.dir")).getParent().getParent()
                    .resolve("AI_KEY").resolve("DEEPSEEK_API_KEY.env");
            
            if (Files.exists(envPath)) {
                log.info("Loading DeepSeek API key from: {}", envPath);
                Properties properties = new Properties();
                try (FileInputStream fis = new FileInputStream(envPath.toFile())) {
                    properties.load(fis);
                    this.deepseekApiKey = properties.getProperty("DEEPSEEK_API_KEY", "");
                    this.deepseekBaseUrl = properties.getProperty("base_url (OpenAI)", "https://api.deepseek.com");
                }
            } else {
                log.info("Env file not found, using configured values");
                this.deepseekApiKey = configuredApiKey;
                this.deepseekBaseUrl = configuredBaseUrl;
            }

            if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
                log.info("DeepSeek API key loaded successfully");
            } else {
                log.warn("DeepSeek API key not found, will use mock responses");
            }
        } catch (IOException e) {
            log.error("Failed to load DeepSeek API key: {}", e.getMessage());
            this.deepseekApiKey = configuredApiKey;
            this.deepseekBaseUrl = configuredBaseUrl;
        }
    }

    public String getDeepseekApiKey() {
        return deepseekApiKey;
    }

    public String getDeepseekBaseUrl() {
        return deepseekBaseUrl;
    }

    public boolean hasApiKey() {
        return deepseekApiKey != null && !deepseekApiKey.isEmpty();
    }
}
