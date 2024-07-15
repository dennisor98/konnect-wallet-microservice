package net.sasakonnect.wallet.services.sme;

import java.math.BigDecimal;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.repository.WalletRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRolePermissionRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountUserRoleRepository;
import net.sasakonnect.wallet.repository.sme.SmeCorporateRepository;
import net.sasakonnect.wallet.repository.sme.SmeRepository;
import net.sasakonnect.wallet.repository.sme.SmeTransactionRepository;
import net.sasakonnect.wallet.services.ChoiceBankSmsService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.RequestSigner;
import net.sasakonnect.wallet.tools.ResponsePagerClass;
import reactor.core.publisher.Mono;
import net.sasakonnect.wallet.RequestDto.ChoiceTransferDto;
import net.sasakonnect.wallet.RequestDto.MpesaBilling;
import net.sasakonnect.wallet.RequestDto.WalletTransferDto;
import net.sasakonnect.wallet.RequestDto.sme.ChoiceSmeTransferDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeMpesaBilling;
import net.sasakonnect.wallet.RequestDto.sme.SmeTransferToMpesa;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.constant.sme.GlobalSmeAccountPermissionConstants;
import net.sasakonnect.wallet.domain.Logs;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeCorporate;
import net.sasakonnect.wallet.domain.sme.SmeTransaction;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRolePermission;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountUserRole;
import net.sasakonnect.wallet.enums.LogTypes;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.TransactionResultNotification;

@Slf4j
@Service
public class SmeTransactionService {
   @Autowired
   public SmeTransactionRepository smeTransactionRepository;
   @Autowired
	RequestSigner requestSigner;
   @Autowired
   UserService userService;
   @Autowired
   SmeService smeService;
   @Autowired
   SmeUserService smeUserService;
   @Autowired
   WalletRepository walletRepository;
   @Autowired
   JwtService jwtService;
   @Autowired
   BankWebClientBean bankClientBean;
   @Autowired
   ChoiceBankSmsService choiceBankSmsService;
   @Autowired
   SmeAccountService smeAccountservice;
   @Autowired
   SmeAccountUserRoleRepository smeAccountUserRoleRepository;
   @Autowired
   SmeAccountRolePermissionRepository smeAccountRolePermissionsRepository;
   @Autowired
   SmeRepository smeRepository;
   @Autowired
   SmeCorporateRepository smeCorporateRepository;
 
   
   public SmeTransaction saveTransaction(NotificationResult<TransactionResultNotification> results) {
		var trans = results.getParams();
		var existingTransaction = this.smeTransactionRepository.findByTxId(trans.getTxId());
		if (!existingTransaction.isPresent()) {
			var transaction = SmeTransaction.builder().txId(trans.getTxId()).txType(trans.getTxType())
					.externalTxId(trans.getExternalTxId()).accountId(trans.getAccountId())
					.accountName(trans.getAccountName()).oppoSubAccount(trans.getOppoSubAccount())
					.balance(trans.getBalance() == null ? null : new BigDecimal(trans.getBalance()))
					.oppoBankCode(trans.getOppoBankCode()).requestId(results.getRequestId())
					// .extInfo(trans.getExtInfo().toString())
					.notificationType(trans.getTxType())
					.remarks(trans.getErrorMsg())
					.feeAmount(trans.getFeeAmount() != null ? new BigDecimal(trans.getFeeAmount()) : new BigDecimal(0))
					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
					.txStatus(trans.getTxStatus())
					.oppoAccountId(trans.getOppoAccountId()).oppoChannelId(trans.getOppoChannelId())
					.oppoAccountName(trans.getOppoAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
					.counterpartyName(trans.getExtInfo().getCounterpartyName())
					.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
			return this.smeTransactionRepository.save(transaction);
			
		} else {
//			var transaction = Transaction.builder().txId(trans.getTxId()).txType(trans.getTxType())
//					.externalTxId(trans.getExternalTxId()).accountId(trans.getAccountId())
//					.accountName(trans.getAccountName()).oppoSubAccount(trans.getOppoSubAccount())
//					// .mpesaBusinessPayType(trans.getMpesaBusinessPayType())
//					.oppoAccountName(trans.getAccountName()).thirdPartyTxType(trans.getThirdPartyTxType())
//					.currency(trans.getCurrency()).amount(new BigDecimal(trans.getAmount())).build();
//			this.transactionRepository.save(transaction);	
		}

		// results.getParams()results;
		// TODO Auto-generated method stub
		return null;

	}
   
   public Optional<SmeTransaction> findSmeTransactionByTxId(String txId){
	   return this.smeTransactionRepository.findByTxId(txId);
   }
   
   public Object applyForTransfer(@Valid ChoiceSmeTransferDto choiceTransfer) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   //verify that smeId in the authentication header is available
	   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
	  String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	  Optional<Sme> sme =  this.smeRepository.findById(smeId);
	  if(sme.isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot process request.Consult your administrator");
	  }else {
		  Optional<SmeCorporate> smeCorporate =  this.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme.get());
		  if(smeCorporate.isEmpty()) {
			  throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
		  }
		  //check if the provided payer account is available
		  Optional<SmeAccount> smeAccount = this.smeAccountservice.findSmeAccountByAccountId(choiceTransfer.getPayerAccountNumber());
		  if(smeAccount.isEmpty()) {
			  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown account information provided");
		  }
		  
		  //check if user has transaction privileges in the provided payer account
//		  if(this.userHasAccountPermission(smeAccount.get(), GlobalSmeAccountPermissionConstants.CanInvokeTransaction.PERMISSION)) {
//			  throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
//		  }
		  
		  var reqId = new HashMap<String, Object>();
			var receivingUser = this.userService.findUserByAccountd(choiceTransfer.getReceiverAccount().trim());
			if (receivingUser.isPresent()) {
				reqId.put("payeeMobileForNotification", receivingUser.get().getMobile());

			}
			reqId.put("payerAccountId", choiceTransfer.getPayerAccountNumber());
			reqId.put("payeeBankCode", choiceTransfer.getBankCode().trim());
			reqId.put("payeeAccountId", choiceTransfer.getReceiverAccount().trim());
			reqId.put("payeeAccountName", choiceTransfer.getReceiverName());
			reqId.put("currency", choiceTransfer.getCurrencyCode());
			reqId.put("amount", choiceTransfer.getAmount());
			reqId.put("otpMobile", user.getMobile());
			reqId.put("otpType", choiceTransfer.getOtpType());
			reqId.put("remark", choiceTransfer.getRemarks());

			var reqs = this.requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

			String responseJson = responseMono.block();
			log.info(responseJson);
			if (responseJson != null) {
				var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
				choiceBankSmsService.invokeSms(resp.getData().txId);
				log.info(resp.getData().txId);
				return resp;
			}
		}
       
		

