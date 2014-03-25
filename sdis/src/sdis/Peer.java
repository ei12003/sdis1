package sdis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Peer {
	private SubscribeChannel MC,MDB,MDR;
	
	public Peer(String strMC, String strMDB, String strMDR) throws NoSuchAlgorithmException, IOException{

		MC = new SubscribeChannel(strMC);
		MDB = new SubscribeChannel(strMDB);
		MDR= new SubscribeChannel(strMDR);
		Backup b = new Backup();	
	}

	
	
}
