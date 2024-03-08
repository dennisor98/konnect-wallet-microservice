package net.sasakonnect.wallet.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.repository.NotifiedContactRepository;
import net.sasakonnect.wallet.domain.NotifiedContact;
import net.sasakonnect.wallet.domain.User;
@Service
public class TransactionEventService {
     @Autowired
    private NotifiedContactRepository  notifiedContactRespository;
     
     @Autowired
     private UserService userService;
     
     @Autowired
    private SmsService smsService;
	
	public void notifyNewCustomer(String accountId,String phone){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run(){
		Optional<User> user = userService.findUserByPhoneNumber(phone);
		
		//check if user has no wallet acc
		if(user.isEmpty()) {
			Optional<NotifiedContact> contact = notifiedContactRespository.findByContact(phone);
			//notify customer in case not yet notified
			if(contact.isEmpty()) {
				
				//send notification
				smsService.sendSms(null, "+254"+phone);
				//record contact as notified
				var notified = NotifiedContact.builder()
						.contact(phone)
						.senderAccId(accountId)
						.build();
				
				notifiedContactRespository.save(notified);
			}
		}
			
			}
		});
		thread.start();
	}
}
