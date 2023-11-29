package net.sasakonnect.wallet.repository;
import jakarta.persistence.EntityManager;
import java.util.logging.*;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.domain.CorporateDetails;
import net.sasakonnect.wallet.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CorporateDetailsRepository extends JpaRepository<CorporateDetails,String>{	
//    private static final Logger logger = Logger.getLogger(CorporateDetailsRepository.class.getName());
//
//    @PersistenceContext EntityManager entityManager;
//
//    public Object insertCorporateDetails(Map<String,Object> corporate) {
//    	Map<String,Object> map =  new HashMap<String,Object>();
//    	logger.info("request"+corporate);
//    	try {
//    		String nativeQuery = "INSERT INTO corporate_details (id,corporate_email, phone, is_verified, is_email_verified, is_active) VALUES (?,?,?, ?, ?, ?)";
//    		entityManager.createNativeQuery(nativeQuery)
//    		    .setParameter(1, corporate.get("id"))
//    		    .setParameter(2, corporate.get("corporate_email"))
//    		    .setParameter(3, corporate.get("phone"))
//    		    .setParameter(4, corporate.get("is_verified"))
//    		    .setParameter(5, corporate.get("is_email_verified"))
//    		    .setParameter(6, corporate.get("is_active"))
//    		    .executeUpdate();
//    		
////        corporate object
//    	Map<String,Object> resMap =  new HashMap<String,Object>();
//    	resMap.put("id",corporate.get("id"));
//    	resMap.put("corporate_email",corporate.get("corporate_email"));
//    	resMap.put("phone",corporate.get("corporate_email"));
//    	resMap.put("is_verified",corporate.get("is_verified"));
//    	resMap.put("is_email_verified",corporate.get("is_email_verified"));
//    	resMap.put("is_active",corporate.get("is_active"));
//        
//    	map.put("success","true");
//        map.put("payload",resMap);
//        map.put("message","Corporate Details created");
//       return ResponseEntity.status(HttpStatus.OK).body(map);
//    	}catch(Exception ex) {
//    		map.put("success", "false");
//    		map.put("message","Error processing request");   
//        	logger.info("SQL ERROR"+ex);
//    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
//
//    	}
//    	 
//    }
//    
//    @Transactional
//    public Object activateCorporateAccount(Object id,Object isActive,Object isVerified) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//        	int  hasAccount = entityManager.createNativeQuery(
//                    "SELECT u FROM user u WHERE u.corporate = :id")
//                    .setParameter("id", id)
//                    .executeUpdate();
//        	if(hasAccount < 1) {
//            	map.put("success",false);
//                map.put("message","No user has this corporate email");
//            }else {
//            	int affectedRows = entityManager.createNativeQuery(
//                    "UPDATE corporate_details c SET c.isActive = :isActive,isVerified= :isVerified WHERE c.id = :id")
//                    .setParameter("isActive", isActive)
//                    .setParameter("id", id)
//                    .setParameter("isVerified",isVerified)
//                    .executeUpdate();
//                 map.put("success", affectedRows > 0);
//                 map.put("message", affectedRows > 0 ? "Corporate account activated successfully" : "No matching corporate account found");
//            }
//            
//          
//        } catch (Exception ex) {
//            map.put("success", false);
//            map.put("message", "Error activating corporate account");
//        }
//        return map;
//    }
//    
//    @Transactional
//    public Object verifyCorporateEmail(String email, Boolean is_email_verified) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//            int affectedRows = entityManager.createNativeQuery(
//                    "UPDATE corporate_details c SET c.isEmailVerified = :is_email_verified WHERE c.corporate_email = :email")
//                    .setParameter("is_email_verified", is_email_verified)
//                    .setParameter("email",email)
//                    .executeUpdate();
//
//            map.put("success", affectedRows > 0);
//            map.put("message", affectedRows > 0 ? "Corporate email activated successfully" : "No matching corporate account found");
//        } catch (Exception ex) {
//            map.put("success", false);
//            map.put("message", "Error activating corporate email");
//        }
//        return map;
//    }
//    
//   public Object getCorporateAccounts() {
//	   Map<String,Object> map =  new HashMap<>();
//	   List<Map<String,Object>> users = entityManager.createNativeQuery("SELECT * FROM corporate_details c")
//		        .getResultList();
//	   return users;
//
//   }
    
}
