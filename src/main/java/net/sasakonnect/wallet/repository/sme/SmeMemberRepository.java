package net.sasakonnect.wallet.repository.sme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.SmeMember;

public interface SmeMemberRepository extends JpaRepository<SmeMember,String> {
    Optional<SmeMember> findSmeMemberByUser(User user);
}
