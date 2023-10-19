package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.WalletClientAccount;
import net.sasakonnect.wallet.repository.WalletClientAccountRepository;
import net.sasakonnect.wallet.repository.WalletClientRepository;
import net.sasakonnect.wallet.tools.Helper;

@Service
public class WalletClientService {
	@Autowired
	WalletClientRepository wallectClientRepository;
	@Autowired
	WalletClientAccountRepository walletClientAccountRepository;
	@Autowired
	private ClientAppsBean clientAppsBean;
	@Autowired
	private WalletService walletService;

	public WalletClient createWallectClientApp(WalletClientDTO walleClientDto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setUserWallets(null);
		user.setPins(null);
		user.setFirebaseTokens(null);
		user.setNotifications(null);
		user.setUserDevices(null);
		user.setUserPins(null);

		var wallectClientApp = WalletClient.builder().appName(walleClientDto.getAppName())
				.appDescription(walleClientDto.getAppDescription()).appKey(Helper.generateHashBasedUUID())
				.appSecret(Helper.generateHashBasedUUID().substring(12)).user(user).build();
		return this.wallectClientRepository.save(wallectClientApp);

	}

	@Transactional
	public Object createWalletClientAccount(@Valid WalletClientAccountDto walletClientAccount) {
		Optional<WalletClient> walletClient = this.wallectClientRepository.findById(walletClientAccount.getAppId());
		if (walletClient.isPresent()) {
			var walletAccount = WalletClientAccount.builder().accountType(walletClientAccount.getAccountType())
					.bankCode(walletClientAccount.getBankCode())
					.payBillAccountNo(walletClientAccount.getPayBillAccountNo())
					.paybillNumber(walletClientAccount.getPaybillNumber())
					.tillNumber(walletClientAccount.getTillNumber()).walletClient(List.of(walletClient.get())).build();

			var walletclientAccount = this.walletClientAccountRepository.save(walletAccount);
			var client = walletClient.get();
			client.setWalletClientAccount(walletclientAccount);

			this.wallectClientRepository.save(client);
			Map<String, String> map = new HashMap<String, String>();

			map.put("success", "true");

			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<List<WalletClient>> findBuyKeyAndSecret(String client_app_key, String appSecret) {
		// TODO Auto-generated method stub
		return this.wallectClientRepository.findByAppKeyAndAppSecret(client_app_key, appSecret);
	}

	public Object payThroughSdk(@Valid SdkPayDto sdkpayDto) {
		var clientApp = clientAppsBean.getWalletClient();
		return this.walletService.requestWalletDeduction(sdkpayDto, clientApp);
		// TODO Auto-generated method stub
		// return null;
	}

}
