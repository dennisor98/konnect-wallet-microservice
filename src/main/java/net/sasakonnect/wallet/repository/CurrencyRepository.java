package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

}
