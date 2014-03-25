package sdis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Peer {
	private SubscribeChannel MC,MDB,MDR;
	
	public Peer(String MCaddr, String MCport,String MDBaddr, String MDBport, String MDRaddr, String MDRport) throws NoSuchAlgorithmException, IOException{

		MC = new SubscribeChannel(MCaddr,MCport);
		MDB = new SubscribeChannel(MDBaddr,MDBport);
		MDR= new SubscribeChannel(MDRaddr,MDRport);
		Backup b = new Backup(MDB);	
	}

	
	
}
