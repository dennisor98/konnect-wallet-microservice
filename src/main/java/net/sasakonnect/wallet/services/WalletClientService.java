package net.sasakonnect.wallet.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import net.sasakonnect.wallet.RequestDto.WalletClientAccountUpdateDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.RequestDto.WalletClientUpdateDto;
import net.sasakonnect.wallet.ResponseDto.TransactionResponseDto;
import net.sasakonnect.wallet.beans.ClientAppsBean;
import net.sasakonnect.wallet.beans.RedisBean;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.WalletClient;
import net.sasakonnect.wallet.domain.WalletClientAccount;
import net.sasakonnect.wallet.repository.WalletClientAccountRepository;
import net.sasakonnect.wallet.repository.WalletClientRepository;
import net.sasakonnect.wallet.tools.Helper;
import net.sasakonnect.wallet.tools.JwtService;
import net.sasakonnect.wallet.tools.ResponsePagerClass;

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
	private SdkWalletService walletService;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisBean<String> redisBean;
	@Autowired
	private JwtService jwtService;

	public ResponseEntity<Object> createWallectClientApp(WalletClientDTO walleClientDto) {
		try {
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			user.setUserWallets(null);
			user.setPins(null);
			user.setFirebaseTokens(null);
			user.setNotifications(null);
			user.setUserDevices(null);
			user.setUserPins(null);
			user.setUserRole(null);
			var wallectClientApp = WalletClient.builder().appName(walleClientDto.getAppName())
					.callBackUrl(walleClientDto.getCallBackUrl()).appDescription(walleClientDto.getAppDescription())
					.appKey(Helper.generateHashBasedUUID()).appSecret(Helper.generateHashBasedUUID().substring(12))
					.user(user).build();
			this.wallectClientRepository.save(wallectClientApp);
			wallectClientApp.setUser(null);
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Client created successfully");
			map.put("wallectClient", wallectClientApp);
			return ResponseEntity.status(HttpStatus.OK).body(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}

	public ResponseEntity<Object> deleteWalletClient(String clientId) {
		Optional<WalletClient> walletClient = this.wallectClientRepository.findById(clientId);
		if (walletClient.isPresent()) {
			this.wallectClientRepository.delete(walletClient.get());
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Client deleted");

			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("success", false);
		map.put("message", "Client not found");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}

	@Transactional
	public Object createWalletClientAccount(@Valid WalletClientAccountDto walletClientAccount) {
		Optional<WalletClient> walletClient = this.wallectClientRepository.findById(walletClientAccount.getAppId());
		if (walletClient.isPresent()) {
			var walletAccount = WalletClientAccount.builder().accountType(walletClientAccount.getAccountType())
					.bankCode(walletClientAccount.getBankCode())
					.payBillAccountNo(walletClientAccount.getPayBillAccountNo())
					.paybillNumber(walletClientAccount.getPaybillNumber()).isPrimary(false)
					.tillNumber(walletClientAccount.getTillNumber()).walletClient(walletClient.get()).build();

			var walletclientAccount = this.walletClientAccountRepository.save(walletAccount);
			var client = walletClient.get();
			client.getWalletClientAccount().add(walletclientAccount);
			this.wallectClientRepository.save(client);
			Map<String, String> map = new HashMap<String, String>();
			map.put("success", "true");
			map.put("message", "client account created for " + client.getAppName());

			return ResponseEntity.status(HttpStatus.OK).body(map);
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Object updateWalletClientApp(WalletClientUpdateDto walletClientUpdateDto) {
		try {
			var walletClientApp = this.wallectClientRepository.findById(walletClientUpdateDto.getId());
			if (walletClientApp.isPresent()) {
				var app = walletClientApp.get();

				app.setAppDescription(walletClientUpdateDto.getDescription());
				app.setAppName(walletClientUpdateDto.getAppName());
				app.setAppKey(walletClientUpdateDto.getAppKey());
				app.setAppSecret(walletClientUpdateDto.getAppSecret());
				app.setCallBackUrl(walletClientUpdateDto.getCallBackUrl());
				app.setEnabled(walletClientUpdateDto.getEnabled());
				this.wallectClientRepository.save(app);

				Map<String, Object> map = new HashMap<>();
				map.put("success", true);
				map.put("message", "Client update successful");
				return ResponseEntity.status(HttpStatus.OK).body(map);

			}
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Client not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);

		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}
	}

	public ResponseEntity<Object> getWalletClients(Integer pageNumber, Integer pageSize) {
		try {
			Page<WalletClient> clients = this.wallectClientRepository
					.findAllByOrderByUpdatedAtDesc(PageRequest.of(pageNumber, pageSize));
			if (!clients.isEmpty()) {
				Map<String, Object> map = new HashMap<>();
				map.put("success", true);
				map.put("message", "Request completed successful");
				ResponsePagerClass<WalletClient> page = ResponsePagerClass.<WalletClient>builder().page(clients)
						.build();
				map.putAll(page.getPagingInfo());
				var clientsMap = clients.stream().map(cl -> {
					Map<String, Object> cMap = new HashMap<>();
					cMap.put("id", cl.getId());
					cMap.put("createdAt", cl.getCreatedAt());
					cMap.put("updatedAt", cl.getUpdatedAt());
					cMap.put("description", cl.getAppDescription());
					cMap.put("appName", cl.getAppName());
					cMap.put("appKey", cl.getAppKey());
					cMap.put("appSecret", cl.getAppSecret());
					cMap.put("callBackUrl", cl.getCallBackUrl());
					cMap.put("enabled", cl.getEnabled());
					cMap.put("accounts", cl.getWalletClientAccount().stream().map(a -> {
						Map<String, Object> aMap = new HashMap<>();
						aMap.put("createdAt", a.getCreatedAt());
						aMap.put("updatedAt", a.getUpdatedAt());
						aMap.put("id", a.getId());
						aMap.put("isPrimary", a.getIsPrimary());
						aMap.put("payBillNumber", a.getPaybillNumber());
						aMap.put("payBillAccountNumber", a.getPayBillAccountNo());
						aMap.put("tillNumber", a.getTillNumber());
						aMap.put("walletAccountNo", a.getWalletAccountNo());
						aMap.put("bankCode", a.getBankCode());
						aMap.put("bankAccount", a.getBankAccount());
						return aMap;
					}).collect(Collectors.toList()));
					return cMap;
				}).collect(Collectors.toList());
				map.put("walletClients", clientsMap);

				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Request completed successful");
			map.put("walletClients", new ArrayList<>());
			return ResponseEntity.status(HttpStatus.OK).body(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}
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

	public TransactionResponseDto payThroughSdk(@Valid SdkPayDto sdkpayDto) {
		var clientApp = clientAppsBean.getWalletClient();
		logger.info("The Object is", clientApp);
		return this.walletService.requestWalletDeduction(sdkpayDto, clientApp);
	}

	public Object invokeStkPushToLoadWallet(String merchantAccount, String targetNo, int amount) {
		return this.walletService.loadWalletFromMpesa(merchantAccount, targetNo, amount);
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

	@Transactional
	public ResponseEntity<Object> updatePrimaryWalletClientAccount(WalletClientAccountUpdateDto clientAccount) {
		try {
			Optional<WalletClient> walletClient = this.wallectClientRepository
					.findById(clientAccount.getWalletClientAccountId());

			if (walletClient.isPresent()) {
				Optional<WalletClientAccount> currentprimaryAcc = this.walletClientAccountRepository
						.findPrimaryWalletClientaccount(clientAccount.getWalletClientId());
				Optional<WalletClientAccount> newprimaryAcc = this.walletClientAccountRepository
						.findById(clientAccount.getWalletClientAccountId());
				if (currentprimaryAcc.isPresent() && newprimaryAcc.isPresent()) {
					var pc = currentprimaryAcc.get();
					pc.setIsPrimary(false);
					this.walletClientAccountRepository.save(pc);
					var npc = newprimaryAcc.get();
					npc.setIsPrimary(true);
					this.walletClientAccountRepository.save(npc);
				}
				Map<String, Object> map = new HashMap<>();
				map.put("success", true);
				map.put("message", "Primary account updated for client");
				var npc = newprimaryAcc.get();
				npc.setIsPrimary(true);
				this.walletClientAccountRepository.save(npc);
				return ResponseEntity.status(HttpStatus.OK).body(map);
			}
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Wallet client not found");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}

	}

	public ResponseEntity<Object> updateWalletClientAccount(WalletClientAccountDto clientAccountPayload) {
		Optional<WalletClientAccount> clientAccount = this.walletClientAccountRepository
				.findById(clientAccountPayload.getAppId());
		if (clientAccount.isEmpty()) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Client account not found");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}
		try {
			WalletClientAccount account = clientAccount.get();
			account.setBankAccount(clientAccountPayload.getBankAccountNo());
			account.setBankCode(clientAccountPayload.getBankCode());
			account.setPayBillAccountNo(clientAccountPayload.getPayBillAccountNo());
			account.setPaybillNumber(clientAccountPayload.getPaybillNumber());
			account.setTillNumber(clientAccountPayload.getTillNumber());
			account.setWalletAccountNo(clientAccountPayload.getWalletAccountNo());
			this.walletClientAccountRepository.save(account);
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("message", "Client account updated");
			return ResponseEntity.status(HttpStatus.OK).body(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
		}
	}

	public Optional<List<WalletClient>> findWalletClientByAppKey(String client_app_key) {

		// TODO Auto-generated method stub
		return this.wallectClientRepository.findWalletClientByAppKey(client_app_key);

	}

}
