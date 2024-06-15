package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.NotificationReadId;
import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.NotificationsRead;
import net.sasakonnect.wallet.domain.User;

public interface NotificationsReadRepository  extends JpaRepository<NotificationsRead,NotificationReadId>{
    boolean existsByMessageAndUser(Notifications notification, User user);

}
