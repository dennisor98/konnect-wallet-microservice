package net.sasakonnect.wallet.constant;

public class ChoiceEndpointsConstants {
	public static final String BASE_URL = "https://baas-pilot.choicebankapi.com";
	public static final String ON_BOARDING = "/onboarding/submitOnboardingRequest";
	public static final String BANK_CODES = "/staticData/getBankCodes";
	public static final String UPLOAD_ID_CARDS = "/onboarding/uploadIdCardPhoto";
	public static final String GET_METAMASK = "/onboarding/getMetaMapProperties";
	public static final String POLL_ONBOARDING = "/onboarding/getUserKyc";
	public static final String OPEN_WALLET_ACCOUNT = "/onboarding/submitEasyOnboardingRequest";
	public static final String GET_WALLET_INFO = "/onboarding/getOnboardingStatus";
	public static final String REQUEST_OTP_RESEND = "/sms/onboarding/resendOnboardSms";
	public static final String CONFIRM_OTP = "/sms/onboarding/confirmOnboardingRequest";
	public static final String GET_ACCOUNT_INFO = "/query/getAccountDetail";
	public static final String DEPOSIT_FROM_MPESA = "/trans/depositFromMpesa";
	public static final String GET_TRANSACTIONS = "/query/getTransList";
	public static final String CONFIRM_OTP_TRANSFER = "/trans/confirmTransfer";
	public static final String WITHDRAW = "/trans/applyForTransfer";
	public static final String BUY_AIRTIME = "/utilityPayment/airtimePayment";
	public static final String CHECK_BALANCE = "/query/getAccountDetails";
	public static final String SHORT_CODE = "/account/applyForShortCode";
	public static final String GET_SHORT_CODE = "/account/queryAccountByShortCode";
	public static final String GET_ONBOARDING_STATUS = "/onboarding/getOnboardingStatus";
	public static final String MPESA_TILL_AND_PAYBILL = "/trans/applyForMpesaBusinessTransfer";

}
