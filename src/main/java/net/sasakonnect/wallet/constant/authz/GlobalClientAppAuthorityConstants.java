package net.sasakonnect.wallet.constant.authz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sasakonnect.wallet.enums.authz.AutheritySensitivity;
import net.sasakonnect.wallet.enums.authz.AuthorityScopeType;

public class GlobalClientAppAuthorityConstants {
  public static class AccessAccountProfile extends AuthorityEntry {
		public static final String AUTHORITY = "auth.account.name.access";
		public static final String DESCRIPTION = "Access Account Profile such as name,OID and acc. Number";
		public static final int SENSITIVITY = 1;
		public static final String CATEGORY = AuthorityScopeType.PROFILE.getType();
	@Override
	String getAUTHORITY() {
		return AUTHORITY;
	}

	@Override
	String getDESCRIPTION() {
		return DESCRIPTION;
	}

	@Override
	int getSENSITIVITY() {
		return SENSITIVITY;
	}

	@Override
	String getCATEGORY() {
		return CATEGORY;
	}
	  
  }
  
  public static class AccessAccountTransactions extends AuthorityEntry {
		public static final String AUTHORITY = "auth.account.transactions.access";
		public static final String DESCRIPTION = "Access to Account Transactions";
		public static final int SENSITIVITY = 1;
		public static final String CATEGORY = AuthorityScopeType.PERSONAL.getType();
	@Override
	String getAUTHORITY() {
		return AUTHORITY;
	}

	@Override
	String getDESCRIPTION() {
		return DESCRIPTION;
	}

	@Override
	int getSENSITIVITY() {
		return SENSITIVITY;
	}

	@Override
	String getCATEGORY() {
		return CATEGORY;
	}
	  
  }
  
  public static class AccessChats extends AuthorityEntry {
		public static final String AUTHORITY = "auth.account.chats.access";
		public static final String DESCRIPTION = "Access to Personal app chat messages";
		public static final int SENSITIVITY = 1;
		public static final String CATEGORY = AuthorityScopeType.PERSONAL.getType();
	@Override
	String getAUTHORITY() {
		return AUTHORITY;
	}

	@Override
	String getDESCRIPTION() {
		return DESCRIPTION;
	}

	@Override
	int getSENSITIVITY() {
		return SENSITIVITY;
	}

	@Override
	String getCATEGORY() {
		return CATEGORY;
	}
	  
  }
  
  public static class AccessPhoneMessages extends AuthorityEntry {
		public static final String AUTHORITY = "auth.phone.message.access";
		public static final String DESCRIPTION = "Access to device personal transaction messages";
		public static final int SENSITIVITY = AutheritySensitivity.HIGH.getLevel();
		public static final String CATEGORY = AuthorityScopeType.PERSONAL.getType();
	@Override
	String getAUTHORITY() {
		return AUTHORITY;
	}

	@Override
	String getDESCRIPTION() {
		return DESCRIPTION;
	}

	@Override
	int getSENSITIVITY() {
		return SENSITIVITY;
	}

	@Override
	String getCATEGORY() {
		return CATEGORY;
	}
	  
  }
  
	public static Set<Map<String, String>> scan() {
		Class<?>[] innerClasses = GlobalClientAppAuthorityConstants.class.getDeclaredClasses();
		Set<AuthorityEntry> permissionEntries = new HashSet<>();

		for (Class<?> innerClass : innerClasses) {
			if (AuthorityEntry.class.isAssignableFrom(innerClass)) {
				try {
					AuthorityEntry entryInstance = (AuthorityEntry) innerClass.getDeclaredConstructor().newInstance();
					permissionEntries.add(entryInstance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		Set<Map<String, String>> result = new HashSet<>();

		for (AuthorityEntry entry : permissionEntries) {
			Map<String, String> details = new HashMap<>();
			details.put("authority", entry.getAUTHORITY());
			details.put("description", entry.getDESCRIPTION());
			details.put("sensitivity",String.valueOf(entry.getSENSITIVITY()));
			details.put("category",entry.getCATEGORY());
			result.add(details);
		}

		return result;
	}
}
