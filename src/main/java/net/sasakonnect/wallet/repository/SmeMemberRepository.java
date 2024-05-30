package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.SmeMember;

public interface SmeMemberRepository extends JpaRepository<SmeMember, String> {

}
