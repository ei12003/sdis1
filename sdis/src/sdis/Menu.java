package sdis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Menu {
	private static SplitMessage m;
	private Peer peer;
	private int rep=2;
	public Menu(){
		
	}
	//ADD REPLICATIO
	public void menu(String args[]) throws Exception{
		if(args.length==8){
			peer = new Peer(args[0],args[1],args[2],args[3],args[4],args[5]);
			rep=Integer.parseInt(args[6]);
			DateFormat dateFormatMC = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMC = new Date();
			System.out.print(dateFormatMC.format(dateMC));
			System.out.println(" => MC: Init at " + args[0]);
			DateFormat dateFormatMDB = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMDB = new Date();
			System.out.print(dateFormatMDB.format(dateMDB));
			System.out.println(" => MDB: Init at " + args[2]);
			DateFormat dateFormatMDR = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMDR = new Date();
			System.out.print(dateFormatMDR.format(dateMDR));
			System.out.println(" => MDR: Init at " + args[4]);
		}
		else{
			peer = new Peer("225.4.5.6","5340","225.4.5.7","5341","225.4.5.8","5342");
			DateFormat dateFormatMC = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMC = new Date();
			System.out.print(dateFormatMC.format(dateMC));
			System.out.println(" => MC: Init at " + "225.4.5.6");
			DateFormat dateFormatMDB = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMDB = new Date();
			System.out.print(dateFormatMDB.format(dateMDB));
			System.out.println(" => MDB: Init at " + "225.4.5.7");
			DateFormat dateFormatMDR = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date dateMDR = new Date();
			System.out.print(dateFormatMDR.format(dateMDR));
			System.out.println(" => MDR: Init at " + "225.4.5.8");
		}
		
		m=new SplitMessage();
		String input="";
		getPreviousCFG();
		while(!input.equals("q")){
			// 1. Create a Scanner using the InputStream available.
			Scanner scanner = new Scanner( System.in );

		
			
			// 2. Don't forget to prompt the user
			System.out.print( "Type some data for the program: " );

			// 3. Use the Scanner to read a line of text from the user.
			input = scanner.nextLine();
			String splits[]=m.split(input);

			if(input.equals("h")||input.equals("H")){
				System.out.println("Help:");
				
				System.out.println("RESTORE filename - restore a file by its a name");
				
				System.out.println("DELETE filename - sent a DELETE message for the file with name filename");
				System.out.println("RECLAIM size - size in bytes");
				System.out.println("");
				
				System.out.println("BACKUP filename - backup a file with name filename");
				System.out.println("sb - show internal backup struct. Useful for debug only.");
				System.out.println("ss - show internal store struct. Useful for debug only.");
				System.out.println("h - help");
				System.out.println("q - quit");
				System.out.println("");
			}
			else if(input.equals("sb")){
				
				System.out.println("\nBacked Files\n");
				printMap(peer.backup.backedFiles);
				
				System.out.println("\nBackup Struct\n");
				for(int i=0;i<peer.backup.allBackedChunks.size();i++)
			      System.out.println("ChunkNo:"+peer.backup.allBackedChunks.get(i).chunkNo+" | TotalFileChunks:"+peer.backup.totalChunks.get(peer.backup.allBackedChunks.get(i).fileId)+" | FileID: "+peer.backup.allBackedChunks.get(i).fileId);
								
			}
			else if(input.equals("ss")){
				

				
				System.out.println("\nStored Struct"+peer.backup.allStoredChunks.size());
				for(int i=0;i<peer.backup.allStoredChunks.size();i++)
			      System.out.println("ChunkNo:"+peer.backup.allStoredChunks.get(i).chunkNo+" | FileID: "+peer.backup.allStoredChunks.get(i).fileId);
								
			}
			else if(splits[0].equals("RECLAIM")/*bool*/){
				if(!peer.reclaim(Integer.parseInt(splits[1])))
					System.out.println("Reclaim Failed.");
			}
			else if(splits[0].equals("DELETE")/*bool*/){
				if(!peer.deleteFile(splits[1]))
					System.out.println("Delete Failed.");
			}
			else if(splits[0].equals("BACKUP")/*bool*/){
				FileOutputStream fout;
				 ObjectOutputStream oos;
				 
				if(!peer.backup.backFile(splits[1], rep)) 
					System.out.println("\nBacked Failed!");
				
				fout = new FileOutputStream("allBackedChunks.bak");
		        oos = new ObjectOutputStream(fout);
		        oos.writeObject(peer.backup.allBackedChunks);
		        oos.close();

		        fout = new FileOutputStream("backedFiles.bak");
		        oos = new ObjectOutputStream(fout);
		        oos.writeObject(peer.backup.backedFiles);
		        oos.close();
		        
		        fout = new FileOutputStream("totalChunks.bak");
		        oos = new ObjectOutputStream(fout);
		        oos.writeObject(peer.backup.totalChunks);
		        oos.close();
		      
				
				
			}
			else if(splits[0].equals("RESTORE")){
				peer.restore.restoringATM=true;
				if(!peer.restore.restoreFile(splits[1]))
					System.out.println("Failed Restoring");
				peer.restore.restoringATM=false;

			}
			
			else if(input.equals("q")||input.equals("Q")){
				System.exit(0);
			}
			else {
				System.out.println("Wrong command. Enter h for help.");
			}
		}
	}
	private void getPreviousCFG() throws Exception {
		File file;
		FileInputStream fin;

		
		file = new File("allBackedChunks.bak");    
		if (file.exists()) {
			System.out.println("Restoring allBackedChunks.bak");
		    fin = new FileInputStream(file);
		    ObjectInputStream restore = new ObjectInputStream(fin);
		    peer.backup.allBackedChunks = (ArrayList<Chunk>)restore.readObject();
		    restore.close();
		}
		
		file = new File("allStoredChunks.bak");    
		if (file.exists()) {
			System.out.println("Restoring allStoredChunks.bak");
		    fin = new FileInputStream(file);
		    ObjectInputStream restore = new ObjectInputStream(fin);
		    peer.backup.allStoredChunks = (ArrayList<Chunk>)restore.readObject();
		    restore.close();
		}
		
		file = new File("backedFiles.bak");    
		if (file.exists()) {
			System.out.println("Restoring backedFiles.bak");
		    fin = new FileInputStream(file);
		    ObjectInputStream restore = new ObjectInputStream(fin);
		    peer.backup.backedFiles = (ConcurrentHashMap<String,String>)restore.readObject();
		    restore.close();
		}
		
		file = new File("totalChunks.bak");    
		if (file.exists()) {
			System.out.println("Restoring totalChunks.bak");
		    fin = new FileInputStream(file);
		    ObjectInputStream restore = new ObjectInputStream(fin);
		    peer.backup.totalChunks = (ConcurrentHashMap<String,Integer>)restore.readObject();
		    restore.close();
		}
		
		
	}
	public static void printMap(ConcurrentHashMap<String,String> mp) {
	    java.util.Iterator<Entry<String, String>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println("FileName:"+pairs.getKey() + " | FileId: " + pairs.getValue());

	    }
	}
}
