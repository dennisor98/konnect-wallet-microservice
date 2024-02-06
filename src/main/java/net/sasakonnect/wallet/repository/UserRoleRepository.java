package net.sasakonnect.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
	@Query("SELECT ur.role FROM UserRole ur JOIN ur.role r WHERE ur.user.id = :userId")
	List<Role> findRolesByUserId(@Param("userId") String userId);
	
	Optional<UserRole> findUserRoleByUserId(@Param("userId") String userId);
	
	
	
	@Query("SELECT ur FROM UserRole ur WHERE ur.roleId =:roleId AND ur.userId =:userId")
	Optional<UserRole> findUserRoleByUserIdAndRoleId(@Param("userId") String userId,@Param("roleId") String roleId);
	
	
//	@Modifying
//    @Transactional
//    @Query("UPDATE UserRole u SET u.userId = :userId, u.roleId = :roleId,u. WHERE u.id = :id")
//    int updateExistingRole(@Param("id") String id, @Param("roleid") String roleId, @Param("userId") String userId);
	
}
