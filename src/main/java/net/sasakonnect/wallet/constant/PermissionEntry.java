package net.sasakonnect.wallet.constant;

public abstract class PermissionEntry {
	abstract String getPERMISSION();

	abstract String getDESCRIPTION();

	static boolean isAssignable = true;

	public static String getUrl(String permission) {
		return "hasPermission(#apartmentId, '" + permission + "')";
	}
}
