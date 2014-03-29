package sdis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Peer {
	private SubscribeChannel MC,MDB,MDR;
	static Thread threadMC,threadMDB,threadMDR;
	
	
	public Peer(String MCaddr, String MCport,String MDBaddr, String MDBport, String MDRaddr, String MDRport) throws NoSuchAlgorithmException, IOException{

		MC = new SubscribeChannel(MCaddr,MCport);
		MDB = new SubscribeChannel(MDBaddr,MDBport);
		//MDR= new SubscribeChannel(MDRaddr,MDRport);
		System.out.println(MCaddr+"|"+MDBaddr+"|"+MDRaddr);
		threadMC= new Thread(MC);
		threadMDB= new Thread(MDB);
		//threadMDR= new Thread(MDR);
		
		Backup backup = new Backup(MDB,MC);
		//Restore restore = new Restore(MDR,MC,backup);
		MC.setBackup(backup);
		MDB.setBackup(backup);
		
		threadMC.start();
		threadMDB.start();
		//threadMDR.start();
		
		/*while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("oi");
		}*/
		//if(!backup.split(new File("file.jpg"),1))
		//	System.out.println("FAILED");
		//MDB.setBackup(backup);
		//MDR.setBackup(backup);
		
	
			 
		
	}
}
