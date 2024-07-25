package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.transaction.annotation.Transactional;

import net.sasakonnect.wallet.RequestDto.NotificationDto;
import net.sasakonnect.wallet.domain.LarkUser;
import net.sasakonnect.wallet.domain.Notifications;
import net.sasakonnect.wallet.domain.NotificationsRead;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.enums.NotificationTargetType;
import net.sasakonnect.wallet.repository.NotificationsReadRepository;
import net.sasakonnect.wallet.repository.NotificationsRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.services.sme.FirebaseService;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

@Service
public class NotificationService {
	@Autowired
	NotificationsRepository  notificationsRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	NotificationsReadRepository notificationsReadRepository;
	@Autowired
	FirebaseService firebaseService;
	
   public void save(Notifications notification) {
	   this.notificationsRepository.save(notification);
   }
   
   @Transactional
   public ResponseEntity<Object> setAsRead(String notificationId) {
       User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       Optional<Notifications> notificationOptional = this.notificationsRepository.findById(notificationId);

       if (notificationOptional.isPresent()) {
           Notifications notification = notificationOptional.get();

           // Check if already read
           if (notification.getMessageRead() != null && this.notificationsReadRepository.existsByMessageAndUser(notification, user)) {
               Map<String, Object> response = new HashMap<>();
               response.put("success", false);
               response.put("message", "Message already read");
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
           }

           try {
        	 // Create and save NotificationsRead
           NotificationsRead notificationRead = NotificationsRead.builder()
                   .message(notification)
                   .user(user)
                   .build();
           NotificationsRead savedNotificationRead = this.notificationsReadRepository.save(notificationRead);

           // Update Notifications entity with the saved NotificationsRead
//           notification.setMessageRead(List.of(savedNotificationRead));
//           this.notificationsRepository.save(notification);

           Map<String, Object> response = new HashMap<>();
           response.put("success", true);
           response.put("message", "Notification updated as read");
           return ResponseEntity.status(HttpStatus.OK).body(response);
           }catch(Exception ex) {
        	   ex.printStackTrace();
        	   Map<String, Object> response = new HashMap<>();
        	   response.put("success",false);
        	   response.put("message","Something went wrong");
        	   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
           }
          
       } else {
           Map<String, Object> response = new HashMap<>();
           response.put("success", false);
           response.put("message", "Notification not found");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
       }
   }
   
   @Transactional
   public ResponseEntity<Object> setAllAsRead() {
       User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       List<Notifications> userNotifications = this.notificationsRepository.findAllUnreadNotifications(user,user.getCreatedAt());

       if (userNotifications.isEmpty()) {
           Map<String, Object> map = new HashMap<>();
           map.put("success", false);
           map.put("message", "No notifications to update");
           return ResponseEntity.status(HttpStatus.OK).body(map);
       }

       try {
           List<NotificationsRead> notificationsReadList = userNotifications.stream()
                   .map(n -> NotificationsRead.builder()
                           .message(n)
                           .user(user)
                           .build())
                   .collect(Collectors.toList());

           this.notificationsReadRepository.saveAll(notificationsReadList);

           Map<String, Object> map = new HashMap<>();
           map.put("success", true);
           map.put("message", "Notification update successful");
           return ResponseEntity.status(HttpStatus.OK).body(map);

       } catch (Exception ex) {
           ex.printStackTrace();
           Map<String, Object> map = new HashMap<>();
           map.put("success", false);
           map.put("message", "Something went wrong");
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
       }
   }
   
   public ResponseEntity<Object> createNotification(NotificationDto notification) {
	   
	   //send notification to an individual
	   if(notification.getTargetType().getValue().equalsIgnoreCase(NotificationTargetType.INDIVIDUAL.getValue())) {
		   Optional<User> user = this.userRepository.findById(notification.getUserId());
		   if(user.isPresent()) {
			   var ntf = Notifications.builder()
					   .title(notification.getTitle())
					   .message(notification.getMessage())
					   .targetUser(user.get())
					   .targetType(notification.getTargetType().getValue())
					   .build();
			   this.notificationsRepository.save(ntf);
			   Map<String,Object> map =  new HashMap<>();
			   map.put("success",true);
			   map.put("message","Request completed successfully");
			   
			   String firebaseToken =   user.get().getFirebaseTokens().get(0).getToken();
			   this.firebaseService.sendMessage(ntf);
			   return ResponseEntity.status(HttpStatus.OK).body(map);
		   }
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",false);
		   map.put("message","User not found");
		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	   }
	   
	   //else send notification as a broadcast
	   
	   var ntf = Notifications.builder()
			   .title(notification.getTitle())
			   .message(notification.getMessage())
			   .targetUser(null)
			   .targetType(notification.getTargetType().getValue())
			   .build();
	   this.notificationsRepository.save(ntf);
	   this.firebaseService.sendBroadCastMessage(ntf);
	   Map<String,Object> map = new HashMap<>();
	   map.put("success",false);
	   map.put("message","Request completed successfully");
	   return ResponseEntity.status(HttpStatus.OK).body(map);  
   }
   
