package sdis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class DBS {
public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
	
		/*if(args.length<6){ // 1.1.1.1 1111 1.1.1.1 1111 1.1.1.1 1111
			System.out.println("java Peer <MC_ADDR> <MC_PORT> <MDB_ADDR> <MDB_PORT> <MDR_ADDR> <MDR_PORT>\n");
			System.out.println("MC: Control Channel | MDB: Backup Channel | MDR: Restore Channel\n");
			return;
		}*/
		
		Peer peer = new Peer(args[0],args[1],  // MC
							 args[2],args[3],  // MDB
							 args[4],args[5]); // MDR
		
		
}
}
