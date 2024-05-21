package net.sasakonnect.wallet.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GlobalPermissionConstants {
	public static class CreateSuperApp extends PermissionEntry {
		public static final String PERMISSION = "can.create.super.app";
		public static final String DESCRIPTION = "user can create new super app";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanSupportCustomer extends PermissionEntry {
		public static final String PERMISSION = "can.operate.customer.support";
		public static final String DESCRIPTION = "can carry out customer support requests";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	
	
	public static class CreateUserRole extends PermissionEntry {
		public static final String PERMISSION = "can.create.user.role";
		public static final String DESCRIPTION = "Can create any role in the system(use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanViewLogs extends PermissionEntry {
		public static final String PERMISSION = "can.view.logs";
		public static final String DESCRIPTION = "Can view all system logs";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	public static class CanViewTariffs extends PermissionEntry {
		public static final String PERMISSION = "can.view.tariff.all";
		public static final String DESCRIPTION = "Can view tariffs";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanViewInvoices extends PermissionEntry {
		public static final String PERMISSION = "can.view.invoices";
		public static final String DESCRIPTION = "Can view invoices";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanGenerateInvoice extends PermissionEntry {
		public static final String PERMISSION = "can.generate.invoice";
		public static final String DESCRIPTION = "Can generate invoice";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	
	public static class CanEditTariff extends PermissionEntry {
		public static final String PERMISSION = "can.edit.tariff.all";
		public static final String DESCRIPTION = "Can edit tariff(use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	public static class SearchTransaction extends PermissionEntry {
		public static final String PERMISSION = "can.transaction.search";
		public static final String DESCRIPTION = "Can search any transaction in the system";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class SyncLark extends PermissionEntry {
		public static final String PERMISSION = "can.sync.lark";
		public static final String DESCRIPTION = "Can trigger sync of lark users into database(Use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class AssignUserRole extends PermissionEntry {
		public static final String PERMISSION = "can.assign.user.role";
		public static final String DESCRIPTION = "Can assign any user any role in the system(use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewCorporateUsers extends PermissionEntry {
		public static final String PERMISSION = "can.view.user.corporate";
		public static final String DESCRIPTION = "Can view corporate users";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewAllUsers extends PermissionEntry {
		public static final String PERMISSION = "can.view.user.all";
		public static final String DESCRIPTION = "Can view all users";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	
	public static class ViewLarkUsers extends PermissionEntry {
		public static final String PERMISSION = "can.view.user.lark";
		public static final String DESCRIPTION = "Can view lark users";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class SearchLarkUsers extends PermissionEntry {
		public static final String PERMISSION = "can.search.user.lark";
		public static final String DESCRIPTION = "Can search lark users";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class AssignRolePermissions extends PermissionEntry {
		public static final String PERMISSION = "can.assign.role.permissions";
		public static final String DESCRIPTION = "Can assign permissions to an existing role";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewAllRoles extends PermissionEntry {
		public static final String PERMISSION = "can.read.role.all";
		public static final String DESCRIPTION = "Can read all the existing system roles";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class DeleteRole extends PermissionEntry {
		public static final String PERMISSION = "can.delete.role";
		public static final String DESCRIPTION = "Can read and delete a role";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	
	}
	public static class EditRole extends PermissionEntry {
		public static final String PERMISSION = "can.edit.role";
		public static final String DESCRIPTION = "Can edit an existing system role";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class EditPermission extends PermissionEntry {
		public static final String PERMISSION = "can.edit.permission";
		public static final String DESCRIPTION = "Can edit existing system permission)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewAllPermissions extends PermissionEntry {
		public static final String PERMISSION = "can.read.permissions.all";
		public static final String DESCRIPTION = "Can read all the existing system permissions)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanSearchUsers extends PermissionEntry {
		public static final String PERMISSION = "can.search.user";
		public static final String DESCRIPTION = "Can search users by the specified parameter";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanViewAccountBalance extends PermissionEntry {
		public static final String PERMISSION = "can.view.wallet.acc.balance";
		public static final String DESCRIPTION = "Can check wallet account balance";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanResetPintattempts extends PermissionEntry {
		public static final String PERMISSION = "can.reset.user.pin.attempts.counts";
		public static final String DESCRIPTION = "Can reset user pin attempts count (Only to be used by super admins and admins)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanResetUserPin extends PermissionEntry {
		public static final String PERMISSION = "can.reset.user.pin";
		public static final String DESCRIPTION = "Can reset user pin(Only to be used by super admins and admins)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}

	public static class DisableSuperApp extends PermissionEntry {
		public static final String PERMISSION = "can.disable.super.app";
		public static final String DESCRIPTION = "user can disable  super app";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}

	public static class CreateWalletClient extends PermissionEntry {
		public static final String PERMISSION = "can.create.wallet.client";
		public static final String DESCRIPTION = "can create client to be used by other languages such as dart and node";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}

	public static class CheckUserAccountStatus extends PermissionEntry {
		public static final String PERMISSION = "can.check.user.account";
		public static final String DESCRIPTION = "can check account status eg.why account was rejected";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CheckAlltransactionHistory extends PermissionEntry {
		public static final String PERMISSION = "can.view.transactions.all";
		public static final String DESCRIPTION = "can view all transactions in the system";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewRecenttransactionHistory extends PermissionEntry {
		public static final String PERMISSION = "can.view.transactions.recent";
		public static final String DESCRIPTION = "can view recent transactions in the system";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewAccountAnalyticsSummary extends PermissionEntry {
		public static final String PERMISSION = "can.view.accounts.analytics.summary";
		public static final String DESCRIPTION = "can view account analytics summary in the system";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class ViewAllAnalytics extends PermissionEntry {
		public static final String PERMISSION = "can.view.accounts.analytics.all";
		public static final String DESCRIPTION = "can view all analytics summary in the system";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanRequestAccountStatement extends PermissionEntry {
		public static final String PERMISSION = "can.request.account.statement";
		public static final String DESCRIPTION = "can request user account statement(use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
	public static class CanCreateTarrif extends PermissionEntry {
		public static final String PERMISSION = "can.create.tarrif";
		public static final String DESCRIPTION = "user with this permission can create a tarrif for a channel(use with caution)";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
	}
	
		
	
	public static class CreateCorporateAccount extends PermissionEntry {
		public static final String PERMISSION = "can.create.corporate.account";
		public static final String DESCRIPTION = "can create a corporate account";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
		
	}

	
	public static class CanSearchAccountInfo extends PermissionEntry {
		public static final String PERMISSION = "can.search.account.info";
		public static final String DESCRIPTION = "can search account information(use carefully";

		@Override
		public String getPERMISSION() {
			return PERMISSION;
		}

		@Override
		public String getDESCRIPTION() {
			return DESCRIPTION;
		}
		
	}
	public static Map<String, String> scan() {
		Class<?>[] innerClasses = GlobalPermissionConstants.class.getDeclaredClasses();

		Set<PermissionEntry> permissionEntries = new HashSet<>();

		for (Class<?> innerClass : innerClasses) {
			// Check if the class extends PermissionEntry
			if (PermissionEntry.class.isAssignableFrom(innerClass)) {
				try {
					// Create an instance of the inner class
					PermissionEntry entryInstance = (PermissionEntry) innerClass.getDeclaredConstructor().newInstance();
					permissionEntries.add(entryInstance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return permissionEntries.stream()
				.collect(Collectors.toMap(PermissionEntry::getPERMISSION, PermissionEntry::getDESCRIPTION));
	}
}
