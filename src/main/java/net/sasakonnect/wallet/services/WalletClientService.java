package net.sasakonnect.wallet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.SdkPayDto;
import net.sasakonnect.wallet.RequestDto.SdkRequestOpenId;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.beans.RedisBean;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.WalletClientAccount;
import net.sasakonnect.wallet.repository.WalletClientAccountRepository;
import net.sasakonnect.wallet.repository.WalletClientRepository;
import net.sasakonnect.wallet.tools.Helper;
import net.sasakonnect.wallet.tools.JwtService;

@Slf4j
@Service
public class WalletClientService {
	private static final Logger logger = LoggerFactory.getLogger(WalletClientService.class);

	@Autowired
	WalletClientRepository wallectClientRepository;
	@Autowired
	WalletClientAccountRepository walletClientAccountRepository;
	@Autowired
	private ClientAppsBean clientAppsBean;
	@Autowired
	private WalletService walletService;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisBean<String> redisBean;
	@Autowired
	private JwtService jwtService;

	public WalletClient createWallectClientApp(WalletClientDTO walleClientDto) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		user.setUserWallets(null);
		user.setPins(null);
		user.setFirebaseTokens(null);
		user.setNotifications(null);
		user.setUserDevices(null);
		user.setUserPins(null);

		var wallectClientApp = WalletClient.builder().appName(walleClientDto.getAppName())
				.callBackUrl(walleClientDto.getCallBackUrl()).appDescription(walleClientDto.getAppDescription())
				.appKey(Helper.generateHashBasedUUID()).appSecret(Helper.generateHashBasedUUID().substring(12))
				.user(user).build();
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
					.tillNumber(walletClientAccount.getTillNumber()).walletClient(walletClient.get()).build();

			var walletclientAccount = this.walletClientAccountRepository.save(walletAccount);
			var client = walletClient.get();

			client.setWalletClientAccount(List.of(walletclientAccount));

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

	@Transactional
	public ResponseEntity<Object> findMerchantByClientAppKey(String client_app_key) {
		// TODO Auto-generated method stub
		var client = this.wallectClientRepository.findByAppKeyAnd(client_app_key);
		if (client.isPresent() && !client.get().isEmpty()) {
			var results = client.get().stream().map(merchant -> merchant.getAppName()).collect(Collectors.toList());
			Map<String, String> map = new HashMap<String, String>();

			map.put("success", "true");
			map.put("body", results.get(0));
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
		Map<String, String> map = new HashMap<String, String>();

		map.put("success", "false");
		map.put("body", null);

		return ResponseEntity.status(HttpStatus.OK).body(map);
	}

	@Transactional
	public Optional<List<WalletClient>> findMerchantByClientAppByKey(String client_app_key) {
		// TODO Auto-generated method stub
		return this.wallectClientRepository.findByAppKeyAnd(client_app_key);
	}

	@Transactional
	public Optional<List<WalletClient>> findMerchantByClientAppBySecret(String secretKey) {
		// TODO Auto-generated method stub
		return this.wallectClientRepository.findByAppSecret(secretKey);
	}

	public Object payThroughSdk(@Valid SdkPayDto sdkpayDto) {
		var clientApp = clientAppsBean.getWalletClient();
		logger.info("The Object is", clientApp);
		return this.walletService.requestWalletDeduction(sdkpayDto, clientApp);
	}
    public Object invokeStkPushToLoadWallet(String merchantAccount,String targetNo,int amount) {
    	return this.walletService.loadWalletFromMpesa(merchantAccount,targetNo,amount);
    }
	public ResponseEntity createOpenidSession(@Valid SdkRequestOpenId sdkRequestOpenId, WalletClient clientData) {

		if (this.userService.findUserByOpenId(sdkRequestOpenId.getOpen_id()).isEmpty()) {
			Map<String, Object> map = new HashMap<>();

			map.put("success", false);
			map.put("message", "Unrecorgised open id");

			map.put("code", "404");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
		}

		if (redisBean.getRecord(sdkRequestOpenId.getPublicKey()).isEmpty()) {
			redisBean.storeRecord(sdkRequestOpenId.getPublicKey(), clientData.getId());
			Map<String, Object> map = new HashMap<>();

			map.put("success", true);
			map.put("message", "Session created");
			map.put("key", sdkRequestOpenId.getPublicKey());

			map.put("code", "200");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
		Map<String, Object> map = new HashMap<>();

		map.put("success", true);
		map.put("message", "Session exist");
		map.put("key", sdkRequestOpenId.getPublicKey());

		map.put("code", "423");
		return ResponseEntity.status(HttpStatus.LOCKED).body(map);

	}

}
