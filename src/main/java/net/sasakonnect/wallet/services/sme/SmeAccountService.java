package net.sasakonnect.wallet.services.sme;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.repository.sme.SmeAccountRepository;

@Service
public class SmeAccountService {
   @Autowired
   SmeAccountRepository smeAccountRepository;
   
   public Optional<SmeAccount> findSmeAccountByAccountId(String accountId){
	   return this.smeAccountRepository.findByAccountNo(accountId);
   }
   
   
}
