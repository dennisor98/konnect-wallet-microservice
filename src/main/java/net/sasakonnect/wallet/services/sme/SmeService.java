package net.sasakonnect.wallet.services.sme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import com.google.gson.Gson;

import net.sasakonnect.wallet.RequestDto.sme.ConfirmSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateEnterpriseDto;
import net.sasakonnect.wallet.RequestDto.sme.CreateSmeDto;
import net.sasakonnect.wallet.RequestDto.sme.LLCInformationDto;
import net.sasakonnect.wallet.beans.BankWebClientBean;
import net.sasakonnect.wallet.constant.ChoiceEndpointsConstants;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Enterprise;
import net.sasakonnect.wallet.domain.sme.SmeAccount;
import net.sasakonnect.wallet.domain.sme.SmeAccountDetails;
import net.sasakonnect.wallet.repository.sme.EnterpriseRepository;
import net.sasakonnect.wallet.repository.sme.SmeAccountRepository;
import net.sasakonnect.wallet.repository.sme.SmeInformationRepository;
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
	BankWebClientBean bankClientBean;
	@Autowired
	RequestSigner requestSigner;
	@Autowired
	ChoiceBankSmsService choiceBankSmsService;

	public Enterprise createEnterprise(CreateEnterpriseDto ced) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var enterprise = Enterprise.builder().name(ced.getName()).industry(ced.getIndustry()).mission(ced.getMission())
				.vision(ced.getVision()).ownership(ced.getOwnership()).creator(user).build();
		return this.enterpriseRepository.save(enterprise);

	}

	public Object getEnterprise(int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		var pagedEnterprices = enterpriseRepository.findAllWithCreator(pageable);
		Map<String, Object> enterpriseResponsePaged = new HashMap<>();
		Map<String, Object> paginationInfo = new HashMap<>();
		if (!pagedEnterprices.isEmpty()) {
			var enterprises = pagedEnterprices.getContent().stream().map(enterprise -> {
				var enterpriseResponse = new HashMap<String, Object>();
				enterpriseResponse.put("name", enterprise.getName());
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
				enterpriseResponse.put("account", sme.getAccountNo());
				enterpriseResponse.put("email", sme.getEmail());
				enterpriseResponse.put("createdOn", sme.getCreatedAt());
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
			var smeAccountBuild = SmeAccount.builder().mobile(createSmeSto.getMobile())
					.businessType(String.valueOf(createSmeSto.getBusinessType().getCode()))
					.countryCode(createSmeSto.getCountryCode()).otpType(createSmeSto.getOtpType())
					.enterprise(enterprise.get()).email(createSmeSto.getEmail()).build();
			Map<String, Object> paginationInfo = new HashMap<>();
			SmeAccount smeAccount = this.smeAccountRepository.save(smeAccountBuild);

			paginationInfo.put("countryCode", smeAccount.getCountryCode());
			paginationInfo.put("status", smeAccount.getStatus());
			paginationInfo.put("completeTime", smeAccount.getCompleteTime());
			paginationInfo.put("accountNo", smeAccount.getAccountNo());
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
					this.smeAccountRepository.delete(smeAccount);
				} else {
					smeAccount.setOnboardingRequestId(
							((Map<?, ?>) gson.get("data")).get("onboardingRequestId").toString());
					this.smeAccountRepository.save(smeAccount);
				}
				return gson;

			}
			return paginationInfo;
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object verifyOtpForSms(CreateSmeDto createSmeSto) {
		var enterprise = this.enterpriseRepository.findById(createSmeSto.getEnterprise_id());
		if (enterprise.isPresent()) {
			var smeAccountBuild = SmeAccount.builder().mobile(createSmeSto.getMobile())
					.businessType(String.valueOf(createSmeSto.getBusinessType().getCode()))
					.countryCode(createSmeSto.getCountryCode()).otpType(createSmeSto.getOtpType())
					.enterprise(enterprise.get()).email(createSmeSto.getEmail()).build();
			Map<String, Object> paginationInfo = new HashMap<>();
			SmeAccount smeAccount = this.smeAccountRepository.save(smeAccountBuild);

			paginationInfo.put("countryCode", smeAccount.getCountryCode());
			paginationInfo.put("status", smeAccount.getStatus());
			paginationInfo.put("completeTime", smeAccount.getCompleteTime());
			paginationInfo.put("accountNo", smeAccount.getAccountNo());
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
					this.smeAccountRepository.delete(smeAccount);
				} else {
					smeAccount.setOnboardingRequestId(
							((Map<?, ?>) gson.get("data")).get("onboardingRequestId").toString());
					this.smeAccountRepository.save(smeAccount);
				}
				return gson;

			}
			return paginationInfo;
		}

		// TODO Auto-generated method stub
		return null;
	}

	public Object registerLccInformation(LLCInformationDto createSmeSto) {
		var sme = this.smeAccountRepository.findSmeByOnboardingId(createSmeSto.getOnboardingRequestId());
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
		var sme = this.smeAccountRepository.findSmeByOnboardingId(confirmsmeDto.getSmeOnboardingId());
		if (sme.isPresent()) {
			return this.choiceBankSmsService.confirmOperation(sme.get().getOnboardingRequestId(),
					confirmsmeDto.getOtp());
			// TODO Auto-generated method stub

		}
		return null;
	}

}
