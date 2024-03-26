package net.sasakonnect.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sasakonnect.wallet.domain.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage,String> {

}
