package net.sasakonnect.wallet.controllers;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.RequestDto.CloseUserAccount;
import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.NotificationDto;
import net.sasakonnect.wallet.RequestDto.PermissionDTO;
import net.sasakonnect.wallet.RequestDto.PermissionsToRoleDTO;
import net.sasakonnect.wallet.RequestDto.PinResetDto;
import net.sasakonnect.wallet.RequestDto.ReversalDto;
import net.sasakonnect.wallet.RequestDto.RoleDTO;
import net.sasakonnect.wallet.RequestDto.SmeDefaultPasswordDto;
import net.sasakonnect.wallet.RequestDto.UserRoleDTO;
import net.sasakonnect.wallet.RequestDto.VerifyCorporate;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountUpdateDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.RequestDto.WalletClientUpdateDto;
import net.sasakonnect.wallet.RequestDto.admin.CheckUserAccount;
import net.sasakonnect.wallet.RequestDto.admin.PinReset;
import net.sasakonnect.wallet.RequestDto.authz.AssignAuthorityDto;
import net.sasakonnect.wallet.RequestDto.authz.ClientAuthorityDto;
import net.sasakonnect.wallet.RequestDto.sme.ChangeUserPhoneNumberDto;
import net.sasakonnect.wallet.RequestDto.sme.ConfirmPhoneNumberChangeDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeAccountManagerDto;
import net.sasakonnect.wallet.RequestDto.sme.SmeMultiAccountDto;
import net.sasakonnect.wallet.RequestDto.tarrif.TariffDTO;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.IsCorporate;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.RoleRepository;
import net.sasakonnect.wallet.services.AccountStatementService;
import net.sasakonnect.wallet.services.AnalyticsService;
import net.sasakonnect.wallet.services.CorporateService;
import net.sasakonnect.wallet.services.InvoiceService;
import net.sasakonnect.wallet.services.LarkService;
import net.sasakonnect.wallet.services.LogService;
import net.sasakonnect.wallet.services.NotificationService;
import net.sasakonnect.wallet.services.PermissionService;
import net.sasakonnect.wallet.services.PinResetService;
import net.sasakonnect.wallet.services.ReversalService;
import net.sasakonnect.wallet.services.RoleService;
import net.sasakonnect.wallet.services.TarrifService;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletClientService;
import net.sasakonnect.wallet.services.WalletService;
import net.sasakonnect.wallet.services.sme.SmeService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@RequestMapping("/administration")
@Tag(name = "Administration", description = "Back Office  routes")
@CustomController()
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AdministrationController {		
	@Autowired
	WalletClientService walletClientService;
	@Autowired
	WalletService walletService;

	@Autowired
	UserService userService;

	@Autowired
	CorporateService corporateService;

	@Autowired
	RoleService roleService;

	@Autowired
	LarkService larkService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	AnalyticsService analyticsService;

	@Autowired
	AccountStatementService accountStatementService;

	@Autowired
	TarrifService tarrifService;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	LogService logService;

	@Autowired
	PinResetService pinResetService;

	@Autowired
	ReversalService reversalService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SmeUserService smeUserService;
	
	@Autowired
	SmeService smeService;

	@GetMapping("/upload/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateSuperApp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateSuperApp.PERMISSION)
	public String upload() throws AccountNotFoundException {
		// Logic to retrieve targetDomainObject
		// For example: String targetDomainObject = someService.getTargetDomainObject();
		return "account: ";
	}

	@PostMapping("/create/app")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object createApp(@Valid @RequestBody() WalletClientDTO walletClientDto) {
		return this.walletClientService.createWallectClientApp(walletClientDto);
	}

	@PutMapping("/update/app")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object updateWallectClientApp(@Valid @RequestBody() WalletClientUpdateDto walletClientUpdateDto) {
		return this.walletClientService.updateWalletClientApp(walletClientUpdateDto);
	}

	@PutMapping("/set/app/account/primary")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object setPrimaryWallectClientAccount(
			@Valid @RequestBody() WalletClientAccountUpdateDto walletClientUpdateDto) {
		return this.walletClientService.updatePrimaryWalletClientAccount(walletClientUpdateDto);
	}

	@DeleteMapping("/delete/app")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object deleteApp(@RequestParam("clientId") String clientId) {
		return this.walletClientService.deleteWalletClient(clientId);
	}

	@PostMapping("/attach/paymentAccount")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object attachPaymentAccount(@Valid @RequestBody() WalletClientAccountDto walletClientAccount) {
		return this.walletClientService.createWalletClientAccount(walletClientAccount);
	}

	@PutMapping("/attach/paymentAccount")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object updatePrimaryWalletClientAccount(@Valid @RequestBody() WalletClientAccountDto walletClientAccount) {
		return this.walletClientService.updateWalletClientAccount(walletClientAccount);
	}

	@PostMapping("/check/account/status")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object checkUserAccountStatus(@Valid @RequestBody() CheckUserAccount checkUserAccount) {
		return this.walletService.checkUserAccountStatus(checkUserAccount);
	}

	@GetMapping("/users/getAll")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllUsers.PERMISSION)
	public ResponseEntity<Object> getAllUsers(@RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		if (pageSize > 100) {
			pageSize = 100;
		}
		return this.userService.getAllUsers(pageNumber, pageSize);
	}
	
	@GetMapping("/users/rejected")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllUsers.PERMISSION)
	public ResponseEntity<Object> getRejectedUsers(@RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		if (pageSize > 100) {
			pageSize = 100;
		}
		return this.userService.getRejectedUsers(pageNumber, pageSize);
	}

	@GetMapping("/user/corporate")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewCorporateUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewCorporateUsers.PERMISSION)
	public Object getAllCorporateUser() {
		return this.userService.getCorporateUsers();
	}

	@GetMapping("/user/search")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchUsers.PERMISSION)
	public Object searchUserbyPhone(@RequestParam(name = "queryString", required = true) String phone,
			@RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		if (pageSize > 10) {
			pageSize = 10;
		}
		if (!phone.isEmpty()) {
			return this.userService.searchUser(phone, pageNumber, pageSize);
		}

		return null;
	}
	
	@GetMapping("/user/rejected/search")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchUsers.PERMISSION)
	public Object searchRejectedUser(@RequestParam(name = "queryString", required = true) String phone,
			@RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		if (pageSize > 10) {
			pageSize = 10;
		}
		if (!phone.isEmpty()) {
			return this.userService.searchRejectedUser(phone, pageNumber, pageSize);
		}

		return null;
	}

	@PostMapping("/user/corporate/create")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateCorporateAccount.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateCorporateAccount.PERMISSION)
	public Object createCorporateDetails(@Valid @RequestBody Corporate corporate) {
		return this.corporateService.createCorporateDetails(corporate);

	}

	@PostMapping("/user/corporate/account/activate")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object activateCorporateAccount(@Valid @RequestBody VerifyCorporate request) {
		return this.corporateService.activateCorporateAccount(request);
	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewCorporateUsers.PERMISSION + "')")
	@IsCorporate()
	@RequirePermission(GlobalPermissionConstants.ViewCorporateUsers.PERMISSION)
	@GetMapping("/user/corporate/get")
	public Object getCorporateEmails() {
		return this.corporateService.getCorporateAccounts();
	}

	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewLarkUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewLarkUsers.PERMISSION)
	@GetMapping("/users/lark")
	public Object getLarkUsers(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.larkService.getLarkUsers(pageNumber, pageSize);
	}

	@IsCorporate()
	@PostMapping("/role/create")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateUserRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateUserRole.PERMISSION)
	public Object createPermission(@Valid @RequestBody RoleDTO role) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		var rol = Role.builder().roleName(role.getRolename()).description(role.getRoleDescription()).user(user).build();
		var resObject = this.roleService.insertRole(rol);
		resObject.setUser(null);
		return ResponseEntity.status(HttpStatus.OK).body(resObject);
	}

	@IsCorporate()
	@PutMapping("/role/edit")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.EditRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.EditRole.PERMISSION)
	public Object editRole(@Valid @RequestBody RoleDTO role,
			@RequestParam(name = "roleId", required = true) String roleId) {
		return this.roleService.editRole(roleId, role);
	}

	@PostMapping("/user/attachRole")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.AssignUserRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.AssignUserRole.PERMISSION)
	public Object attachUserToRole(@Valid @RequestBody UserRoleDTO userRole) {
		return this.roleService.attachUserToRole(userRole);
	}

	@GetMapping("role/getAll")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getAllRoles() {
		return this.roleService.getAllRoles();
	}

	@GetMapping("permissions/getAll")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllPermissions.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllPermissions.PERMISSION)
	public Object getAllPermissions() {
		return this.permissionService.getAllPermissions();
	}

	@PostMapping("/role/assignPermissions")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.AssignRolePermissions.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.AssignRolePermissions.PERMISSION)
	public Object assignPermissionsToRole(@Valid @RequestBody PermissionsToRoleDTO rolePermission) {
		return this.roleService.insertPermissionsNotAttachedToRole(rolePermission);

	}

	@DeleteMapping("/role")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.DeleteRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.DeleteRole.PERMISSION)
	public Object assignPermissionsToRole(@RequestParam(name = "roleId", required = true) String roleId) {
		return this.roleService.deleteRoleByid(roleId);

	}

	@GetMapping("/user/permissions")
	@IsCorporate()

	public Object getUserPermissions() {
		return this.roleService.getUserPermissions();
	}

	@GetMapping("/transactions/history")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getTransactionHistory(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return transactionService.getTransactionHistory(pageNumber, pageSize);
	}

	@GetMapping("/transactions/history/user")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getTransactionHistoryByAccountNumber(
			@RequestParam(name = "acccountNumber", required = true) String acccountNumber,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber

	) {
		return transactionService.getTransactionHistoryByAccountNumber(acccountNumber, pageNumber, pageSize);
	}

	@GetMapping("/role/permissions")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getRolePermissionsByRoleId(@RequestParam(name = "roleId", required = true) String roleId) {

		return this.roleService.getRolePermissionsByRoleId(roleId);
	}

	@GetMapping("/role/users")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getRoleUsers(@RequestParam(name = "roleId", required = true) String roleId,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {

		return this.roleService.getRoleUsers(roleId, pageNumber, pageSize);
	}

	@PutMapping("/permission")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.EditPermission.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.EditPermission.PERMISSION)
	public Object editPermission(@Valid @RequestBody PermissionDTO payload,
			@RequestParam(name = "permissionId", required = true) String permissionId) {

		return this.permissionService.editPermission(permissionId, payload);
	}

	@Hidden()
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SyncLark.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SyncLark.PERMISSION)
	@GetMapping("/lark/user/sync")
	public void syncLarkUsers() {
		this.larkService.syncLarkUsers();
	}

	@Hidden()
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SyncLark.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SyncLark.PERMISSION)
	@GetMapping("/lark/dept/sync")
	public void syncLarkDepartments() {
		this.larkService.synLarkDepartments();
	}

	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/summary")
	public Object getSummary() {
		return this.analyticsService.getSummary();
	}

	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/transactions/recent")
	public Object getRecenTransactions(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.analyticsService.getRecentTransactions(pageNumber, pageSize);
	}

	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/wallet/transaction/rank")
	public ResponseEntity<Object> getWalletTransactionRanks(
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.transactionService.getWalletTransactionRanks(pageNumber, pageSize);
	}

	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/transaction/trend")
	public ResponseEntity<Object> getDailyTransactionTrends(
			@RequestParam(name = "filter", defaultValue = "daily") String filter,
			@RequestParam(name = "month", defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) int month,
			@RequestParam(name = "year", defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
		if (filter.equalsIgnoreCase("daily")) {
			return this.transactionService.getDailyTransactionTrend(year, month);
		}

		else if (filter.equalsIgnoreCase("monthly")) {
			return this.transactionService.getMonthlyTransactionTrend(year);
		}

		else if (filter.equalsIgnoreCase("annual")) {
			return this.transactionService.getAnnualTransactionTrend();
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "Invalid filter value");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
		}

	}

	@PostMapping("/user/wallet/pin/resetAttempts")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanResetPintattempts.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanResetPintattempts.PERMISSION)
	public Object resetWalletPinAttempts(@Valid @RequestBody PinReset req) {
		return this.userService.resetPinAttempts(req.getCounter(), req.getUserId());

	}

	@PostMapping("/user/wallet/pin/reset")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanResetUserPin.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanResetUserPin.PERMISSION)
	public Object resetWalletPin(@Valid @RequestBody PinReset req) {
		return this.userService.resetUserPin(req.getUserId());
	}

	@GetMapping("/wallet/balance")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getWalletBalance(@RequestParam(name = "accountNumber", required = true) String accountNumber) {
		return this.walletService.getWalletAccountBalance(accountNumber);
	}

	@GetMapping("/wallet/pin/attempts")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getWalletPinAttempts(@RequestParam(name = "accountNumber", required = true) String accountNumber) {
		return this.walletService.getWalletPinAttempts(accountNumber);
	}

	@GetMapping("/wallet/statement")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public void getWalletStatement(@RequestParam(name = "mobileNumber", required = true) String mobileNumber,
			@RequestParam(name = "filePath", required = true) String filePath) {
//		this.accountStatementService.readFileAndGeneratePDF(filePath);

	}

	@GetMapping("/transaction/search")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SearchTransaction.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SearchTransaction.PERMISSION)
	public ResponseEntity<Object> searchTransaction(
			@RequestParam(name = "queryString", required = true) String queryString,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) throws Exception {
		return this.transactionService.searchTransaction(queryString, pageNumber, pageSize);

	}

	@PostMapping("account/statement")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION)
	@Operation(summary = "Get account statement", description = "Get account statement between start and end dates")
	public Object getAccountStatement(@RequestParam(name = "accountId", required = true) String accountId,
			@Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-02-01") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date (YYYY-MM-DD)", example = "2024-02-29") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return walletService.getUserStatement(accountId, startDate, endDate);
	}

	@GetMapping("account/statement")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION)
	public ResponseEntity<Object> getUserAccounttatements(
			@RequestParam(name = "userId", required = true) String userId) {
		return this.walletService.getUserRequestedstatements(userId);
	}

	@GetMapping("analytics/onBoarding/trend")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	@Operation(summary = "Get user onboarding trend", description = "Get user onBoarding trend")
	public ResponseEntity<Object> getOnboardingTrend(
			@RequestParam(name = "type", required = true, defaultValue = "daily") String type,
			@RequestParam(name = "month", required = false, defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) int month,
			@RequestParam(name = "year", required = false, defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year) {
		if (type.equalsIgnoreCase("annual")) {
			return this.userService.getAnnualOnboardingTrend();
		}

		if (type.equalsIgnoreCase("monthly")) {
			return this.userService.getMonthlyOnboardingTrend(month, year);
		}

		if (type.equalsIgnoreCase("daily")) {
			return this.userService.getDailyOnboardingTrend(month, year);
		}

		return null;
	}

	@GetMapping("analytics/onBoarding/deviation")
	@IsCorporate()

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	@Operation(summary = "Get user onboarding trend", description = "Get user onBoarding trend")
	public ResponseEntity<Object> getUserOnboardingDeviation() {
		return this.userService.getOnBoardingDeviation();
	}

	@GetMapping("analytics/spending")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	public ResponseEntity<Object> getTransactionBehaviour(@RequestParam(name = "year", required = false) Integer year,
			@RequestParam(name = "month", required = false) Integer month) {
		if (year != null && month != null) {
			return transactionService.getGeneralTransactionBehaviour(year, month);
		}
		if (year != null && month == null) {

			return transactionService.getGeneralTransactionBehaviour(year, null);
		}

		if (year == null && month == null) {
			return transactionService.getGeneralTransactionBehaviour(null, null);
		}

		return transactionService.getGeneralTransactionBehaviour(null, null);
	}
	// tarrif routes

	@PostMapping("tarrif")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanCreateTarrif.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanCreateTarrif.PERMISSION)
	@Operation(summary = "Create a new tarrif", description = "Create tarrif that will show on client the ammount they are changed on transaction")
	public Object createTarrif(@Valid @RequestBody TariffDTO tariffDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return bindingResult.getFieldErrors();

		} else {
			return this.tarrifService.createTarrif(tariffDTO);
		}
		// return walletService.getUserStatement(accountId,startDate, endDate);
	}

	@GetMapping("tarrif")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewTariffs.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanViewTariffs.PERMISSION)
	public ResponseEntity<Object> getTariffs() {
		return this.tarrifService.getTarrifs();
	}

	@GetMapping("tarrif/filter")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewTariffs.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanViewTariffs.PERMISSION)
	public ResponseEntity<Object> filterTariffsByChannel(@Valid @RequestParam("channelType") ChannelType channelType) {
		return this.tarrifService.filterByChannel(channelType);
	}

	@PutMapping("tarrif")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanEditTariff.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanEditTariff.PERMISSION)
	public ResponseEntity<Object> editTarrif(@RequestParam("id") String id, @RequestBody() TariffDTO tariffDTO) {
		return this.tarrifService.editTarrif(id, tariffDTO);
	}

	@GetMapping("invoice/generate")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanGenerateInvoice.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanGenerateInvoice.PERMISSION)
	public ResponseEntity<Object> generateInvoice(
			@Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-02-01") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@Parameter(description = "End date (YYYY-MM-DD)", example = "2024-02-29") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
		return this.invoiceService.generateInvoice(startDate, endDate);
	}

	@GetMapping("invoice")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewInvoices.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanViewInvoices.PERMISSION)
	public ResponseEntity<Object> getInvoices(@RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.invoiceService.getInvoices(pageNumber, pageSize);
	}

	@GetMapping("logs")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewLogs.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanViewLogs.PERMISSION)
	public ResponseEntity<Object> getLogs(
			@Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-02-01") @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@Parameter(description = "End date (YYYY-MM-DD)", example = "2024-02-29") @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
			@RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return logService.getLogs(startDate, endDate, pageNumber, pageSize);
	}

	@GetMapping("logs/search")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanViewLogs.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanViewLogs.PERMISSION)
	public ResponseEntity<Object> searchLogs(@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "queryString") String queryString) {
		return logService.searchLogs(queryString, pageNumber, pageSize);
	}

	@GetMapping("account/check")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> confirmAccount(@RequestParam(name = "idNumber") String idNumber) {
		return this.pinResetService.confirmAccountExists(idNumber);
	}

	@GetMapping("lark/user/search")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SearchLarkUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SearchLarkUsers.PERMISSION)
	public ResponseEntity<Object> searchLarkUser(@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "queryString") String queryString) {
		return this.larkService.search(queryString, pageNumber, pageSize);
	}

	@PostMapping("pin/reset/request")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> requestPinReset(@Valid @RequestBody() PinResetDto request) {
		return this.pinResetService.requestPinReset(request);
	}

	@PostMapping("transaction/reversal/request")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> requestTransactionReversal(@Valid @RequestBody() ReversalDto request) {
		return this.reversalService.requestTransactionReversal(request);
	}

	@GetMapping("account/state")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> getAccountStatus(@RequestParam("mobile") String mobile) {
		return this.walletService.getAccountStatus(mobile.substring(mobile.length() - 9));
	}

	@PostMapping("notification")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> createNotification(@Valid @RequestBody() NotificationDto request) {
		return this.notificationService.createNotification(request);
	}

	@GetMapping("user/notification/history")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public Object getNotifications(@RequestParam("userId") String userId,@RequestParam(name="pageNumber",defaultValue="0") Integer pageNumber,@RequestParam(name="pageSize",defaultValue="10") Integer pageSize) {
		return this.notificationService.getUserNotificationHistory(userId, pageNumber, pageSize);
	}
	
	@GetMapping("apps/clients")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchAccountInfo.PERMISSION)
	public ResponseEntity<Object> getWalletClients(
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.walletClientService.getWalletClients(pageNumber, pageSize);
	}

	@PostMapping("sme/member/password")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanGenerateSmePassword.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanGenerateSmePassword.PERMISSION)
	public ResponseEntity<Object> generateSmememberPassword(@Valid @RequestBody() SmeDefaultPasswordDto request) {
		return this.smeUserService.createDefaultPassword(request.getUserId());
	}

	@PostMapping("user/close/account")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestUserAccountCloser.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestUserAccountCloser.PERMISSION)
	public Object closeUserAccount(@Valid @RequestBody() CloseUserAccount request) {
		return this.userService.closeUserAccount(request);
	}

	@GetMapping("user/transactions/trend")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestUserAccountCloser.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestUserAccountCloser.PERMISSION)
	public ResponseEntity<Object> getUserTransactionsTrend(@RequestParam("userId") String userId,
			@RequestParam("period") String period) {
//		if(Integer.valueOf(period) > 30) {
//			period = String.valueOf(30);
//		}
		return this.transactionService.getAccountTransactionSummary(userId, period);
	}

	@PostMapping("user/change/phonenumber")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ChangeUserPhoneNumber.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ChangeUserPhoneNumber.PERMISSION)
	public Object changeUserPhoneNumber(@Valid @RequestBody() ChangeUserPhoneNumberDto request) {
		return this.userService.changeUserPhoneNumber(request);
	}

	@PostMapping("user/confirm/change/phonenumber")
	@IsCorporate()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ConfirmChangeUserPhoneNumber.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ConfirmChangeUserPhoneNumber.PERMISSION)
	public Object confirmPhoneChange(@Valid @RequestBody() ConfirmPhoneNumberChangeDto request) {
		return this.userService.confirmPhoneChange(request);
	}
	
	
	@IsCorporate
	@PostMapping("/sme/multiAccount")
	@RequirePermission(GlobalPermissionConstants.CanCreateAnEnterprise.PERMISSION)
	public Object createSmeMultiAccount(@Valid @RequestBody() SmeMultiAccountDto accountDto) {
		return this.smeService.createSmeMultiAccount(accountDto);

	}
	
	
	@IsCorporate
	@GetMapping("/user/kycMaterials")
	@RequirePermission(GlobalPermissionConstants.CanQueryUserKycMaterials.PERMISSION)
	public Object getUserKycMaterials(@RequestParam("userId") String userId) {
		return this.walletService.getUserKycMaterials(userId);
	}
	
	
	@IsCorporate
	@GetMapping("/user/kycdocs")
	@RequirePermission(GlobalPermissionConstants.CanQueryUserKycMaterials.PERMISSION)
	public Object getUserKycDocs(@RequestParam(name="userId",required=true) String userId,
			@RequestParam(name="startDate",required=false) String startDate,
			@RequestParam(name="endDate",required=false) String endDate,
			@RequestParam(name="pageNumber",required=false,defaultValue="0") Integer pageNumber,
			@RequestParam(name="pageSize",required=false,defaultValue="10") Integer pageSize
			) {
		return this.walletService.getUserKycDocs(userId,startDate,endDate,pageNumber,pageSize);
	}
	
	@IsCorporate
	@GetMapping("/user/onbdocs")
	@RequirePermission(GlobalPermissionConstants.CanQueryUserKycMaterials.PERMISSION)
	public Object getUserOnbcDocs(@RequestParam(name="userId",required=true) String userId,
			@RequestParam(name="startDate",required=false) String startDate,
			@RequestParam(name="endDate",required=false) String endDate,
			@RequestParam(name="pageNumber",required=false,defaultValue="0") Integer pageNumber,
			@RequestParam(name="pageSize",required=false,defaultValue="10") Integer pageSize
			) {
		return this.walletService.getUserOnbDocs(userId,startDate,endDate,pageNumber,pageSize);
	}
	
	@IsCorporate
	@GetMapping("/user/walletInfo")
	@RequirePermission(GlobalPermissionConstants.CanQueryUserKycMaterials.PERMISSION)
	public Object getUserWalletinfo(@RequestParam("userId") String userId) {
		return this.walletService.getUserWalletInfo(userId);
	}
	
	@IsCorporate
	@GetMapping("/client/authorities")
	@RequirePermission(GlobalPermissionConstants.CanCreateClientAuthorities.PERMISSION)
	public Object getClientAuthorities() {
		return this.walletClientService.getGlobalClientAuthorities();
	}
	
	@IsCorporate
	@PostMapping("/client/authorities/assign")
	@RequirePermission(GlobalPermissionConstants.CanCreateClientAuthorities.PERMISSION)
	public Object attachClientAuthorities(@Valid @RequestBody AssignAuthorityDto authDto) {
		return this.walletClientService.assignClientAuthority(authDto);
	}
	
	
	
	
	

}
