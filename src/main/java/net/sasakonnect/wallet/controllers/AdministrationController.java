package net.sasakonnect.wallet.controllers;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import net.bytebuddy.asm.Advice.This;
import net.sasakonnect.wallet.RequestDto.Corporate;
import net.sasakonnect.wallet.RequestDto.PermissionDTO;
import net.sasakonnect.wallet.RequestDto.PermissionsToRoleDTO;
import net.sasakonnect.wallet.RequestDto.RoleDTO;
import net.sasakonnect.wallet.RequestDto.UserRoleDTO;
import net.sasakonnect.wallet.RequestDto.VerifyCorporate;
import net.sasakonnect.wallet.RequestDto.WalletClientAccountDto;
import net.sasakonnect.wallet.RequestDto.WalletClientDTO;
import net.sasakonnect.wallet.RequestDto.admin.CheckUserAccount;
import net.sasakonnect.wallet.RequestDto.admin.PinReset;
import net.sasakonnect.wallet.annotations.CustomController;
import net.sasakonnect.wallet.annotations.RequirePermission;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.repository.RoleRepository;
import net.sasakonnect.wallet.services.AccountStatementService;
import net.sasakonnect.wallet.services.AnalyticsService;
import net.sasakonnect.wallet.services.CorporateService;
import net.sasakonnect.wallet.services.LarkService;
import net.sasakonnect.wallet.services.PermissionService;
import net.sasakonnect.wallet.services.RoleService;
import net.sasakonnect.wallet.services.TransactionService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.WalletClientService;
import net.sasakonnect.wallet.services.WalletService;

@RequestMapping("/administration")
@Tag(name = "Administration", description = "Back Office  routes")
@CustomController()
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
	AccountStatementService  accountStatementService;

	@Autowired
	RoleRepository roleRepository;

	@GetMapping("/upload/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateSuperApp.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateSuperApp.PERMISSION)
	public String upload() throws AccountNotFoundException {
		// Logic to retrieve targetDomainObject
		// For example: String targetDomainObject = someService.getTargetDomainObject();
		return "account: ";
	}

	@PostMapping("/create/app")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object createApp(@Valid @RequestBody() WalletClientDTO walletClientDto) {
		return this.walletClientService.createWallectClientApp(walletClientDto);
	}

	@PostMapping("/attach/paymentAccount")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateWalletClient.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateWalletClient.PERMISSION)
	public Object attachPaymentAccount(@Valid @RequestBody() WalletClientAccountDto walletClientAccount) {
		return this.walletClientService.createWalletClientAccount(walletClientAccount);
	}


	@PostMapping("/check/account/status")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object checkUserAccountStatus(@Valid @RequestBody() CheckUserAccount checkUserAccount) {
		return this.walletService.checkUserAccountStatus(checkUserAccount);
	}

	@GetMapping("/users/getAll")
//	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllUsers.PERMISSION + "')")
//	@RequirePermission(GlobalPermissionConstants.ViewAllUsers.PERMISSION)
	public ResponseEntity<Object> getAllUsers(
			@RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber
			) {
		return this.userService.getAllUsers(pageNumber,pageSize);
	}

	@GetMapping("/user/corporate")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewCorporateUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewCorporateUsers.PERMISSION)
	public Object getAllCorporateUser() {
		return this.userService.getCorporateUsers();
	}

	@GetMapping("/user/search")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanSearchUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanSearchUsers.PERMISSION)
	public Object searchUserbyPhone(
			@RequestParam(name="queryString",required=true) String phone ,
			@RequestParam(name="pageNumber",required=false,defaultValue="0") Integer pageNumber,
			@RequestParam(name="pageSize",required=false,defaultValue="10") Integer pageSize
			) {
		
		if(!phone.isEmpty()) {
			return this.userService.searchUser(phone, pageNumber, pageSize);
		}
	
		return null;
	}

	@PostMapping("/user/corporate/create")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CreateCorporateAccount.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CreateCorporateAccount.PERMISSION)
	public Object createCorporateDetails(@Valid @RequestBody Corporate corporate) {
		return this.corporateService.createCorporateDetails(corporate);

	}

	@PostMapping("/user/corporate/account/activate")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
	public Object activateCorporateAccount(@Valid @RequestBody VerifyCorporate request) {
		return this.corporateService.activateCorporateAccount(request);
	}

