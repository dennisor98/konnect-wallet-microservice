package net.sasakonnect.wallet.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class Helper {
	public static String generateHashBasedUUID() {
		try {
			// Generate a random UUID
			UUID uuid = UUID.randomUUID();

			// Convert the UUID to bytes
			byte[] uuidBytes = asBytes(uuid);

			// Create a MessageDigest instance (e.g., SHA-256)
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// Compute the hash of the UUID bytes
			byte[] hashBytes = md.digest(uuidBytes);

			// Convert the byte array to a hexadecimal string
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				hexString.append(String.format("%02x", b));
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			// Handle the exception, e.g., log it or throw a custom exception
			throw new RuntimeException("SHA-256 algorithm not available.", e);
		}
	}

	private static byte[] asBytes(UUID uuid) {
		long mostSigBits = uuid.getMostSignificantBits();
		long leastSigBits = uuid.getLeastSignificantBits();
		byte[] bytes = new byte[16];
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) (mostSigBits >>> 8 * (7 - i));
			bytes[8 + i] = (byte) (leastSigBits >>> 8 * (7 - i));
		}
		return bytes;
	}

}
