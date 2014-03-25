package sdis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	public static String apply(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(str.getBytes("UTF-8"));
		
		StringBuffer sb = new StringBuffer();
		   for(byte b : hash) {
				sb.append(String.format("%02x", b));
		    }

		    return sb.toString();

	}
}
