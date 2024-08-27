package net.sasakonnect.wallet.repository.authz;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.authz.GlobalAuthority;

public interface GlobalAuthorityRepository extends JpaRepository<GlobalAuthority,String> {
   Optional<GlobalAuthority> findByName(String name);
}
