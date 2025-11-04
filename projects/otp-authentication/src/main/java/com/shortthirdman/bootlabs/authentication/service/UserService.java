package com.shortthirdman.bootlabs.authentication.service;

import com.shortthirdman.bootlabs.authentication.dto.UserRegistrationRequest;
import com.shortthirdman.bootlabs.authentication.entity.Otp;
import com.shortthirdman.bootlabs.authentication.entity.User;
import com.shortthirdman.bootlabs.authentication.enums.OtpType;
import com.shortthirdman.bootlabs.authentication.enums.Role;
import com.shortthirdman.bootlabs.authentication.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final SmsService smsService;

    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.ROLE_USER);
        user.setVerified(false);

        return userRepository.save(user);
    }

    public void initiateEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Otp otp = otpService.createOtp(user, OtpType.EMAIL_VERIFICATION);
        emailService.sendOtpEmail(user.getEmail(), otp.getCode());
    }

    public void initiatePhoneVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPhoneNumber() == null) {
            throw new RuntimeException("Phone number not set");
        }

        Otp otp = otpService.createOtp(user, OtpType.PHONE_VERIFICATION);
        smsService.sendOtpSms(user.getPhoneNumber(), otp.getCode());
    }

    public boolean verifyEmailOtp(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isValid = otpService.validateOtp(user, code, OtpType.EMAIL_VERIFICATION);

        if (isValid) {
            user.setVerified(true);
            userRepository.save(user);
        }

        return isValid;
    }

    public boolean verifyPhoneOtp(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return otpService.validateOtp(user, code, OtpType.PHONE_VERIFICATION);
    }

    public User getUserByEmail(@NotBlank String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