   public ResponseEntity<Object> getUserNotifications(Integer pageNumber, Integer pageSize) {
	    try {
	        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        Page<Notifications> notifications = notificationsRepository.findUserNotifications(user, user.getCreatedAt(), PageRequest.of(pageNumber, pageSize));

	        Map<String, Object> response = new HashMap<>();
	        response.put("success", true);
	        response.put("message", "Request completed successfully");

	        // Populate paging info
	        Map<String, Object> pagingInfo = new HashMap<>();
	        pagingInfo.put("totalElements", notifications.getTotalElements());
	        pagingInfo.put("totalPages", notifications.getTotalPages());
	        pagingInfo.put("currentPage", notifications.getNumber());
	        pagingInfo.put("pageSize", notifications.getSize());
	        response.put("pagingInfo", pagingInfo);

	        // Process notifications
	        List<Map<String, Object>> notificationsList = notifications.stream().map(n -> {
	            Map<String, Object> nMap = new HashMap<>();
	            nMap.put("id", n.getId());
	            nMap.put("createdAt", n.getCreatedAt());
	            nMap.put("updatedAt", n.getUpdatedAt());
	            nMap.put("title", n.getTitle());
	            nMap.put("message", n.getMessage());
	            nMap.put("targetType", n.getTargetType());
	            boolean isRead = notificationsReadRepository.existsByMessageAndUser(n, user);
	            nMap.put("isRead", isRead);
	            return nMap;
	        }).collect(Collectors.toList());

	        response.put("notifications", notificationsList);

	        return ResponseEntity.status(HttpStatus.OK).body(response);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("success", false);
	        errorResponse.put("message", "A server error was encountered");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
   
   public ResponseEntity<Object> filterNotificationsByReadstatus(Boolean filter,Integer pageNumber,Integer pageSize){
	   try {
		   Page<Notifications> notifications;
			 User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			   if(filter == true) {
				   notifications = this.notificationsRepository.findReadNotifications(user,PageRequest.of(pageNumber,pageSize));
				   
			   }else {
				   notifications = this.notificationsRepository.findUnreadNotifications(user,user.getCreatedAt(),PageRequest.of(pageNumber,pageSize));
			   }
			   
			   Map<String,Object> map  = new HashMap<>();
			   if(notifications.isEmpty()) {
				   map.put("notifications", new ArrayList<>());
			   }
			   
			   var ntfs = notifications.stream().map(n->{
				   Map<String,Object> nMap = new HashMap<>();
					  nMap.put("id",n.getId());
					  nMap.put("createdAt",n.getCreatedAt());
					  nMap.put("updatedAt",n.getUpdatedAt());
					  nMap.put("title",n.getTitle());
					  nMap.put("message",n.getMessage());
					  nMap.put("target",n.getTargetType());
					  boolean isRead = notificationsReadRepository.existsByMessageAndUser(n, user);
			            nMap.put("isRead", isRead);
					  return nMap;
			   }).collect(Collectors.toList());
			   map.putIfAbsent("notifications",ntfs);
			   ResponsePagerClass<Notifications> page =  ResponsePagerClass.<Notifications>builder()
			   		    .page(notifications)
			   		    .build();
				   map.putAll(page.getPagingInfo());
		return ResponseEntity.status(HttpStatus.OK).body(map);
	   }catch(Exception ex) {
		   Map<String,Object> map = new HashMap<>();
		   map.put("success", false);
		   map.put("message", "Something went wrong");
		   ex.printStackTrace();
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
	   }
	
   }
}
