package sdis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AllPermission;
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


	public int findTotalSpace(){
		int size=0;
		for(int i=0;i<backup.allStoredChunks.size();i++){
			size+=backup.allStoredChunks.get(i).getSize();
		}
		return size;
	}
	public boolean reclaim(int numSize) throws IOException {
		System.out.println("Total space before: "+findTotalSpace());
		int size=0;
		while(size<numSize){
			if(backup.allStoredChunks.size()==0)
			{
				System.out.println("No more space available to free.");
				return true;
			}
			size+=backup.allStoredChunks.get(0).getSize();
			
			String header = "REMOVED" + " 1.0 "+ backup.allStoredChunks.get(0).fileId + " " + backup.allStoredChunks.get(0).chunkNo + Message.CRLF + Message.CRLF;
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header.getBytes());
			MC.send(outputStream.toByteArray());
			System.out.println("SENDING :"+header+"\n");
			
			backup.allStoredChunks.remove(0);
		}
		System.out.println("Total space after: "+findTotalSpace());
		try {
			backup.updateAllStoredChunksFile();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
