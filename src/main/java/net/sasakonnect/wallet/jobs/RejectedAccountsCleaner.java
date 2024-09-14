package net.sasakonnect.wallet.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.UserRepository;


@Component
public class RejectedAccountsCleaner {
    @Autowired
    UserRepository  userRepository;
    
    @Scheduled(cron = "0 */3 * * * *")
    private void clearRejectedAccounts() {
    	List<User> userList = this.userRepository.findRejectedUsers();
    	if(!userList.isEmpty()) {
    		this.userRepository.deleteAll(userList);
    	}
    }
}
