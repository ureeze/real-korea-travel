package com.realkoreatravel.auth.dto;

public record OAuthLoginResponse(
        Long memberId,
        String email,
        String name,
        String profileImageUrl
) {

    public static OAuthLoginResponse from(
            Long memberId,
            String email,
            String name,
            String profileImageUrl
    ) {
        return new OAuthLoginResponse(memberId, email, name, profileImageUrl);
    }
}
