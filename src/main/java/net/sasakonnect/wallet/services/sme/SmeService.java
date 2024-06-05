package net.sasakonnect.wallet.services.sme;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.sme.ConfirmSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateEnterpriseDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.LLCInformationDto;
import net.sasakonnect.wallet.RequestDto.sme.LlcSmeMemberDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeAccountDocuments;
import net.sasakonnect.wallet.RequestDto.sme.SubmitSmeAccount;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Enterprise;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeAccountDetails;
import net.sasakonnect.wallet.domain.sme.SmeMember;
import net.sasakonnect.wallet.notification.MultipleAccountOpeningResultNotification;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;
import net.sasakonnect.wallet.enums.sme.BusinessType;
import net.sasakonnect.wallet.enums.sme.OperatingMode;
import net.sasakonnect.wallet.enums.sme.SmeDocumentContentType;
import net.sasakonnect.wallet.enums.sme.SmeDocumentMediaType;
import net.sasakonnect.wallet.notification.NotificationResult;
import net.sasakonnect.wallet.notification.SmeAccountOpeningResultNotification;
import net.sasakonnect.wallet.repository.LogsRepository;
import net.sasakonnect.wallet.repository.SmeMemberRepository;
import net.sasakonnect.wallet.repository.UserRepository;
import net.sasakonnect.wallet.repository.sme.EnterpriseRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRepository;
import net.sasakonnect.wallet.repository.sme.SmeInformationRepository;
import net.sasakonnect.wallet.repository.sme.SmeRepository;
import net.sasakonnect.wallet.services.ChoiceBankSmsService;
import net.sasakonnect.wallet.tools.RequestSigner;
import reactor.core.publisher.Mono;

@Service
public class SmeService {

	@Autowired
	EnterpriseRepository enterpriseRepository;
	@Autowired
	SmeAccountRepository smeAccountRepository;

	@Autowired
	SmeInformationRepository smeAccountInfoRepository;
	@Autowired
	SmeRepository smeRepository;

	@Autowired
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	ChoiceBankSmsService choiceBankSmsService;
	@Autowired
	LogsRepository logsRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SmeMemberRepository smeMemberRepository;

	public ResponseEntity<Object> createEnterprise(CreateEnterpriseDto ced) {
		
		if(findEnterpriseByName(ced.getName()).isPresent()) {
		   Map<String,Object> map = new HashMap<>();
		   map.put("success",false);
		   map.put("message","Enterprise name ,"+ced.getName()+" is not availabe");
		   
		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		try {
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	     	var enterprise = Enterprise.builder().name(ced.getName()).industry(ced.getIndustry()).mission(ced.getMission())
				.vision(ced.getVision()).ownership(ced.getOwnership()).creator(user).build();
	     	  this.enterpriseRepository.save(enterprise);
	     	Map<String,Object> map = new HashMap<>();
	     	map.put("success",true);
	     	map.put("message","Enterprise created");
	     	return ResponseEntity.status(HttpStatus.OK).body(map);
		}catch(Exception ex) {
			ex.printStackTrace();
			Map<String,Object> map = new HashMap<>();
	     	map.put("success",false);
	     	map.put("message","Something went wrong on creating enterprise");
	     	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);	
		}
       

	}
	
	private Optional<Enterprise> findEnterpriseByName(String name){
		return 	this.enterpriseRepository.findByName(name);
	}

