package sdis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class DBS {
	private static SplitMessage m;
	private static Menu menu;
	public static void main(String[] args) throws Exception {

		/*if(args.length<6){ // 1.1.1.1 1111 1.1.1.1 1111 1.1.1.1 1111
			System.out.println("java Peer <MC_ADDR> <MC_PORT> <MDB_ADDR> <MDB_PORT> <MDR_ADDR> <MDR_PORT>\n");
			System.out.println("MC: Control Channel | MDB: Backup Channel | MDR: Restore Channel\n");
			return;
		}*/

		/*Peer peer = new Peer(args[0],args[1],  // MC
							 args[2],args[3],  // MDB
							 args[4],args[5]); // MDR
		 */
		//Peer peer = new Peer("225.4.5.6","5340","225.4.5.7","5341","225.4.5.8","5342");
		//String oi="PUTCHUNK <Version> <FileId> <ChunkNo> <ReplicationDeg> "+Message.CRLF+Message.CRLF+"<Body> asdasd asdasd";
		//String oi2="PUTCHUNK <Version> <FileId>";
		//System.out.println(oi.split(Message.CRLF+Message.CRLF)[1]);
		menu=new Menu();
		menu.menu(args);
		
	}
}
