package net.sasakonnect.wallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.domain.Transaction;

public interface LarkUserRepository extends JpaRepository<LarkUser,String>{
	@Modifying
    @Transactional
    @Query("UPDATE LarkUser u SET u.name = :name, u.mobileNumber = :mobileNumber WHERE u.openId = :openId")
    int updateExistingUser(@Param("openId") String openId, @Param("name") String name, @Param("mobileNumber") String mobileNumber);

    @Modifying
    @Transactional
    @Query("INSERT INTO LarkUser (departmentId, name, openId, avartarUrl, mobileNumber, email) VALUES (:departmentId, :name, :openId, :avartarUrl, :mobileNumber, :email)")
    int insertNewUser(@Param("departmentId") String departmentId, @Param("name") String name, @Param("openId") String openId,
                      @Param("avartarUrl") String avartarUrl, @Param("mobileNumber") String mobileNumber, @Param("email") String email);

    default void upsert(String departmentId, String name, String openId, String avartarUrl, String mobileNumber, String email) {
        if (updateExistingUser(openId, name, mobileNumber) == 0) {
            insertNewUser(departmentId, name, openId, avartarUrl, mobileNumber, email);
        }
    }
    
   
    
    @Query("SELECT u.id,u.openId,u.name,u.avartarUrl,u.email,u.mobileNumber FROM LarkUser u")
    Page<LarkUser> findAllLarkUsers(@Param("accountId") String accountId,Pageable pageable);
    
    @Query("SELECT u FROM LarkUser u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :queryString, '%'))")
    Page<LarkUser> searchLarkUser(@Param("queryString") String queryString, Pageable pageable);
}
