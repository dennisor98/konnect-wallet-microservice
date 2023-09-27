package net.sasakonnect.wallet.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
	@Transactional
	@Modifying
	@Query("INSERT INTO Role r (r.roleName) SELECT :name FROM Role r WHERE NOT EXISTS (SELECT 1 FROM Role r2 WHERE r2.roleName = :name)")
	void insertRole(@Param("name") String name);

	@Transactional
	@Modifying
	@Query("INSERT INTO Role r (r.roleName) SELECT :name FROM Role r WHERE NOT EXISTS (SELECT 1 FROM Role r2 WHERE r2.roleName IN :names)")

	void insertRoles(@Param("names") Collection<String> names);
}
