package net.sasakonnect.wallet.repository;
import java.util.List;

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
	
	
	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.messageRead IS NULL")
	Page<Notifications> findUnreadNotifications(@Param("user") User user, Pageable pageable);
	
	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.messageRead IS NULL")
	List<Notifications> findAllUnreadNotifications(@Param("user") User user);
	
	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR  n.targetType = 'GENERAL') AND n.messageRead IS NOT NULL")
	Page<Notifications> findReadNotifications(@Param("user") User user, Pageable pageable);
}
