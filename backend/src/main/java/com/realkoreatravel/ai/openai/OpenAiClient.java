package com.realkoreatravel.ai.openai;

import com.realkoreatravel.ai.config.OpenAiProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/** 애플리케이션의 GPT 기능이 공통으로 사용하는 OpenAI Responses API 클라이언트다. */
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestClient restClient;
    private final OpenAiProperties properties;

    /**
     * 프롬프트를 OpenAI Responses API에 전달하고 첫 번째 텍스트 응답을 반환한다.
     *
     * <p>빈 프롬프트는 호출 전에 거부하며, 429·5xx·네트워크 오류는 설정된 횟수만큼 재시도한다.
     * 재시도할 수 없는 오류나 최대 시도 횟수를 초과한 오류는 공통 클라이언트 예외로 변환한다.</p>
     */
    public String generate(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new IllegalArgumentException("GPT 프롬프트는 비어 있을 수 없습니다.");
        }

        int maxAttempts = Math.max(properties.maxAttempts(), 1);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                OpenAiResponse response = request(prompt);
                return response.extractText();
            } catch (RestClientResponseException exception) {
                if (!isRetryable(exception.getStatusCode()) || attempt == maxAttempts) {
                    throw new OpenAiClientException("OpenAI API 호출에 실패했습니다.", exception);
                }
                waitBeforeRetry();
            } catch (ResourceAccessException exception) {
                if (attempt == maxAttempts) {
                    throw new OpenAiClientException("OpenAI API 연결에 실패했습니다.", exception);
                }
                waitBeforeRetry();
            }
        }

        throw new OpenAiClientException("OpenAI API 호출에 실패했습니다.");
    }

    /**
     * 모델명과 프롬프트로 Responses API 요청을 생성하고 응답 JSON을 역직렬화한다.
     *
     * <p>응답 객체가 없거나 텍스트 콘텐츠를 포함하지 않으면 후속 기능이 잘못된 결과를 사용하지
     * 않도록 즉시 클라이언트 예외를 발생시킨다.</p>
     */
    private OpenAiResponse request(String prompt) {
        OpenAiResponse response = restClient.post()
                .uri("/v1/responses")
                .body(new OpenAiRequest(properties.model(), prompt, false))
                .retrieve()
                .body(OpenAiResponse.class);

        if (response == null || !StringUtils.hasText(response.extractText())) {
            throw new OpenAiClientException("OpenAI API 응답에 텍스트가 없습니다.");
        }
        return response;
    }

    /**
     * 요청을 다시 보내도 성공할 가능성이 있는 일시적 오류인지 판단한다.
     *
     * <p>호출 제한 초과인 429와 서버 측 일시 장애인 5xx만 재시도하고, 잘못된 요청이나 인증 오류는
     * 즉시 실패시킨다.</p>
     */
    private boolean isRetryable(HttpStatusCode statusCode) {
        return statusCode.value() == 429 || statusCode.is5xxServerError();
    }

    /**
     * 동일 요청을 연속해서 보내지 않도록 다음 재시도 전 설정된 대기 시간을 적용한다.
     *
     * <p>대기 중 스레드가 중단되면 중단 상태를 복원하고 재시도 중단 예외로 변환한다.</p>
     */
    private void waitBeforeRetry() {
        try {
            Thread.sleep(properties.retryBackoff().toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OpenAiClientException("OpenAI API 재시도가 중단되었습니다.", exception);
        }
    }

    /** Responses API에 전달할 최소 요청 구조다. */
    private record OpenAiRequest(String model, String input, boolean store) {
    }

    /** Responses API에서 필요한 출력 텍스트 구조만 표현한다. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenAiResponse(List<OutputItem> output) {

        /**
         * Responses API의 여러 출력 항목 중 비어 있지 않은 첫 번째 텍스트 콘텐츠를 추출한다.
         * 출력이 없거나 텍스트가 없으면 null을 반환해 호출 계층에서 응답 오류로 처리하게 한다.
         */
        private String extractText() {
            if (output == null) {
                return null;
            }
            return output.stream()
                    .filter(item -> item.content() != null)
                    .flatMap(item -> item.content().stream())
                    .map(OutputContent::text)
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .orElse(null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputItem(List<OutputContent> content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputContent(String text) {
    }
}
