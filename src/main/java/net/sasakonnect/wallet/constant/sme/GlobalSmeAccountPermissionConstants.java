package net.sasakonnect.wallet.constant.sme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalSmeAccountPermissionConstants {
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
	
	public static class CanInvokeTransaction extends SmePermissionEntry{
		public static final String PERMISSION = "can.invoke.transaction";
		public static final String DESCRIPTION = "can initiate a transaction in the account(Use with caution)";
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
	
	public static class CanGenerateAccountStatements extends SmePermissionEntry{
		public static final String PERMISSION = "can.generate.account.statement";
		public static final String DESCRIPTION = "can generate account transactions statement(Use with caution)";
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
	
	public static class CanBlockAccount extends SmePermissionEntry{
		public static final String PERMISSION = "can.block.account";
		public static final String DESCRIPTION = "can generate account transactions statement(Use with caution)";
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
	
	public static class CanDepositToAccount extends SmePermissionEntry{
		public static final String PERMISSION = "can.deposit.account";
		public static final String DESCRIPTION = "can deposit money to the given account";
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
	
	public static Set<Map<String, String>> scan() {
		Class<?>[] innerClasses = GlobalSmeAccountPermissionConstants.class.getDeclaredClasses();
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
