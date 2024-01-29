package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.domain.LarkDepartment;
import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.Wallet;
import net.sasakonnect.wallet.jobs.LarkUsersSync;
import net.sasakonnect.wallet.repository.LarkUserRepository;
import net.sasakonnect.wallet.repository.lark.DepartmentRepository;

@Service
public class LarkService {
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	LarkUserRepository   larkUserRepository;

	@Autowired
	LarkUsersSync larkSync;
	
	
   public List<LarkDepartment> getAllDepartments(){
	   List<LarkDepartment> departments  = this.departmentRepository.findAll();
	   
	   if(!departments.isEmpty()) {
		   return departments;
	   }else {
		   return null;
	   }
	  
   }
   
   
   public Object getLarkUsers(Integer pageNumber,Integer pageSize) {
	   
	   Page<LarkUser> larkUsers = this.larkUserRepository.findAll(PageRequest.of(pageNumber,pageSize));
	   Map<String,Object> data =  new HashMap<>();
       Map<String,Object> payload =  new HashMap<>();
	   payload.put("success",true);
	   payload.put("message","Request succesfull");			
	  List<Map<String, Object>> usersCollection = larkUsers.get().map(user -> {
	  Map<String,Object> resultData = new HashMap<>();
				    resultData.put("id",user.getId());
				    resultData.put("open_id",user.getOpenId());
				    resultData.put("name",user.getName());
				    resultData.put("email",user.getEmail());
				    resultData.put("mobileNumber",user.getMobileNumber());
				    resultData.put("avatarUrl",user.getAvartarUrl());
				    return resultData;
				}).collect(Collectors.toList());
				data.put("result",usersCollection);
			
			data.put("totalRows",Double.valueOf(larkUsers.getTotalElements()));
			data.put("pageSize", larkUsers.getSize());
			data.put("currentPage",larkUsers.getNumber());
			data.put("nextPage",larkUsers.hasNext()?larkUsers.nextPageable().getPageNumber() : null);
			data.put("hasNextPage",larkUsers.hasNext());
			data.put("hasPreviousPage",larkUsers.hasPrevious());
			payload.put("payload",data);
			return payload;
		
		   }
      public void syncLarkUsers() {
    	  this.larkSync.syncLarkDeptUsers();
      }
      
      public void synLarkDepartments() {
    	  this.larkSync.getLarkDepartments();
    }
  
}
