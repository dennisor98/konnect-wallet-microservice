package net.sasakonnect.wallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Logs;

public interface LogsRepository extends JpaRepository<Logs,String>{
	@Query("SELECT l FROM Logs l WHERE l.description LIKE %:queryString%")
   Page<Logs> findAllByDescriptionLike(@Param("queryString") String queryString,Pageable page);
}
