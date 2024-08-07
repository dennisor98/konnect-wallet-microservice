package net.sasakonnect.wallet.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.repository.NotificationsRepository;

public class NotificationsCleaner {
	@Autowired
	NotificationsRepository notificationRepository;
	
	@Scheduled(cron = "29 23 * * ?")
	public void clearExpiredMessages() {
		List<Notifications> expiredNotifications =  this.notificationRepository.findExpiredNotifications();
		if(!expiredNotifications.isEmpty()) {
			this.notificationRepository.deleteAll(expiredNotifications);
		}
	}
}
