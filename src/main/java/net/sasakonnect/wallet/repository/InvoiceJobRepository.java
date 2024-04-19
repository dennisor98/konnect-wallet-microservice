package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.InvoiceJob;


public interface InvoiceJobRepository extends JpaRepository<InvoiceJob, String>{
   
}
