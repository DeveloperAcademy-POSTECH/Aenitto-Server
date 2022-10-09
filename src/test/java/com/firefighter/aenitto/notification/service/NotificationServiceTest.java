package com.firefighter.aenitto.notification.service;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.notification.FcmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @InjectMocks
    private FcmService fcmService;


    @DisplayName("FCM access token 받기 - 성공")
    @Test
    void get_access_token_success() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {


        // then
        Method method = FcmService.class.getDeclaredMethod("getAccessToken");
        method.setAccessible(true);
        String result = (String) method.invoke(fcmService);
        assertThat(result).isNotNull();
    }

}
