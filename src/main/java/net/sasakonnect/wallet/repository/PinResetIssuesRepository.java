package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.PinresetIssues;

public interface PinResetIssuesRepository extends JpaRepository<PinresetIssues,String> {

}
