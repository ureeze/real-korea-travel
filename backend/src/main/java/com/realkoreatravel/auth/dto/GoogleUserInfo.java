package com.realkoreatravel.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfo(
        String sub,
        String email,
        String name,
        String picture
) {
}
