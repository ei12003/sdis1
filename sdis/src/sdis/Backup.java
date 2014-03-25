package sdis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Backup {
	
	public static final int chuckSize = 64000;
	
	public Backup() throws NoSuchAlgorithmException, IOException{
		split(new File("file.jpg"));
	}
	
	public void split(File file) throws NoSuchAlgorithmException, IOException{
		
		int bytesRead;
		String bitString = file.getName()+file.lastModified();
		String fileID = SHA256.apply(bitString);
		BufferedInputStream fileBuffer = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[chuckSize];
		
		while((bytesRead=fileBuffer.read(buffer))>0){
			
		}
	}
}