		// TODO Auto-generated method stub
		return null;
	}
   
   public Object withdrawToMpesa(SmeTransferToMpesa mpesa) {
	   User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   //verify that smeId in the authentication header is available
	   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			   .getRequest();
	   String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	   Optional<Sme> sme =  this.smeRepository.findById(smeId);
	   if(sme.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot process request.Consult your administrator");
	   }
	   Optional<SmeCorporate> smeCorporate =  this.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme.get());
	   if(smeCorporate.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
	   }
	   //check if the provided payer account is available
	   Optional<SmeAccount> smeAccount = this.smeAccountservice.findSmeAccountByAccountId(mpesa.getPayerAccountNumber());
	   if(smeAccount.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown account information provided");
	   }

	   var reqId = new HashMap<String, Object>();
	   reqId.put("payerAccountId", mpesa.getPayerAccountNumber());
	   reqId.put("amount", mpesa.getAmount());
	   reqId.put("payeeBankCode", "M-PESA");
	   reqId.put("payeeAccountId", mpesa.getReceiverMobileNumber());
	   reqId.put("currency", mpesa.getCurrencyCode());
	   reqId.put("remark", mpesa.getRemarks());
	   reqId.put("otpType", "SMS");
	   reqId.put("payeeMobileForNotification", mpesa.getReceiverMobileNumber());

	   var reqs = this.requestSigner.signRequest(reqId);

	   Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.WITHDRAW)
			   .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
			   .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

	   String responseJson = responseMono.block();
	   if (responseJson != null) {
		   var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
		   choiceBankSmsService.invokeSms(resp.getData().txId);
		   log.info(resp.getData().txId);
		   return resp;
	   }

	   return null;
   }
   
   public Object mpesaTillAndByGoods(@Valid SmeMpesaBilling tillAndBuyGoods) {

	   User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   //verify that smeId in the authentication header is available
	   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			   .getRequest();
	   String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	   Optional<Sme> sme =  this.smeRepository.findById(smeId);
	   if(sme.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot process request.Consult your administrator");
	   }
	   Optional<SmeCorporate> smeCorporate =  this.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme.get());
	   if(smeCorporate.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
	   }
	   //check if the provided payer account is available
	   Optional<SmeAccount> smeAccount = this.smeAccountservice.findSmeAccountByAccountId(tillAndBuyGoods.getPayerAccountNumber());
	   if(smeAccount.isEmpty()) {
		   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown account information provided");
	   }
	   var reqId = new HashMap<String, Object>();
	   reqId.put("payerAccountId", tillAndBuyGoods.getPayerAccountNumber());
	   reqId.put("payType", tillAndBuyGoods.getBillType().getCode());

	   switch (tillAndBuyGoods.billType) {
	   case PAY_BILL:
		   reqId.put("payeeReferenNumber", tillAndBuyGoods.getReceivingAccount().trim());

		   break;
	   case TILL:

		   break;
	   default:
		   break;

	   }
		reqId.put("payeeShortCode", tillAndBuyGoods.getShortCode().trim());

		reqId.put("amount", tillAndBuyGoods.getAmount());
		reqId.put("description", tillAndBuyGoods.getShortNote());

		reqId.put("otpType", tillAndBuyGoods.getOtpType());

		var reqs = this.requestSigner.signRequest(reqId);

		Mono<String> responseMono = this.bankClientBean.webClient.post()
				.uri(ChoiceEndpointsConstants.MPESA_TILL_AND_PAYBILL).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(String.class);
		String responseJson = responseMono.block();
		log.info(responseJson);
		if (responseJson != null) {
			var resp = new Gson().fromJson(responseJson, TransactionResponseDto.class);
			choiceBankSmsService.invokeSms(resp.getData().txId);
			return resp;
			// return new Gson().fromJson(responseJson, Object.class);

		}

		// TODO Auto-generated method stub
		return null;
	}
   
   private boolean userHasAccountPermission(SmeAccount smeAccount,String permission) {
	   User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
	   String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
	   Optional<Sme> sme =  this.smeRepository.findById(smeId);
	   Optional<SmeCorporate> smecorpOptional =  this.smeUserService.smeCorporateRepository.findSmeCorporateByUserAndSmes(user,sme.get());
	   if(smecorpOptional.isEmpty()) {
		   return false;
	   }
	   Optional<SmeAccountUserRole> smeaccrole = this.smeAccountUserRoleRepository.findByCorporateAndSmeAccount(smecorpOptional.get(),smeAccount);
	   if(smeaccrole.isEmpty()) {
		   return false;
	   }
	   
	   //check if user role has the required permission
	   Optional<SmeAccountRolePermission> smeccpermissions = this.smeAccountRolePermissionsRepository.findRolePermissionByRoleAndPermission(smeaccrole.get().getRole(),permission);
	   if(smeccpermissions.isEmpty()) {
		   return false;
	   }
	   return true;
   }
   
   public ResponseEntity<Object> getSmeTransactions(Integer pageNumber,Integer pageSize){
	   Page<SmeTransaction> smeTransactionspage =  this.smeTransactionRepository.findAll(PageRequest.of(pageNumber,pageSize));
	   if(smeTransactionspage.isEmpty()) {
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",true);
		   map.put("message","Request complete");	
		   map.put("transactions",new ArrayList<>());
		   return ResponseEntity.status(HttpStatus.OK).body(map);
	   }
	   Map<String,Object> map = new HashMap<>();
	   map.put("success",true);
	   map.put("message","Request complete");
	   ResponsePagerClass<SmeTransaction> page = ResponsePagerClass.<SmeTransaction>builder().page(smeTransactionspage)
				.build();
	   map.putAll(page.getPagingInfo());
	   map.put("transactions",smeTransactionspage.get().collect(Collectors.toList()));
	   return ResponseEntity.status(HttpStatus.OK).body(map);
   }
   
   
}
