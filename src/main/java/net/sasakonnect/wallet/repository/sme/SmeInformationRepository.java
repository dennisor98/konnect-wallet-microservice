package net.sasakonnect.wallet.repository.sme;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.sme.SmeAccountDetails;

public interface SmeInformationRepository extends JpaRepository<SmeAccountDetails, String> {

}
