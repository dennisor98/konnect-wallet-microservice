package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.NotificationsRead;

public interface NotificationsReadRepository  extends JpaRepository<NotificationsRead,String>{
  
}
