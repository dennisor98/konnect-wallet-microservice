package net.sasakonnect.wallet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.provider.SmsManager;

@Slf4j

@Service
public class SmsService {
	@Value("${spring.profiles.active}")
	String profileActive;

	@Value("${WALLET_TEMPLATE_INVITE}")
	private String template;
	private SmsManager smsManager;
	
	public SmsService(SmsManager smsManager) {
		this.smsManager = smsManager;
	}
	
	public void sendSms(String template,String contact) {
		StringBuilder stringbuilder = new StringBuilder();

		if (template == null) {
			template = this.template;
		}
		stringbuilder.append(template);
		log.info(stringbuilder.toString());
	    smsManager.sendMessage(stringbuilder.toString(),contact);
			
	}
}
