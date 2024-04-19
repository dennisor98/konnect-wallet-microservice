package net.sasakonnect.wallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.InvoiceJob;


public interface InvoiceJobRepository extends JpaRepository<InvoiceJob, String>{
	Page<InvoiceJob> findByOrderByCreatedAtDesc(Pageable page);
}
