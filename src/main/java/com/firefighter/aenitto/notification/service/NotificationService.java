package com.firefighter.aenitto.notification.service;

public interface NotificationService {
	void sendMessage(String targetToken, String title, String body, String roomId);
}
