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
