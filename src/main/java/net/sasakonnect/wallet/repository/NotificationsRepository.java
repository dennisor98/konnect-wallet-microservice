package net.sasakonnect.wallet.repository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.User;
public interface NotificationsRepository extends JpaRepository<Notifications,String> {
	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.createdAt >= :userCreatedAt ORDER BY n.createdAt DESC ")
	Page<Notifications> findUserNotifications(@Param("user") User user,@Param("userCreatedAt") Date userCreatedAt,Pageable pageable);

	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.messageRead IS NULL AND n.createdAt >= :userCreatedAt  ORDER BY n.createdAt DESC")
	Page<Notifications> findUnreadNotifications(@Param("user") User user,@Param("userCreatedAt") Date userCreatedAt, Pageable pageable);

	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.messageRead IS NULL ORDER BY n.createdAt DESC")
	List<Notifications> findAllUnreadNotifications(@Param("user") User user);

	@Query("SELECT n FROM Notifications n WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND n.messageRead IS NOT NULL ORDER BY n.createdAt DESC")
	Page<Notifications> findReadNotifications(@Param("user") User user, Pageable pageable);

}
