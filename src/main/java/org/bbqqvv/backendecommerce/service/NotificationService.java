package org.bbqqvv.backendecommerce.service;


import org.bbqqvv.backendecommerce.entity.Notification;

import java.util.List;

public interface NotificationService {
	Notification createNotification(Notification notification);
    List<Notification> getNotificationsByUserId(Long userId);
}