//	@PostMapping("/user/corporate/email/verify")
//	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION + "')")
//	@RequirePermission(GlobalPermissionConstants.CheckUserAccountStatus.PERMISSION)
//    public Object verifyCorporateEmail(@RequestBody  VerifyEmailDTO request) {
//		return this.corporateService.verifyEmail(request.getEmail(),true);
//	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewCorporateUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewCorporateUsers.PERMISSION)
	@GetMapping("/user/corporate/get")
	public Object getCorporateEmails() {
		return this.corporateService.getCorporateAccounts();
	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewLarkUsers.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewLarkUsers.PERMISSION)
	@GetMapping("/users/lark")
	public Object getLarkUsers(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.larkService.getLarkUsers(pageNumber, pageSize);
	}

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

	@PutMapping("/role/edit")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.EditRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.EditRole.PERMISSION)
	public Object editRole(@Valid @RequestBody RoleDTO role,
			@RequestParam(name = "roleId", required = true) String roleId) {
		return this.roleService.editRole(roleId, role);
	}

	@PostMapping("/user/attachRole")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.AssignUserRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.AssignUserRole.PERMISSION)
	public Object attachUserToRole(@Valid @RequestBody UserRoleDTO userRole) {
		return this.roleService.attachUserToRole(userRole);
	}

	@GetMapping("role/getAll")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getAllRoles() {
		return this.roleService.getAllRoles();
	}

	@GetMapping("permissions/getAll")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllPermissions.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllPermissions.PERMISSION)
	public Object getAllPermissions() {
		return this.permissionService.getAllPermissions();
	}

	@PostMapping("/role/assignPermissions")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.AssignRolePermissions.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.AssignRolePermissions.PERMISSION)
	public Object assignPermissionsToRole(@Valid @RequestBody PermissionsToRoleDTO rolePermission) {
		return this.roleService.insertPermissionsNotAttachedToRole(rolePermission);

	}

	@DeleteMapping("/role")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.DeleteRole.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.DeleteRole.PERMISSION)
	public Object assignPermissionsToRole(@RequestParam(name = "roleId", required = true) String roleId) {
		return this.roleService.deleteRoleByid(roleId);

	}

	@GetMapping("/user/permissions")
	public Object getUserPermissions() {
		return this.roleService.getUserPermissions();
	}

	@GetMapping("/transactions/history")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getTransactionHistory(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber

	) {
		return transactionService.getTransactionHistory(pageNumber, pageSize);
	}

	@GetMapping("/transactions/history/user")
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
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getRolePermissionsByRoleId(@RequestParam(name = "roleId", required = true) String roleId) {

		return this.roleService.getRolePermissionsByRoleId(roleId);
	}
	
	@GetMapping("/role/users")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllRoles.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllRoles.PERMISSION)
	public Object getRoleUsers(
			@RequestParam(name = "roleId", required = true) String roleId,
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber
			) {

		return this.roleService.getRoleUsers(roleId,pageNumber,pageSize);
	}

	@PutMapping("/permission")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.EditPermission.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.EditPermission.PERMISSION)
	public Object editPermission(@Valid @RequestBody PermissionDTO payload,
			@RequestParam(name = "permissionId", required = true) String permissionId) {

		return this.permissionService.editPermission(permissionId, payload);
	}

	@Hidden()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SyncLark.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SyncLark.PERMISSION)
	@GetMapping("/lark/user/sync")
	public void syncLarkUsers() {
		this.larkService.syncLarkUsers();
	}

	@Hidden()
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SyncLark.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.SyncLark.PERMISSION)
	@GetMapping("/lark/dept/sync")
	public void syncLarkDepartments() {
		this.larkService.synLarkDepartments();
	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/summary")
	public Object getSummary() {
		return this.analyticsService.getSummary();
	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/transactions/recent")
	public Object getRecenTransactions(@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.analyticsService.getRecentTransactions(pageNumber, pageSize);
	}

	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAccountAnalyticsSummary.PERMISSION)
	@GetMapping("/analytics/wallet/transaction/rank")
	public ResponseEntity<Object> getWalletTransactionRanks(
			@RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
		return this.transactionService.getWalletTransactionRanks(pageNumber, pageSize);
	}

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
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanResetPintattempts.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanResetPintattempts.PERMISSION)
	public Object resetWalletPinAttempts(@Valid @RequestBody PinReset req) {
		return this.userService.resetPinAttempts(req.getCounter(), req.getUserId());

	}

	@PostMapping("/user/wallet/pin/reset")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanResetUserPin.PERMISSION + "')")
	@RequirePermission(GlobalPermissionConstants.CanResetUserPin.PERMISSION)
	public Object resetWalletPin(@Valid @RequestBody PinReset req) {
		return this.userService.resetUserPin(req.getUserId());
	}

	@GetMapping("/wallet/balance")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getWalletBalance(@RequestParam(name = "accountNumber", required = true) String accountNumber) {
		return this.walletService.getWalletAccountBalance(accountNumber);
	}
	
	@GetMapping("/wallet/pin/attempts")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public Object getWalletPinAttempts(@RequestParam(name = "accountNumber", required = true) String accountNumber) {
		return this.walletService.getWalletPinAttempts(accountNumber);
	}
	
	@GetMapping("/wallet/statement")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CheckAlltransactionHistory.PERMISSION)
	public void getWalletStatement(@RequestParam(name = "mobileNumber", required = true) String mobileNumber,@RequestParam(name = "filePath", required = true) String filePath)  {
//		this.accountStatementService.readFileAndGeneratePDF(filePath);
	
	}
	
	@GetMapping("/transaction/search")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.SearchTransaction.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.SearchTransaction.PERMISSION)
	public ResponseEntity<Object> searchTransaction(
			@RequestParam(name = "queryString", required =true) String queryString,
		    @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
			@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber
			) throws Exception {
	return	this.transactionService.searchTransaction(queryString,pageNumber,pageSize);
	
	}
	
	@PostMapping("account/statement")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION)
	@Operation(summary = "Get account statement", description = "Get account statement between start and end dates")
	public Object getAccountStatement(
			@RequestParam(name="accountId",required=true) String accountId,
			@Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-02-01") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@Parameter(description = "End date (YYYY-MM-DD)", example = "2024-02-29") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return walletService.getUserStatement(accountId,startDate, endDate);
	}
	
	@GetMapping("account/statement")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.CanRequestAccountStatement.PERMISSION)
	public ResponseEntity<Object> getUserAccounttatements(
			@RequestParam(name="userId",required=true) String userId
			){
		return this.walletService.getUserRequestedstatements(userId);
	}
	
	@GetMapping("analytics/onBoarding/trend")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	@Operation(summary = "Get user onboarding trend", description = "Get user onBoarding trend")
	public ResponseEntity<Object> getOnboardingTrend(
			@RequestParam(name="type",required=true,defaultValue="daily") String type,
			@RequestParam(name = "month",required=false,defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) int month,
			@RequestParam(name = "year",required=false,defaultValue="#{T(java.time.LocalDate).now().getYear()}") int year
			) {
		if(type.equalsIgnoreCase("annual")) {
			return this.userService.getAnnualOnboardingTrend();
		}
		
		if(type.equalsIgnoreCase("monthly")) {
			return this.userService.getMonthlyOnboardingTrend(month, year);
		}
		
		if(type.equalsIgnoreCase("daily")) {
			return this.userService.getDailyOnboardingTrend(month, year);
		}
		
		return null;
	}
	
	@GetMapping("analytics/onBoarding/deviation")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	@Operation(summary = "Get user onboarding trend", description = "Get user onBoarding trend")
    public ResponseEntity<Object> getUserOnboardingDeviation(){
		return this.userService.getOnBoardingDeviation();
	}
	
	
	@GetMapping("analytics/spending")
	@PreAuthorize("hasPermission(#apartmentId, '" + GlobalPermissionConstants.ViewAllAnalytics.PERMISSION
			+ "')")
	@RequirePermission(GlobalPermissionConstants.ViewAllAnalytics.PERMISSION)
	public ResponseEntity<Object> getTransactionBehaviour(
			@RequestParam(name = "month",required=false) Integer year,
			@RequestParam(name = "year",required=false) Integer month
			) {
		  if (year != null && month != null) {
	            return transactionService.getGeneralTransactionBehaviour(year, month);
	        } else if (year != null && month == null) {
	            return transactionService.getGeneralTransactionBehaviour(year,null);
	        } else {
	            return transactionService.getGeneralTransactionBehaviour(null,null);
	        }
	}

}
