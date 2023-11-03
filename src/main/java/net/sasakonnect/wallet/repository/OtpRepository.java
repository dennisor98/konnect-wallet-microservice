package net.sasakonnect.wallet.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sasakonnect.wallet.domain.Otp;

public interface OtpRepository extends JpaRepository<Otp, String> {

	Optional<Otp> findFirstByPhoneNumberOrderByCreatedAtDesc(String phone);

	Optional<Otp> findByIdAndCode(String id, String code);

	Optional<Otp> findByHashAndCode(String hash, String code);

	@Query("SELECT otp FROM Otp otp JOIN FETCH otp.user WHERE otp.hash = :hash")
	Optional<Otp> findByHashWithUser(@Param("hash") String hash);

	@Query("SELECT otp FROM Otp otp JOIN FETCH otp.user WHERE otp.hash = :hash AND otp.code =:code AND otp.deletedAt is NULL ORDER BY otp.createdAt DESC")
	Optional<Otp> findByHashAndCodeWithUser(@Param("hash") String hash, @Param("code") String code);

	@Modifying
	@Query("UPDATE Otp otp SET  otp.deletedAt = :deletedAt WHERE otp.id = :id")
	void softDelete(@Param("id") String id, @Param("deletedAt") Date deletedAt);
}
