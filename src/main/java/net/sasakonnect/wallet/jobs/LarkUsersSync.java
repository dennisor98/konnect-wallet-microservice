package net.sasakonnect.wallet.jobs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.transaction.Transactional;

import  org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.RequestDto.lark.user.*;
import net.sasakonnect.wallet.RequestDto.lark.LarkDTO;
import net.sasakonnect.wallet.RequestDto.lark.user.UserDTO;
import net.sasakonnect.wallet.domain.LarkDepartment;
import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.repository.LarkUserRepository;
import net.sasakonnect.wallet.repository.lark.DepartmentRepository;
import net.sasakonnect.wallet.services.LarkService;

@Data
class AccesTokenReponse {
	Integer code;
	Double expire;
	String msg;
	String tenant_access_token;
	
}



@Service
public class LarkUsersSync {
	private final String botId = "cli_a53a08afc8b8d00a";
	private String botSecret = "v0SWDp3ppqPHuKQ0ihtTefQiazd7lUFh";
	private final String larkBaseUrl = "https://open.larksuite.com/open-apis";
	 private static final Logger logger = LoggerFactory.getLogger(LarkUsersSync.class);
	 @Autowired
	 DepartmentRepository larkDepRepository;
	 
	 @Autowired
	 LarkService larkService;
	 
	 @Autowired
	 LarkUserRepository larkUserRepository;
//	
//	public LarkUsersSync() {
//		this.botId = botId;
//		this.botSecret = botSecret;
//	}
	
	public String getBotToken(String appId,String appSecret) {
		RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("app_id",appId);
        requestBody.put("app_secret",appSecret);
        
        // Create HttpEntity with headers and body
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(requestBody);
       try {
    	    ResponseEntity<AccesTokenReponse> responseEntity = restTemplate.exchange(
                this.larkBaseUrl+"/auth/v3/tenant_access_token/internal",
                HttpMethod.POST,
                requestEntity,
                AccesTokenReponse.class
        );
    	    AccesTokenReponse responseBody =  responseEntity.getBody();
    	    
    	    return responseBody.tenant_access_token;
       }catch(Exception ex) {
    	 return null;
       }
       

        
	}
	
	public void getDepartmentSubDepartments(String departmentId) {
 	   System.out.println("get sub-departments"+departmentId );

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+this.getBotToken(this.botId,this.botSecret));
        HttpEntity<LarkDTO> requestEntity = new HttpEntity<>(headers);
        String endpoint = "/contact/v3/departments/"+departmentId+"/children?department_id_type=open_department_id&page_size=50&user_id_type=open_id";
        
        ResponseEntity<LarkDTO> responseEntity = restTemplate.exchange(
        		this.larkBaseUrl + endpoint,
                HttpMethod.GET,
                requestEntity,
                LarkDTO.class
        );

        LarkDTO departments = responseEntity.getBody();
        if (null != departments.getData().getItems()) {
            departments.getData().getItems().forEach(item -> {
          	   System.out.println("data"+item);
//                this.getDepartmentSubDepartments(item.getOpen_department_id());

                try {
                    this.larkDepRepository.upsert(item.getOpen_department_id(), item.getName());
                } catch (Exception ex) {
                    // Handle the exception if needed
                }
            });
        }
	}
	
	
	@Transactional
//	@Scheduled(fixedRate = 2000)  //execute job after every one week
	public Object getLarkDepartments() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+this.getBotToken(this.botId,this.botSecret));
        HttpEntity<LarkDTO> requestEntity = new HttpEntity<>(headers);
        String endpoint = "/contact/v3/departments/0/children?department_id_type=open_department_id&page_size=50&user_id_type=open_id";
        ResponseEntity<LarkDTO> responseEntity = restTemplate.exchange(
                larkBaseUrl + endpoint,
                HttpMethod.GET,
                requestEntity,
                LarkDTO.class
        );

        LarkDTO departments = responseEntity.getBody();
//		restTemplate
//        System.out.println("hasMore"+departments);clear
             if(!departments.getData().items.isEmpty()) {
            	 departments.getData().getItems().forEach(item -> {
                     this.getDepartmentSubDepartments(item.getOpen_department_id());

                     try {
                         this.larkDepRepository.upsert(item.getOpen_department_id(), item.getName());
                     } catch (Exception ex) {
                         // Handle the exception if needed
                     }
                 });
        }else {
        	
        }
		
