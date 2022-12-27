package com.firefighter.aenitto.notification.service;

import java.io.IOException;

public interface NotificationService {
	void sendMessage(String targetToken, String title, String body, Long rooomId);
}
