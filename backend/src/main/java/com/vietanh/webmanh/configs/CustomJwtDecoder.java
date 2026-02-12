package com.vietanh.webmanh.configs;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.SignedJWT;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    // exception không đi vào @controladvice
    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String BLACK_LIST_TOKEN_PREFIX = "blacklist:token:";
    static final String CREDENTIALS_UPDATED_PATTERN = "credentials_updated:%d";

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            String blacklistKey = BLACK_LIST_TOKEN_PREFIX + signedJWT.getJWTClaimsSet().getJWTID();

            if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                throw new JwtException("TOKEN_REVOKED");
            }

            // 1 thiết bị đổi mật khẩu, các thiết bị khác sẽ bị logout
            Integer userId = Integer.parseInt(signedJWT.getJWTClaimsSet().getSubject());
            String value = redisTemplate.opsForValue()
                    .get(String.format(CREDENTIALS_UPDATED_PATTERN, userId));

            Instant tokenIat = signedJWT.getJWTClaimsSet().getIssueTime().toInstant();

            if (value != null) {
                Instant passwordChangedAt = Instant.ofEpochMilli(Long.parseLong(value));
                if (tokenIat.isBefore(passwordChangedAt)) {
                    throw new JwtException("TOKEN_INVALIDATED");
                }
            }

            if (new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                throw new JwtException("TOKEN_EXPIRED");
            }

            return new Jwt(
                    token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());
        } catch (ParseException e) {
            throw new JwtException("INVALID_TOKEN", e);
        }
    }
}