	public ResponseEntity<Object> getEnterprise(int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		var pagedEnterprices = enterpriseRepository.findAllWithCreator(pageable);
		Map<String, Object> enterpriseResponsePaged = new HashMap<>();
		Map<String, Object> paginationInfo = new HashMap<>();
		if (!pagedEnterprices.isEmpty()) {
			var enterprises = pagedEnterprices.getContent().stream().map(enterprise -> {
				var enterpriseResponse = new HashMap<String, Object>();
				enterpriseResponse.put("name", enterprise.getName());
				enterpriseResponse.put("id", enterprise.getId());

				enterpriseResponse.put("industry", enterprise.getIndustry());
				enterpriseResponse.put("ownership", enterprise.getOwnership());
				enterpriseResponse.put("mission", enterprise.getMission());
				enterpriseResponse.put("createdBy",
						enterprise.getCreator().getFirstName() + " " + enterprise.getCreator().getLastName());
				return enterpriseResponse;
			}).collect(Collectors.toList());

			paginationInfo.put("currentPage", pagedEnterprices.getNumber());
			paginationInfo.put("totalItems", pagedEnterprices.getTotalElements());
			paginationInfo.put("totalPages", pagedEnterprices.getTotalPages());
			paginationInfo.put("pageSize", pagedEnterprices.getSize());
			paginationInfo.put("hasNext", pagedEnterprices.hasNext());
			paginationInfo.put("nextPage",
					pagedEnterprices.hasNext() ? pagedEnterprices.nextPageable().getPageNumber() : 0);
			paginationInfo.put("previusPage",
					pagedEnterprices.hasPrevious() ? pagedEnterprices.previousOrFirstPageable().getPageNumber() : null);

			enterpriseResponsePaged.put("page", paginationInfo);
			enterpriseResponsePaged.put("payload", enterprises);

		} else {
			paginationInfo.put("currentPage", 0);
			paginationInfo.put("totalItems", 0);
			paginationInfo.put("totalPages", 0);
			paginationInfo.put("pageSize", 0);
			enterpriseResponsePaged.put("page", paginationInfo);
			enterpriseResponsePaged.put("payload", new ArrayList());

		}

		return ResponseEntity.ok(enterpriseResponsePaged);
	}

	public Object getSmeAccount(int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		var pagedsme = smeAccountRepository.findAllWithEnterprise(pageable);
		Map<String, Object> enterpriseResponsePaged = new HashMap<>();
		Map<String, Object> paginationInfo = new HashMap<>();
		if (!pagedsme.isEmpty()) {
			var enterprises = pagedsme.getContent().stream().map(sme -> {
				var enterpriseResponse = new HashMap<String, Object>();
				// enterpriseResponse.put("account", sme.getAccountNo());
				// enterpriseResponse.put("email", sme.getEmail());
				enterpriseResponse.put("name", sme.getSme().getAccountDetails().getBusinessName());
				enterpriseResponse.put("createdOn", sme.getCreatedAt());
				enterpriseResponse.put("id", sme.id);

				return enterpriseResponse;
			}).collect(Collectors.toList());

			paginationInfo.put("currentPage", pagedsme.getNumber());
			paginationInfo.put("totalItems", pagedsme.getTotalElements());
			paginationInfo.put("totalPages", pagedsme.getTotalPages());
			paginationInfo.put("pageSize", pagedsme.getSize());
			paginationInfo.put("hasNext", pagedsme.hasNext());
			paginationInfo.put("nextPage", pagedsme.hasNext() ? pagedsme.nextPageable().getPageNumber() : 0);
			paginationInfo.put("previusPage",
					pagedsme.hasPrevious() ? pagedsme.previousOrFirstPageable().getPageNumber() : null);

			enterpriseResponsePaged.put("page", paginationInfo);
			enterpriseResponsePaged.put("payload", enterprises);

		} else {
			paginationInfo.put("currentPage", 0);
			paginationInfo.put("totalItems", 0);
			paginationInfo.put("totalPages", 0);
			paginationInfo.put("pageSize", 0);
			enterpriseResponsePaged.put("page", paginationInfo);
			enterpriseResponsePaged.put("payload", new ArrayList());

		}

		return ResponseEntity.ok(enterpriseResponsePaged);
	}

