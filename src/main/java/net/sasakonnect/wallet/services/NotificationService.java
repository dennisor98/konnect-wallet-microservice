package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.RequestDto.NotificationDto;
import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.enums.NotificationTargetType;
import net.sasakonnect.wallet.repository.NotificationsRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Service
public class NotificationService {
	@Autowired
	NotificationsRepository  notificationsRepository;
	
	@Autowired
	UserRepository userRepository;
	
	
   public void save(Notifications notification) {
	   this.notificationsRepository.save(notification);
   }
   
   public ResponseEntity<Object> setAsRead(String notificationId) {
	   Optional<Notifications> notification = this.notificationsRepository.findById(notificationId);
	   if(notification.isPresent()) {
		   var ntf = notification.get();
		   ntf.setIsRead(true);
		   this.notificationsRepository.save(ntf);
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",true);
		   map.put("message","Notification updated as read");
		   return ResponseEntity.status(HttpStatus.OK).body(map);
	   }else {
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",false);
		   map.put("message","Notification not found");
		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	   }
   }
   
   public ResponseEntity<Object> createNotification(NotificationDto notification) {
	   if(notification.getTargetType().getValue().equalsIgnoreCase(NotificationTargetType.INDIVIDUAL.getValue())) {
		   Optional<User> user = this.userRepository.findById(notification.getUserId());
		   if(user.isPresent()) {
			   var ntf = Notifications.builder()
					   .title(notification.getTitle())
					   .message(notification.getMessage())
					   .targetUser(user.get())
					   .targetType(notification.getTargetType().getValue())
					   .isRead(false)
					   .build();
			   this.notificationsRepository.save(ntf);
			   Map<String,Object> map =  new HashMap<>();
			   map.put("success",true);
			   map.put("message","Request completed successfully");
			   
			   return ResponseEntity.status(HttpStatus.OK).body(map);
		   }
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",false);
		   map.put("message","User not found");
		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	   }
	   
	   var ntf = Notifications.builder()
			   .title(notification.getTitle())
			   .message(notification.getMessage())
			   .targetUser(null)
			   .targetType(notification.getTargetType().getValue())
			   .isRead(false)
			   .build();
	   this.notificationsRepository.save(ntf);
	   
	   Map<String,Object> map = new HashMap<>();
	   map.put("success",false);
	   map.put("message","Request completed successfully");
	   return ResponseEntity.status(HttpStatus.OK).body(map);  
   }
   
   public ResponseEntity<Object> getUserNotifications(Integer pageNumber,Integer pageSize){
	   try {
		   User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   
	   Page<Notifications> notifications = this.notificationsRepository.findUserNotifications(user,PageRequest.of(pageNumber, pageSize));
	   Map<String,Object> map = new HashMap<>();
	   
	   map.put("success", true);
	   map.put("message", "Request completed successfully");
	   ResponsePagerClass<Notifications> page =  ResponsePagerClass.<Notifications>builder()
   		    .page(notifications)
   		    .build();
	   map.putAll(page.getPagingInfo());
	   if(!notifications.isEmpty()) {
		  var ntf =  notifications.stream().map(n ->{
			  Map<String,Object> nMap = new HashMap<>();
			  nMap.put("id",n.getId());
			  nMap.put("createdAt",n.getCreatedAt());
			  nMap.put("updatedAt",n.getUpdatedAt());
			  nMap.put("title",n.getTitle());
			  nMap.put("message",n.getMessage());
			  nMap.put("target",n.getTargetType());
			  nMap.put("isRead",n.getIsRead());
			  return nMap;
		   }).collect(Collectors.toList());
		  map.put("notifications",ntf);
		 return ResponseEntity.status(HttpStatus.OK).body(map);

	   }else {
		  map.put("notifications",new ArrayList<>());
	   return ResponseEntity.status(HttpStatus.OK).body(map); 
	   }
	   

	   }catch(Exception ex) {
		   ex.printStackTrace();
		   Map<String,Object> map = new HashMap<>();
		   map.put("success", false);
		   map.put("message", "A server error was encountered");
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	   }
	   
   }
}
