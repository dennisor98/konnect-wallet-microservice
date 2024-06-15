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
	@Query("SELECT n FROM Notifications n LEFT JOIN NotificationsRead nr ON nr.message = n WHERE (nr.user = :user OR n.targetType = 'GENERAL') AND n.createdAt >= :userCreatedAt ORDER BY n.createdAt DESC")
	Page<Notifications> findUserNotifications(@Param("user") User user,@Param("userCreatedAt") Date userCreatedAt,Pageable pageable);
	
	@Query("SELECT n FROM Notifications n LEFT JOIN NotificationsRead nr ON nr.message = n AND nr.user = :user WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND nr.id IS NULL AND n.createdAt >= :userCreatedAt ORDER BY n.createdAt DESC")
	Page<Notifications> findUnreadNotifications(@Param("user") User user,@Param("userCreatedAt") Date userCreatedAt, Pageable pageable);

	@Query("SELECT n FROM Notifications n LEFT JOIN NotificationsRead nr ON nr.message = n AND nr.user = :user WHERE (n.targetUser = :user OR n.targetType = 'GENERAL') AND nr.id IS NULL AND n.createdAt >= :userCreatedAt ORDER BY n.createdAt DESC")
	List<Notifications> findAllUnreadNotifications(@Param("user") User user,@Param("userCreatedAt") Date userCreatedAt);
	
	@Query("SELECT n FROM Notifications n JOIN NotificationsRead nr ON nr.message = n WHERE nr.user = :user ORDER BY n.createdAt DESC")
	Page<Notifications> findReadNotifications(@Param("user") User user, Pageable pageable);


}
