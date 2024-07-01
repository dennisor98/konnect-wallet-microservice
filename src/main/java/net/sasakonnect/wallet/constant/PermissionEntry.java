package net.sasakonnect.wallet.constant;

public abstract class PermissionEntry {
	abstract String getPERMISSION();

	abstract String getDESCRIPTION();
	

	public static String getUrl(String permission) {
		return "hasPermission(#apartmentId, '" + permission + "')";
	}
}
