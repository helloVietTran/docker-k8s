package com.viettran.reading_story_web.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.viettran.reading_story_web.dto.request.AuthenticationRequest;
import com.viettran.reading_story_web.dto.request.IntrospectRequest;
import com.viettran.reading_story_web.dto.request.LogoutRequest;
import com.viettran.reading_story_web.dto.request.RefreshRequest;
import com.viettran.reading_story_web.dto.response.AuthenticationResponse;
import com.viettran.reading_story_web.dto.response.IntrospectResponse;
import com.viettran.reading_story_web.entity.mysql.DisabledToken;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.repository.jpa.DisabledTokenRepository;
import com.viettran.reading_story_web.repository.jpa.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    DisabledTokenRepository disabledTokenRepository;

    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${app.jwt.secretKey}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${app.jwt.access-duration}")
    protected long ACCESS_DURATION;

    @NonFinal
    @Value("${app.jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getAccessToken();

        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().isValid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isAuthenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var accessToken = generateToken(user, false);
        var refreshToken = generateToken(user, true);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signAccessToken = verifyToken(request.getAccessToken(), true);

            String jid = signAccessToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signAccessToken.getJWTClaimsSet().getExpirationTime();

            DisabledToken disabledToken =
                    DisabledToken.builder().id(jid).expiryTime(expiryTime).build();

            disabledTokenRepository.save(disabledToken);
        } catch (AppException exception) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getRefreshToken(), true);

        var userId = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var accessToken = generateToken(user, false);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .isAuthenticated(true)
                .build();
    }

    public String generateToken(User user, boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Long duration = ACCESS_DURATION;
        if (isRefresh) {
            duration = REFRESHABLE_DURATION;
        }

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("reading-story-web.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("id", user.getId())
                .claim("isVerified", user.getIsVerified())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) {
        try {
            // Khởi tạo verifier với secret key
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

            // Parse token thành SignedJWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Tính toán thời gian hết hạn
            Date expiryTime = (isRefresh)
                    ? new Date(signedJWT
                            .getJWTClaimsSet()
                            .getIssueTime()
                            .toInstant()
                            .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                            .toEpochMilli())
                    : signedJWT.getJWTClaimsSet().getExpirationTime();

            // Kiểm tra chữ ký của token
            boolean isVerified = signedJWT.verify(verifier);

            // Kiểm tra xem token có hết hạn không và chữ ký có hợp lệ không
            if (!(isVerified && expiryTime.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Kiểm tra nếu token đã bị vô hiệu hóa
            if (disabledTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Trả về signedJWT nếu không có lỗi
            return signedJWT;

        } catch (JOSEException | ParseException e) {
            // Xử lý khi có lỗi trong việc parse hoặc verify token
            log.error("Token verification failed", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            // Bắt các lỗi bất kỳ khác
            log.error("Unexpected error during token verification", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
