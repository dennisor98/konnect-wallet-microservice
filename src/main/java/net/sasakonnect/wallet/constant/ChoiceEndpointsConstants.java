package net.sasakonnect.wallet.constant;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ChoiceEndpointsConstants {
///production url https://baas.choicedigitalbank.com
	// development https://baas-pilot.choicebankapi.com/
//	public static final String BASE_URL = "https://baas-pilot.choicedigitalbank.com";
	public static final String ON_BOARDING = "/onboarding/submitOnboardingRequest";
	public static final String BANK_CODES = "/staticData/getBankCodes";
	public static final String UPLOAD_ID_CARDS = "/onboarding/uploadIdCardPhoto";
	public static final String GET_METAMASK = "/onboarding/getMetaMapProperties";
	public static final String POLL_ONBOARDING = "/onboarding/getUserKyc";
	public static final String OPEN_WALLET_ACCOUNT = "/onboarding/v2/submitEasyOnboardingRequest";
	public static final String GET_WALLET_INFO = "/onboarding/getOnboardingStatus";
	public static final String REQUEST_OTP_RESEND = "/common/resendOtp";
	public static final String CONFIRM_OTP = "/common/confirmOperation";
	public static final String GET_ACCOUNT_INFO = "/query/getAccountDetail";
	public static final String DEPOSIT_FROM_MPESA = "/trans/depositFromMpesa";
	public static final String GET_TRANSACTIONS = "/query/getTransList";
	public static final String CONFIRM_OTP_TRANSFER = "/common/confirmOperation";
	public static final String REQUEST_BANK_STATEMENT = "/statement/applyBankAccountStatement";
	public static final String ADD_OR_UPDATE_EMAIL = "/user/addOrUpdateEmail";
	public static final String REQUEST_BANK_STATEMENT_CSV = "/statement/applyAccountStatement";
	public static final String COMMON_SEND_OTP = "/common/sendOtp";
	public static final String WITHDRAW = "/trans/v2/applyForTransfer";
	public static final String BUY_AIRTIME = "/utilityPayment/v2/airtimePayment";
	public static final String PAY_UTILITY = "/utilityPayment/v2/billPayment";
	public static final String CHECK_BALANCE = "/query/getAccountDetails";
	public static final String SHORT_CODE = "/account/applyForShortCode";
	public static final String GET_SHORT_CODE = "/account/queryAccountByShortCode";
	public static final String GET_ONBOARDING_STATUS = "/onboarding/getOnboardingStatus";
	public static final String MPESA_TILL_AND_PAYBILL = "/trans/v2/applyForMpesaBusinessTransfer";
	public static final String GET_TRANSACTION_STATUS = "/query/getTransResult";
	public static final String UPGRADE_ACCOUNT = "/onboarding/walletAccountUpgrade";
	public static final String APPLY_FOR_SME = "/onboarding/business/applyForSmeOnboarding";
	public static final String APPLY_FOR_MULTIPLE_SME_ACCOUNT="/account/businessOpenAccount";
	public static final String VERIFY_SME_MULTIPLE_ACCOUNT_OTP="/account/confirmAccountApplicationOtp";
	public static final String UPDATE_LLC_SME_INFO = "/onboarding/business/submitCompanyOnboardingRequest";
	public static final String UPLOAD_SME_DOCUMENT = "/onboarding/business/uploadMedia";
	public static final String UPLOAD_SME_MEMBER = "/onboarding/business/submitCompanyMember";
	public static final String SUBMIT_SME_FOR_CONFIRMATION = "/onboarding/business/submitOrPullBackRequest";

}