//       this.getBotToken(this.botId,this.botSecret);
		return null;
	}
	
	 public List<String> getDepartmentIds() {
		   List<LarkDepartment> deps = this.larkService.getAllDepartments();
		   var depIds =  deps.stream().map(dep -> {
			 return  dep.getDepartmentId();
			   
		   }).collect(Collectors.toList());
		   return depIds;
	   }
	   
	  public void getDepartmentUsersByDepartmentId(String departmentId) {
	        String endpoint = "/contact/v3/users/find_by_department";
	        
	         UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(larkBaseUrl + endpoint)
	                .queryParam("department_id", departmentId)
	                .queryParam("department_id_type", "open_department_id")
	                .queryParam("page_size", 50)
	                .queryParam("user_id_type", "open_id");

	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        headers.set("Authorization", "Bearer " + this.getBotToken(this.botId, this.botSecret));

	        HttpEntity<UserResponseDTO> requestEntity = new HttpEntity<>(headers);

	        ResponseEntity<UserResponseDTO> responseEntity = restTemplate.exchange(
	                builder.toUriString(),
	                HttpMethod.GET,
	                requestEntity,
	                UserResponseDTO.class
	        );
	        
	        UserResponseDTO responseBody  =  responseEntity.getBody();
	        System.out.println("hasMore" + responseBody.getData().hasMore);
	        if(responseBody.getData().hasMore == true) {
	        	   this.fetchMoreUsers(responseBody.getData().page_token, departmentId);
	           }
	        if(responseBody.getData().items.size() > 0) {
	        	List<LarkUser> users =  responseBody.getData().items.stream().map(item -> {
	        		return LarkUser.builder()
	        				.email(item.getEmail())
	        				.departmentId(departmentId)
	        				.openId(item.getOpen_id())
	        				.name(item.getName())
	        				.mobileNumber(item.getMobile())
	        				.avartarUrl(item.getAvatar().getAvatar_origin())
	        				.build();
	        	}).collect(Collectors.toList());
	        	
	           for(LarkUser user : users) {
//	        	   upsert(String departmentId, String name, String openId, String avartarUrl, String mobileNumber, String email)
	        	   this.larkUserRepository.upsert(departmentId,user.getName(), user.getOpenId(), user.getAvartarUrl(),user.getMobileNumber(), user.getEmail());
	           };
	          
	        }else {
	        	   System.out.println("Users empty");

	        }

//	        System.out.println("Users" + responseEntity.getBody().getData().items.toString());

	    }
	  
	  public void fetchMoreUsers(String pageToken,String departmentId) {
		  String endpoint = "/contact/v3/users/find_by_department";
	        
	         UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(larkBaseUrl + endpoint)
	                .queryParam("department_id", departmentId)
	                .queryParam("department_id_type", "open_department_id")
	                .queryParam("page_size", 50)
	                .queryParam("page_token",pageToken)
	                .queryParam("user_id_type", "open_id");

	        RestTemplate restTemplate = new RestTemplate();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	        headers.set("Authorization", "Bearer " + this.getBotToken(this.botId, this.botSecret));

	        HttpEntity<UserResponseDTO> requestEntity = new HttpEntity<>(headers);

	        ResponseEntity<UserResponseDTO> responseEntity = restTemplate.exchange(
	                builder.toUriString(),
	                HttpMethod.GET,
	                requestEntity,
	                UserResponseDTO.class
	        );
	        
	        UserResponseDTO responseBody  =  responseEntity.getBody();
	        System.out.println("message" + responseEntity.getBody().getMsg());
	        if(responseBody.getData().items.size() > 0) {
	        	List<LarkUser> users =  responseBody.getData().items.stream().map(item -> {
	        		return LarkUser.builder()
	        				.email(item.getEmail())
	        				.departmentId(departmentId)
	        				.openId(item.getOpen_id())
	        				.name(item.getName())
	        				.mobileNumber(item.getMobile())
	        				.avartarUrl(item.getAvatar().getAvatar_origin())
	        				.build();
	        	}).collect(Collectors.toList());
	        	
	           for(LarkUser user : users) {
//	        	   upsert(String departmentId, String name, String openId, String avartarUrl, String mobileNumber, String email)
	        	   this.larkUserRepository.upsert(departmentId,user.getName(), user.getOpenId(), user.getAvartarUrl(),user.getMobileNumber(), user.getEmail());
	           };
	        }
	  }
	
//   @Scheduled(fixedRate=6000)
	public void syncLarkDeptUsers() {
	   var depIds =   this.getDepartmentIds().stream().map(depId -> {
			return depId;
		}).collect(Collectors.toList());
//	   this.getDepartmentUsersByDepartmentId("od-161a5bb86b9cbd14cd04140122486680");
	   for(String i : depIds ) {
		   this.getDepartmentUsersByDepartmentId(i);
	   }
//	   depIds.forEach(id -> {
//		   this.getDepartmentUsersByDepartmentId(id);
//	   });
//		
	}
	

}
