package com.realkoreatravel.ai.openai;

/** OpenAI API 호출과 응답 처리 중 발생한 외부 연동 오류를 표현한다. */
public class OpenAiClientException extends RuntimeException {

    public OpenAiClientException(String message) {
        super(message);
    }

    public OpenAiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
