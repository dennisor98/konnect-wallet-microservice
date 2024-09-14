package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.CorporatePin;

public interface CorporatePinRepository extends JpaRepository<CorporatePin,String> {

}
