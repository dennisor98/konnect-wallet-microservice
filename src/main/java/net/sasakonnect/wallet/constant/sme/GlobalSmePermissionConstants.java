package net.sasakonnect.wallet.constant.sme;

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
	
	public static class CanAssignUserRole extends SmePermissionEntry{
		public static final String PERMISSION = "can.assign.user.role";
		public static final String DESCRIPTION = "can assign a role to a user";
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
}
