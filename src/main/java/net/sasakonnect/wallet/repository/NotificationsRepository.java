package net.sasakonnect.wallet.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.User;
public interface NotificationsRepository extends JpaRepository<Notifications,String> {
	
	@Query("SELECT n FROM Notifications n WHERE n.targetUser = :user OR n.targetType = 'GENERAL'")
	Page<Notifications> findUserNotifications(@Param("user") User user, Pageable pageable);
}
