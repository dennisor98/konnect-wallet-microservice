package net.sasakonnect.wallet.constant.sme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.constant.PermissionEntry;

public class GlobalSmePermissionConstants {
	public static class CanAddSmeUser extends SmePermissionEntry{
		public static final String PERMISSION = "can.create.sme.user";
		public static final String DESCRIPTION = "can create sme user";
		public static final String CATEGORY = "user";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanViewSmeUser extends SmePermissionEntry{
		public static final String PERMISSION = "can.view.sme.user";
		public static final String DESCRIPTION = "can view sme user details";
		public static final String CATEGORY = "user";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanSearchSmeUser extends SmePermissionEntry{
		public static final String PERMISSION = "can.search.sme.user";
		public static final String DESCRIPTION = "can search sme user details";
		public static final String CATEGORY = "user";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanEditSmeUser extends SmePermissionEntry{
		public static final String PERMISSION = "can.edit.sme.user";
		public static final String DESCRIPTION = "can sme sme user details";
		public static final String CATEGORY = "user";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanDeleteSmeUser extends SmePermissionEntry{
		public static final String PERMISSION = "can.delete.sme.user";
		public static final String DESCRIPTION = "can delete sme user details(use with caution)";
		public static final String CATEGORY = "user";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	

	
	public static class CanAssignAccountAccess extends SmePermissionEntry{
		public static final String PERMISSION = "can.assign.user.acc.access";
		public static final String DESCRIPTION = "can assign account access to sme user";
		public static final String CATEGORY = "account";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanAssignSmeRole extends SmePermissionEntry{
		public static final String PERMISSION = "can.assign.sme.role";
		public static final String DESCRIPTION = "can assign sme role to a user";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanCreateSmeRole extends SmePermissionEntry{
		public static final String PERMISSION = "can.create.sme.role";
		public static final String DESCRIPTION = "can create sme role";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanCreateSmeAccountRole extends SmePermissionEntry{
		public static final String PERMISSION = "can.create.sme..account.role";
		public static final String DESCRIPTION = "can assign sme account role ";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanAssignAccountRole extends SmePermissionEntry{
		public static final String PERMISSION = "can.assign.account.role";
		public static final String DESCRIPTION = "can assign account role to a user";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanAssignRolePermissions extends SmePermissionEntry{
		public static final String PERMISSION = "can.assign.permissions.role";
		public static final String DESCRIPTION = "can assign permissions to a role";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	
	public static class CanCreatePaySlip extends SmePermissionEntry{
		public static final String PERMISSION = "can.create.payslip";
		public static final String DESCRIPTION = "can create a payslip";
		public static final String CATEGORY = "payslip";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	public static class CanGetRoles extends SmePermissionEntry{
		public static final String PERMISSION = "can.get.roles.all";
		public static final String DESCRIPTION = "can fetch all roles";
		public static final String CATEGORY = "role";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanGetPermissions extends SmePermissionEntry{
		public static final String PERMISSION = "can.get.permissions.all";
		public static final String DESCRIPTION = "can fetch all permissions";
		public static final String CATEGORY = "permission";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanPrintPaySlip extends SmePermissionEntry{
		public static final String PERMISSION = "can.print.payslip";
		public static final String DESCRIPTION = "can print a payslip record";
		public static final String CATEGORY = "payslip";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanDeletePayslip extends SmePermissionEntry{
		public static final String PERMISSION = "can.delete.payslip.record";
		public static final String DESCRIPTION = "can edit a payslip record";
		public static final String CATEGORY = "payslip";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanEditPaySlip extends SmePermissionEntry{
		public static final String PERMISSION = "can.edit.payslip.record";
		public static final String DESCRIPTION = "can print a payslip record";
		public static final String CATEGORY = "payslip";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	

	
	public static class CanQueryAccountBalance extends SmePermissionEntry{
		public static final String PERMISSION = "can.query.acc.balance";
		public static final String DESCRIPTION = "can query account  balance(Use with caution)";
		public static final String CATEGORY = "account";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanViewTransactions extends SmePermissionEntry{
		public static final String PERMISSION = "can.view.transactions";
		public static final String DESCRIPTION = "can view all transaction history";
		public static final String CATEGORY = "transaction";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static class CanInitiateTransaction extends SmePermissionEntry{
		public static final String PERMISSION = "can.init.transaction";
		public static final String DESCRIPTION = "can initiate a transaction(use with caution)";
		public static final String CATEGORY = "transaction";

		@Override
		String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		String getDESCRIPTION() {
			return DESCRIPTION;
		}

		@Override
		String getCATEGORY() {
			return CATEGORY;
		}

	}
	
	public static Set<Map<String, String>> scan() {
		Class<?>[] innerClasses = GlobalSmePermissionConstants.class.getDeclaredClasses();
		Set<SmePermissionEntry> permissionEntries = new HashSet<>();

		for (Class<?> innerClass : innerClasses) {
			if (SmePermissionEntry.class.isAssignableFrom(innerClass)) {
				try {
					SmePermissionEntry entryInstance = (SmePermissionEntry) innerClass.getDeclaredConstructor().newInstance();
					permissionEntries.add(entryInstance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		Set<Map<String, String>> result = new HashSet<>();

		for (SmePermissionEntry entry : permissionEntries) {
			Map<String, String> details = new HashMap<>();
			details.put("permission", entry.getPERMISSION());
			details.put("description", entry.getDESCRIPTION());
			details.put("category", entry.getCATEGORY());
			result.add(details);
		}

		return result;
	}

}
