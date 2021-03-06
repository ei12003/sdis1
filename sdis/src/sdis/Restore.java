package sdis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Restore implements Serializable {
	SubscribeChannel MDR, MC;
	ConcurrentHashMap<Integer,Chunk> fileRestoring;
	Backup backup;
	public boolean restoringATM;
	public Restore(SubscribeChannel MDR, SubscribeChannel MC, Backup backup) {
		this.MDR = MDR;
		this.MC = MC;
		this.backup = backup;
		fileRestoring = new ConcurrentHashMap<Integer,Chunk>();
		restoringATM = false;
	}

	public boolean getChunks(int total, String fileId) {
		String header;
		boolean exists=false;
		int tries = 0;
		int timeout = backup.INIT_BACKUP_TIMEOUT;
		// GETCHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF>
		for (int i = 0; i < total; i++) {
			header = "GETCHUNK" + " 1.0 " + fileId + " " + i + Message.CRLF
					+ Message.CRLF;
			try {
				while (tries < backup.MAX_TRIES) {
					MC.send(header.getBytes());
					Thread.sleep(timeout);
					//timeout *= 2;
					for(int j=0;j<fileRestoring.size();j++)
					{
						if(fileRestoring.containsKey(i)){
							exists=true;
							break;
						}	
					}
					if(exists){
						
						break;
					}
					
					tries++;
				}
				if (tries == backup.MAX_TRIES)
					return false;

			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeout = backup.INIT_BACKUP_TIMEOUT;
			tries = 0;
		}
		return true;

	}

	public boolean restoreFile(String string) throws IOException {
		String fileId = backup.backedFiles.get(string);
		
		if (fileId != null) {
			System.out.println("<<EXISTS>>:" + fileId + "\n<<TOTAL CHUNKS>>:"
					+ backup.totalChunks.get(fileId) + "\nRestoring...");
			if(!getChunks(backup.totalChunks.get(fileId), fileId))
				return false;
			else{
				FileOutputStream fos = new FileOutputStream(string);
				
				for(int i=0;i<fileRestoring.size();i++){
					
					fos.write(fileRestoring.get(i).data);
				}
				fos.close();
				System.out.println("File Restored");
				return true;
			}
		} else
			System.out.println("<<NULL>>");
		return false;

	}

}
