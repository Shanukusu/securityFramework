package ru.shanina.securityframework.core_iam.token;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Token Pair - Access Token + Refresh Token
 */
@Data
@AllArgsConstructor
public class TokenPair {
    private String accessToken;      // Short-lived (15 min)
    private String refreshToken;     // Long-lived (7 days)
    private long accessTokenExpiresIn;   // Секунд
    private long refreshTokenExpiresIn;  // Секунд
    private String tokenType;        // "Bearer"
}

