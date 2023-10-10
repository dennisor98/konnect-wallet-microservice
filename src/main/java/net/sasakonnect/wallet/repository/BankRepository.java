package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.Bank;

public interface BankRepository extends JpaRepository<Bank, String> {

}
