package net.sasakonnect.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.UserJob;

public interface UserJobRepository extends JpaRepository<UserJob, String> {
	public Optional<UserJob> findByUserId(String user_id);

	@Modifying
	@Transactional
	@Query("UPDATE UserJob u SET u.downloadLink = :downloadLink, u.isComplete = true WHERE u.jobId = :jobId")
	void updateDownloadLinkAndIsCompleteByJobId(@Param("jobId") String jobId,
			@Param("downloadLink") String downloadLink);
	
	
	@Query("SELECT u FROM UserJob u WHERE u.jobOwner =:user ")
	List<UserJob> findAdminStatementsByAccountid(@Param("user") User user);

}
