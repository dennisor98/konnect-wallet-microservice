package net.sasakonnect.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
	@Query("SELECT ur.role FROM UserRole ur JOIN ur.role r WHERE ur.user.id = :userId")
	List<Role> findRolesByUserId(@Param("userId") String userId);
}
