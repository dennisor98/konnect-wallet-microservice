package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.Logs;

public interface LogsRepository extends JpaRepository<Logs,String>{

}