	public Object createSme(CreateSmeDto createSmeSto) {
		var enterprise = this.enterpriseRepository.findById(createSmeSto.getEnterprise_id());
		if (enterprise.isPresent()) {
			var smeAccountBuild = Sme.builder().mobile(createSmeSto.getMobile())
					.businessType(String.valueOf(createSmeSto.getBusinessType().getCode()))
					.mobile(createSmeSto.getMobile()).countryCode(createSmeSto.getCountryCode())
					.otpType(createSmeSto.getOtpType()).enterprise(enterprise.get()).email(createSmeSto.getEmail())
					.build();
			Map<String, Object> paginationInfo = new HashMap<>();
			Sme smeAccount = this.smeRepository.save(smeAccountBuild);

			paginationInfo.put("countryCode", smeAccount.getCountryCode());
			paginationInfo.put("status", smeAccount.getStatus());
			paginationInfo.put("completeTime", smeAccount.getCompleteTime());
			paginationInfo.put("businessType", smeAccount.getBusinessType());
			paginationInfo.put("mobile", smeAccount.getMobile());
			paginationInfo.put("email", smeAccount.getEmail());
			paginationInfo.put("otpType", smeAccount.getOtpType());
			paginationInfo.put("onboardingRequestId", smeAccount.getOnboardingRequestId());

			var reqId = new HashMap<String, Object>();
			reqId.put("userId", smeAccount.getId());
			reqId.put("countryCode", smeAccount.getCountryCode());
			reqId.put("businessType", smeAccount.getBusinessType());
			reqId.put("mobile", smeAccount.getMobile());
			reqId.put("email", smeAccount.getEmail());
			reqId.put("otpType", smeAccount.getOtpType());

			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.APPLY_FOR_SME)
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				var gson = new Gson().fromJson(responseJson, HashMap.class);
				if (!((String) gson.get("code")).equalsIgnoreCase("00000")) {
					this.smeRepository.delete(smeAccount);
				} else {
					smeAccount.setOnboardingRequestId(
							((Map<?, ?>) gson.get("data")).get("onboardingRequestId").toString());
					this.smeRepository.save(smeAccount);
				}
				return gson;

			}
			return paginationInfo;
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object uploadSmeAccountDocuments(SmeAccountDocuments smeInfo) {

		var sme = this.smeRepository.findSmeByOnboardingId(smeInfo.getOnboardingRequestId());
		if (sme.isPresent()) {

			var reqId = new HashMap<String, Object>();
			reqId.put("onboardingRequestId", sme.get().getOnboardingRequestId());
			reqId.put("mediaBase64", smeInfo.getBase64Document());
			reqId.put("mediaType", smeInfo.getMediaType().name());
			reqId.put("contentType", smeInfo.getContentType().getValue());

			var reqs = requestSigner.signRequest(reqId);

			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.UPLOAD_SME_DOCUMENT).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				var gson = new Gson().fromJson(responseJson, HashMap.class);

				return gson;

			}
		}

		// TODO Auto-generated method stub
		return null;
	}

