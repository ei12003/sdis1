package sdis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Restore {
	SubscribeChannel MDR, MC;
	ArrayList<Chunk> fileRestoring;
	Backup backup;

	public Restore(SubscribeChannel MDR, SubscribeChannel MC, Backup backup) {
		this.MDR = MDR;
		this.MC = MC;
		this.backup = backup;
		fileRestoring = new ArrayList<Chunk>();
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
						if(fileRestoring.get(j).chunkNo == i){
							exists=true;
							break;
						}	
					}
					if(exists){
						System.out.println(">>>>>EXISTE");
						break;
					}
					else
						System.out.println(">>>>>NAOEXISTE"+tries);
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
					+ backup.totalChunks.get(fileId) + "<<STORED>>:"+backup.allStoredChunks.size());
			if(!getChunks(backup.totalChunks.get(fileId), fileId))
				return false;
			else{
				FileOutputStream fos = new FileOutputStream("pathname.txt");
				for(int i=0;i<fileRestoring.size();i++){
					fos.write(fileRestoring.get(i).data);
				}
				fos.close();
				System.out.println("RESTORED");
				return true;
			}
		} else
			System.out.println("<<NULL>>");
		return false;

	}

}
