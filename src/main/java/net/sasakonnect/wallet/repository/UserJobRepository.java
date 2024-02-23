package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

import net.sasakonnect.wallet.domain.UserJob;

public interface UserJobRepository extends JpaRepository<UserJob, String> {
	public Optional<UserJob> findByUserId(String user_id);

	@Modifying
	@Transactional
	@Query("UPDATE UserJob u SET u.dowloadLink = :downloadLink, u.isComplete = true WHERE u.jobId = :jobId")
	void updateDownloadLinkAndIsCompleteByJobId(String jobId, String downloadLink);
}