//	public Object verifyOtpForSms(CreateSmeDto createSmeSto) {
//		var enterprise = this.enterpriseRepository.findById(createSmeSto.getEnterprise_id());
//		if (enterprise.isPresent()) {
//			var smeAccountBuild = Sme.builder().mobile(createSmeSto.getMobile())
//					.businessType(String.valueOf(createSmeSto.getBusinessType().getCode()))
//					.countryCode(createSmeSto.getCountryCode()).otpType(createSmeSto.getOtpType())
//					.enterprise(enterprise.get()).email(createSmeSto.getEmail()).build();
//			Map<String, Object> paginationInfo = new HashMap<>();
//			Sme smeAccount = this.smeRepository.save(smeAccountBuild);
//
//			paginationInfo.put("countryCode", smeAccount.getCountryCode());
//			paginationInfo.put("status", smeAccount.getStatus());
//			paginationInfo.put("completeTime", smeAccount.getCompleteTime());
//			paginationInfo.put("businessType", smeAccount.getBusinessType());
//			paginationInfo.put("mobile", smeAccount.getMobile());
//			paginationInfo.put("email", smeAccount.getEmail());
//			paginationInfo.put("otpType", smeAccount.getOtpType());
//			paginationInfo.put("onboardingRequestId", smeAccount.getOnboardingRequestId());
//
//			var reqId = new HashMap<String, Object>();
//			reqId.put("userId", smeAccount.getId());
//			reqId.put("countryCode", smeAccount.getCountryCode());
//			reqId.put("businessType", smeAccount.getBusinessType());
//			reqId.put("mobile", smeAccount.getMobile());
//			reqId.put("email", smeAccount.getEmail());
//			reqId.put("otpType", smeAccount.getOtpType());
//
//			var reqs = requestSigner.signRequest(reqId);
//
//			Mono<String> responseMono = this.bankClientBean.webClient.post().uri(ChoiceEndpointsConstants.APPLY_FOR_SME)
//					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(reqs))
//					.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String.class);
//
//			String responseJson = responseMono.block();
//
//			if (responseJson != null) {
//				var gson = new Gson().fromJson(responseJson, HashMap.class);
//				if (!((String) gson.get("code")).equalsIgnoreCase("00000")) {
//					this.smeAccountRepository.delete(smeAccount);
//				} else {
//					smeAccount.setOnboardingRequestId(
//							((Map<?, ?>) gson.get("data")).get("onboardingRequestId").toString());
//					this.smeAccountRepository.save(smeAccount);
//				}
//				return gson;
//
//			}
//			return paginationInfo;
//		}
//
//		// TODO Auto-generated method stub
//		return null;
//	}

	public Object registerLLcMember(LlcSmeMemberDto lccSmemeberDto) {
		var sme = this.smeRepository.findSmeById(lccSmemeberDto.getSme_id());
		if (sme.isPresent()) {
			var foundSme = sme.get();
			var user = this.userRepository.findUserWithWalletsById(lccSmemeberDto.getUser_id());
			if (user.isPresent()) {
				var foundUser = user.get();
				HashMap<String, Object> registrationData = new HashMap<>();

				// Mandatory fields
				registrationData.put("onboardingRequestId", foundSme.getOnboardingRequestId());
				registrationData.put("userId", foundSme.id);
				registrationData.put("idType", foundUser.getIdType().evaluateId()); // Kenyan ID example
				registrationData.put("firstName", foundUser.getFirstName());
				registrationData.put("lastName", foundUser.getLastName());
				registrationData.put("idNumber", foundUser.getIdNumber());
				registrationData.put("gender", foundUser.getGender().getValue()); // Male
				registrationData.put("countryCode", foundUser.getCountryCode());
				registrationData.put("mobile", foundUser.getMobile());
				registrationData.put("kraPin", foundUser.getKraPin());
				registrationData.put("idFrontSideFile", lccSmemeberDto.getIdFrontSideFile());
				registrationData.put("idFrontSideFileType", lccSmemeberDto.getIdFrontSideFileType().getValue());
				registrationData.put("idBackSideFile", lccSmemeberDto.getIdBackSideFile());
				registrationData.put("idBackSideFileType", lccSmemeberDto.getIdBackSideFileType().getValue());
				registrationData.put("selfieFile", lccSmemeberDto.getSelfieFile());
				registrationData.put("selfieFileType", lccSmemeberDto.getSelfieFileType().getValue());
				registrationData.put("kraPinFile", lccSmemeberDto.getKraPin());
				registrationData.put("kraPinFileType", lccSmemeberDto.getKraPinFileType().getValue());
				var reqs = requestSigner.signRequest(registrationData);

				Mono<String> responseMono = this.bankClientBean.webClient.post()
						.uri(ChoiceEndpointsConstants.UPLOAD_SME_MEMBER).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
						.bodyToMono(String.class);
				String responseJson = responseMono.block();
				if (responseJson != null) {
					var gson = new Gson().fromJson(responseJson, HashMap.class);
					if (((String) gson.get("code")).equalsIgnoreCase("00000")) {
						var memberid = ((Map<?, ?>) gson.get("data")).get("memberId").toString();
						SmeMember smeMember = SmeMember.builder().member_id(memberid).sme_account(foundSme)
								.user(foundUser).build();

						this.smeMemberRepository.save(smeMember);

						// this.smeAccountInfoRepository.delete(smeInfo);
					} else {

					}
					return gson;

				}

			}

		}
		return "sme account not found";

	}

	public Object registerLccInformation(LLCInformationDto createSmeSto) {
		var sme = this.smeRepository.findSmeByOnboardingId(createSmeSto.getOnboardingRequestId());
		if (sme.isPresent()) {
			var smeInformation = SmeAccountDetails.builder().account(sme.get())
					.businessAddress(createSmeSto.getBusinessAddress()).businessName(createSmeSto.getBusinessName())
					.businessIndustry(createSmeSto.getBusinessIndustry()).operatingMode(createSmeSto.getOperatingMode())
					.businessCerNum(createSmeSto.getBusinessCerNum()).kraPin(createSmeSto.getKraPin()).build();
			var smeInfo = smeAccountInfoRepository.save(smeInformation);
			var reqId = new HashMap<String, Object>();
			reqId.put("onboardingRequestId", sme.get().getOnboardingRequestId());
			reqId.put("businessName", smeInfo.getBusinessName());
			reqId.put("businessCerNum", smeInfo.getBusinessCerNum());
			reqId.put("kraPin", smeInfo.getKraPin());
			reqId.put("operatingMode", smeInfo.getOperatingMode().getValue());
			reqId.put("businessIndustry", smeInfo.getBusinessIndustry().getValue());
			reqId.put("businessAddress", smeInfo.getBusinessAddress());

			var reqs = requestSigner.signRequest(reqId);
			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.UPDATE_LLC_SME_INFO).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();

			if (responseJson != null) {
				var gson = new Gson().fromJson(responseJson, HashMap.class);
				if (!((String) gson.get("code")).equalsIgnoreCase("00000")) {
					this.smeAccountInfoRepository.delete(smeInfo);
				} else {

				}
				return gson;

			}
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object confirmSmePhoneNumber(ConfirmSmeDto confirmsmeDto) {
		var sme = this.smeRepository.findSmeByOnboardingId(confirmsmeDto.getSmeOnboardingId());
		if (sme.isPresent()) {
			return this.choiceBankSmsService.confirmOperation(sme.get().getOnboardingRequestId(),
					confirmsmeDto.getOtp());
			// TODO Auto-generated method stub

		}
		return null;
	}

	public void updateAccountinfo(NotificationResult<SmeAccountOpeningResultNotification> results) {
		var body = results.getParams();
		if (body.getOnboardingRequestId() != null) {
			var sme = this.smeRepository.findSmeByOnboardingId(body.getOnboardingRequestId());
			var smedata = sme.get();
			var smeAccount = SmeAccount.builder().accountName("default_Account").accountNo(body.getAccountId())
					.sme(smedata).build();
			smedata.getSmeAccounts().add(smeAccount);
			this.smeRepository.save(smedata);

		} else {
			// this.smeAccountRepository.save(null)
		}

	}

	public void updateMulitpleAccountinfo(NotificationResult<MultipleAccountOpeningResultNotification> results) {
		var body = results.getParams();

		var smeAccount = this.smeAccountRepository.findSmeAccountByApplicationId(body.getApplicationId());
		if (smeAccount.isPresent()) {
			var acc = smeAccount.get();
			acc.setAccountNo(body.getAccountId());
			acc.setAccountName(body.getAccountName());
			this.smeAccountRepository.save(acc);
		}
		// TODO Auto-generated method stub

	}

	public Object submitSmeMaterials(@Valid SubmitSmeAccount document) {
		var smeRepo = this.smeRepository.findSmeByOnboardingId(document.getOnboardingRequestId());
		if (!smeRepo.isEmpty()) {
			var sm = smeRepo.get();
			var reqId = new HashMap<String, Object>();
			reqId.put("onboardingRequestId", sm.getOnboardingRequestId());
			reqId.put("action", 1);

			var reqs = requestSigner.signRequest(reqId);
			Mono<String> responseMono = this.bankClientBean.webClient.post()
					.uri(ChoiceEndpointsConstants.SUBMIT_SME_FOR_CONFIRMATION).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(reqs)).accept(MediaType.APPLICATION_JSON).retrieve()
					.bodyToMono(String.class);

			String responseJson = responseMono.block();
			return responseJson;

		}
		return null;

	}

	
	//001
	public ResponseEntity<Object> getBusinessTypes(){
		EnumSet<BusinessType> bsType = EnumSet.allOf(BusinessType.class);
		ArrayList<BusinessType> bsTypeList = new ArrayList<>(bsType);
		Map<String,Object> map = new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("businessTypes", bsTypeList);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	
	//002
	public ResponseEntity<Object> getIndustryTypes(){
		EnumSet<BusinessIndustry> bsIndustry = EnumSet.allOf(BusinessIndustry.class);
		ArrayList<BusinessIndustry> bsIndustryList = new ArrayList<>(bsIndustry);
		Map<String,Object> map = new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("industryTypes", bsIndustryList);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	
	//003
	public ResponseEntity<Object> getOperatingModes(){
		EnumSet<OperatingMode> enumSet = EnumSet.allOf(OperatingMode.class);
		ArrayList<OperatingMode> operatingModes = new ArrayList<>(enumSet);
		Map<String,Object> map = new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("operatingModes", operatingModes);
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	
	//004
	public ResponseEntity<Object> getSmeDocumentTypes(){
		EnumSet<SmeDocumentContentType> enumSet = EnumSet.allOf(SmeDocumentContentType.class);
		ArrayList<SmeDocumentContentType> documentType = new ArrayList<>(enumSet);
		Map<String,Object> map = new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("documentTypes", documentType);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	
	//005
	public ResponseEntity<Object> getSmeDocumentMediaTypes(){
		EnumSet<SmeDocumentMediaType> enumSet = EnumSet.allOf(SmeDocumentMediaType.class);
		ArrayList<SmeDocumentMediaType> documentType = new ArrayList<>(enumSet);
		Map<String,Object> map = new HashMap<>();
		map.put("success",true);
		map.put("message","Request successful");
		map.put("documentMediaTypes", documentType);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	
}
