package com.shortthirdman.bootlabs.authentication.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final Environment env;

    public void sendOtpSms(String phoneNumber, String otpCode) {
        var accountSid = env.getProperty("twilio.account.sid");
        var authToken = env.getProperty("twilio.auth.token");
        var twilioNumber = env.getProperty("twilio.phone.number");

        Twilio.init(accountSid, authToken);

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioNumber),
                "Your OTP code is: " + otpCode + ". This code will expire in 5 minutes."
        ).create();
    }
}
