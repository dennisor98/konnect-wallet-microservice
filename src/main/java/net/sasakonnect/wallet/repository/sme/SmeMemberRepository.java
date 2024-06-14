package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeMember;

public interface SmeMemberRepository extends JpaRepository<SmeMember,String> {
	@Query("SELECT m FROM SmeMember m WHERE m.user = :user")
    Optional<SmeMember> findSmeMemberByUser(@Param("user") User user);
}
