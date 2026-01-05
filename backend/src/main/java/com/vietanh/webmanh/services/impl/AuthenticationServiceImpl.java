package com.vietanh.webmanh.services.impl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.constants.EventTopic;
import com.vietanh.webmanh.dbs.postgres.models.*;
import com.vietanh.webmanh.dbs.postgres.repositories.*;
import com.vietanh.webmanh.dtos.events.UserForgotEvent;
import com.vietanh.webmanh.dtos.requests.*;
import com.vietanh.webmanh.dtos.responses.AuthenticationResponse;
import com.vietanh.webmanh.dtos.responses.IntrospectResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import com.vietanh.webmanh.dtos.events.UserCreatedEvent;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.UserMapper;
import com.vietanh.webmanh.services.AuthenticationService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.EventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    //repo
    UserRepository userRepository;
    DisabledTokenRepository disabledTokenRepository;
    RoleRepository roleRepository;
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    VerifyTokenRepository verifyTokenRepository;

    //other
    EventPublisher eventPublisher;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @NonFinal
    @Value("${app.jwt.secretKey}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${app.jwt.access-duration}")
    protected long ACCESS_DURATION;

    @NonFinal
    @Value("${app.jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${app.frontend.base-url}")
    private String baseUrl;

    @Override
    public UserResponse register(RegisterRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) throw new AppException(ErrorCode.USER_EXISTED);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        eventPublisher.publish(EventTopic.USER_CREATED.getTopicName(),
                new UserCreatedEvent(user.getUserId(), user.getEmail(), user.getUsername()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public IntrospectResponse introspect(TokenRequest request) {
        var token = request.getAccessToken();


        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().isValid(isValid).build();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
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

    @Override
    public void logout(TokenRequest request) throws ParseException {
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

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException {
        var signedJWT = verifyToken(request.getRefreshToken(), true);

        var userId = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var accessToken = generateToken(user, false);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .isAuthenticated(true)
                .build();
    }

    @Override
    public void forgetPassword(ForgetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User user = userOptional.get();

        String token = generateToken(user, false);
        Instant now = Instant.now();

        ResetPasswordToken resetPasswordToken = ResetPasswordToken.builder()
                .userId(user.getUserId())
                .hashedToken(token)
                .createdAt(now)
                .expiryAt(now.plusSeconds(10*60)) // 10 phút
                .build();

        resetPasswordTokenRepository.save(resetPasswordToken);

        String resetUrl = baseUrl + "/change-password/resetToken?token=" + token;
        // gửi mail
        eventPublisher.publish(EventTopic.USER_FORGOT_EVENT.getTopicName(),
                new UserForgotEvent(user.getEmail(), user.getUsername(), resetUrl));
    }

    @Override
    public void changePasswordWithResetToken(ChangePasswordWithTokenRequest request) throws ParseException {
        SignedJWT signedJWT = verifyToken(request.getResetToken(), false);
        Integer userId = Integer.parseInt(signedJWT.getJWTClaimsSet().getSubject());

        Optional<ResetPasswordToken> tokenOptional =
                resetPasswordTokenRepository.findLatestValidTokenByUserId(userId, Instant.now());
        if (tokenOptional.isEmpty()) throw new AppException(ErrorCode.TOKEN_NOT_EXISTED);

        ResetPasswordToken resetPasswordToken = tokenOptional.get();
        if (!resetPasswordToken.getHashedToken().equals(request.getResetToken()))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        resetPasswordToken.setUsed(true); // đánh dấu dc dùng

        resetPasswordTokenRepository.save(resetPasswordToken);
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if(userOptional.isEmpty()){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AppException(ErrorCode.INVALID_PASSWORD);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    // mail ảo => không có mail
    // mail real => ai xác thực cũng được => mail ông này đã xác thực
    // mục đích đã đạt được => không cần overthinking
    @Override
    public void verifyAccount(VerifyAccountRequest request){
        Optional<VerifyAccountToken> tokenOptional =
                verifyTokenRepository.findById(request.getVerifyToken());

        if (tokenOptional.isEmpty()) throw new AppException(ErrorCode.INVALID_TOKEN);
        VerifyAccountToken verifyAccountToken = tokenOptional.get();
        if(!verifyAccountToken.isValidToken()) throw new AppException(ErrorCode.INVALID_TOKEN);

        User user = userRepository
                .findById(tokenOptional.get().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(user.getIsVerified()) return;// đã xác thực thì thôi
        user.setIsVerified(true);
        userRepository.save(user);

        verifyTokenRepository.deleteById(verifyAccountToken.getVerifyToken());
    }

    private String generateToken(User user, boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Long duration = ACCESS_DURATION;
        if (isRefresh) {
            duration = REFRESHABLE_DURATION;
        }

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId().toString())
                .issuer("webmanh.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("id", user.getUserId().toString())
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

    private SignedJWT verifyToken(String token, boolean isRefresh) {
        try {
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = (isRefresh)
                    ? new Date(signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                    .toEpochMilli())
                    : signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean isVerified = signedJWT.verify(verifier);

            if (!(isVerified && expiryTime.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // logout token ?
            if (disabledTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            return signedJWT;

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
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