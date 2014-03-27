package sdis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Peer {
	private static SubscribeChannel MC,MDB,MDR;
	static Thread threadMC;
	
	
	public Peer(String MCaddr, String MCport,String MDBaddr, String MDBport, String MDRaddr, String MDRport) throws NoSuchAlgorithmException, IOException{

		MC = new SubscribeChannel(MCaddr,MCport);
		MDB = new SubscribeChannel(MDBaddr,MDBport);
		MDR= new SubscribeChannel(MDRaddr,MDRport);
		
		Backup backup = new Backup(MDB);
		
		MC.setBackup(backup);
		MDB.setBackup(backup);
		MDR.setBackup(backup);
		
		//threadMC= new Thread(MC);
		//threadMC.start();
			 
		
	}
}
