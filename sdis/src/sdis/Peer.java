package sdis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Peer {
	private SubscribeChannel MC,MDB,MDR;
	static Thread threadMC,threadMDB,threadMDR;
	public Backup backup;
	public Restore restore;
	
	public Peer(String MCaddr, String MCport,String MDBaddr, String MDBport, String MDRaddr, String MDRport) throws NoSuchAlgorithmException, IOException, InterruptedException{

		MC = new SubscribeChannel(MCaddr,MCport);
		MDB = new SubscribeChannel(MDBaddr,MDBport);
		MDR= new SubscribeChannel(MDRaddr,MDRport);
		System.out.println(MCaddr+"|"+MDBaddr+"|"+MDRaddr);
		threadMC= new Thread(MC);
		threadMDB= new Thread(MDB);
		threadMDR= new Thread(MDR);
		
		backup = new Backup(MDB,MC,MDR);
		restore = new Restore(MDR,MC,backup);
		MC.setBackup(backup);
		MDB.setBackup(backup);
		MC.setRestore(restore);
		MDR.setRestore(restore);
		
		threadMC.start();
		threadMDB.start();
		threadMDR.start();
		
		/*while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("oi");
		}*/
		/*if(!backup.backFile("file.jpg",2))
			System.out.println("FAILED");
		else{
			System.out.println("BACKED");
			String fileId = backup.backedFiles.get("file.jpg");
			System.out.println("<<EXISTS>>:" + fileId + "\n<<TOTAL CHUNKS>>:"
					+ backup.totalChunks.get(fileId) + "<<STORED>>:"+backup.allStoredChunks.size());
			restore.restoringATM=true;
			if(!restore.restoreFile("file.jpg"))
				System.out.println("FAILED RESTORING");
			else
				System.out.println("RESTORED");
		}*/
		
		//MDB.setBackup(backup);
		//MDR.setBackup(backup);

			 
		
	}

	public boolean deleteFile(String string) throws Exception {
		String fileId = backup.backedFiles.get(string);
		if(fileId==null){
			System.out.println("File doesn't exist.");
			return false;
		}			
		String header = "DELETE " + fileId + Message.CRLF + Message.CRLF;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header.getBytes());
		MC.send(outputStream.toByteArray());
		System.out.println("SENDING :"+header+"\n");
		
		
		int removed;
		do{
			removed=0;
		for(int i=0;i<backup.allBackedChunks.size();i++){
			if(backup.allBackedChunks.get(i).fileId.equals(fileId)){
				removed++;
				backup.allBackedChunks.remove(i);
			}
		}
		}while(removed>0);
		backup.updateAllBackedChunksFile();
		
		
		backup.backedFiles.remove(string);
		backup.updateBackedFilesFile();
		
		backup.totalChunks.remove(string);
		backup.updateTotalChunksFile();
		
		File f=new File(string);
		if(f.delete())
			return true;
		else{
			System.out.println("Can't Delete File");
			return false;
		}
			
	}
}
