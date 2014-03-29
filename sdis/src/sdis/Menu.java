package sdis;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Menu {
	private static SplitMessage m;
	public Menu(){
		
	}
	public void menu(String args[]) throws NoSuchAlgorithmException, IOException{
		if(args.length==7){
			Peer peer = new Peer(args[0],args[1],args[2],args[3],args[4],args[5]);
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
			Peer peer = new Peer("225.4.5.6","5340","225.4.5.7","5341","225.4.5.8","5342");
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
				System.out.println("RESTORE fileID - restore a file by its fileID");
				System.out.println("Restore filename - restore a file by its a name");
				System.out.println("DELETE fileID - sent a DELETE  message for the file with id fileID");
				System.out.println("DELETE filename - sent a DELETE message for the file with name filename");
				System.out.println("");
				System.out.println("REMOVE fileID N - sent a REMOVE message for chunk N of filleID");
				System.out.println("BACKUP filename - backup a file with name filename");
				System.out.println("sb - show internal backup struct. Useful for debug only.");
				System.out.println("ss - show internal store struct. Useful for debug only.");
				System.out.println("h - help");
				System.out.println("q - quit");
				System.out.println("");
			}
			else if(splits[0].equals("BACKUP")/*bool*/){

			}
			else if(splits[0].equals("RESTORE")){

			}
			else if(splits[0].equals("DELETE")){

			}
			else if(splits[0].equals("REMOVE")){

			}
			else if(input.equals("q")||input.equals("Q")){
				System.exit(0);
			}
			else {
				System.out.println("Wrong command. Enter h for help.");
			}
		}
	}
}
