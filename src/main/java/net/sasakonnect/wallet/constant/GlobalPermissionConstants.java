package net.sasakonnect.wallet.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GlobalPermissionConstants {
	public static class CreateSuperApp extends PermissionEntry {
		public static final String PERMISSION = "can.creat.super.app";
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
