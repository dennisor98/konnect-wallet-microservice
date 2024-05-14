package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sasakonnect.wallet.RequestDto.ReversalDto;
import net.sasakonnect.wallet.domain.Reversal;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.ReversalRepository;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.WalletRepository;

@Service
public class ReversalService {
	@Autowired
	private ReversalRepository reversalRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	LarkService larkService;
	
	
	public ResponseEntity<Object> requestTransactionReversal(ReversalDto req){
		try {
	
			User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Optional<User> user = this.userRepository.findByMobile(req.getMobileNumber().substring(req.getMobileNumber().length() - 9));
			
			if(user.isEmpty()) {
				Map<String,Object> map = new HashMap<>();
				map.put("success",false);
				map.put("message","No phone number is associated with the account");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
			}else {
				var userWallets = this.walletRepository.findByUserWalletsUser(user.get());
				if(!userWallets.get(0).getAccountId().equalsIgnoreCase(req.getAccountNumber())) {
					Map<String,Object> map = new HashMap<>();
					map.put("success",false);
					map.put("message","Account number does not match the phone number");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
				}
				Optional<Transaction> transaction =  this.transactionRepository.findByTxId(req.getReference());
				if(transaction.isEmpty()) {
					Map<String,Object> map = new HashMap<>();
					map.put("success",false);
					map.put("message","Wrong transaction reference number");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
				}
				
				if(!transaction.get().getAccountId().equalsIgnoreCase(req.getAccountNumber())) {
					Map<String,Object> map = new HashMap<>();
					map.put("success",false);
					map.put("message","Transaction reference does not match the account number provided");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
				}
				var reversal = Reversal.builder()
						       .accountNumber(req.getAccountNumber())
						       .mobileNumber(req.getMobileNumber())
						       .reference(req.getReference())
						       .description(req.getDescription())
						       .build();
				this.reversalRepository.save(reversal);
				this.larkService.sendReversalRequestNotification(loggedInUser, req, userWallets.get(0).getUserWallets().get(0).getWallet(), transaction.get());
				Map<String,Object> map = new HashMap<>();
				map.put("success",true);
				map.put("message","Reversal request submitted");
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			Map<String,Object> map = new HashMap<>();
			map.put("success",false);
			map.put("message","Something went wrong");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
	}

}
