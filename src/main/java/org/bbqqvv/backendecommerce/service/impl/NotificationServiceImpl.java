package org.bbqqvv.backendecommerce.service.impl;

import org.bbqqvv.backendecommerce.entity.Notification;
import org.bbqqvv.backendecommerce.repository.NotificationRepository;
import org.bbqqvv.backendecommerce.service.NotificationService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
	
    private NotificationRepository notificationRepository;
    
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
