package com.realkoreatravel.ai.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** OpenAI API 접속과 재시도 정책을 외부 설정으로 관리한다. */
@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
        String apiKey,
        String baseUrl,
        String model,
        Duration connectTimeout,
        Duration readTimeout,
        int maxAttempts,
        Duration retryBackoff
) {
}
