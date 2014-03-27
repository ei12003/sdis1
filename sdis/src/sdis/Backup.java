package sdis;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Backup {
	public static final int INIT_BACKUP_TIMEOUT = 500;
	public static final int MAX_TRIES = 5;
	ArrayList<Chunk> allChunks;
	SubscribeChannel MDB;
	public static final int chuckSize = 64000;


	
	public Backup(SubscribeChannel MDB) throws NoSuchAlgorithmException, IOException{
		allChunks = new ArrayList<Chunk>();
		this.MDB=MDB;
		split(new File("file.jpg"),1);
	}
	
	
	public void split(File file, int replicationDeg) throws NoSuchAlgorithmException, IOException{
		
		int bytesRead,chunkNo=0;
		String bitString = file.getName()+file.lastModified();
		String fileID = SHA256.apply(bitString);
		BufferedInputStream fileBuffer = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[chuckSize];
		Chunk chunk;
		
		while((bytesRead=fileBuffer.read(buffer))>0){
			chunk = new Chunk(fileID,chunkNo,replicationDeg);
			sendChunk(chunk,buffer);
			allChunks.add(chunk);
			chunkNo++;
		}
		//putChunk("oi",2,2,"oi".getBytes());
	}
	
	public void sendChunk(Chunk chunk, byte[] data) throws IOException{
		
		int timeout = INIT_BACKUP_TIMEOUT;
		int tries=0;
		while(tries<MAX_TRIES || chunk.isDesiredReplication()){
			chunk.resetStoredPeers();
			putChunk(chunk.fileId, chunk.chunkNo, chunk.replicationDeg, data);
			
			try {
				Thread.sleep(timeout);
				timeout*=2;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			tries++;
		}
		
		
	}
	
	public void putChunk(String fileId, int chunkNo, int replicationDeg, byte[] data) throws IOException{
		String header= "PUTCHUNK"+"1.0"+fileId+chunkNo+replicationDeg+Message.CRLF+Message.CRLF;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( header.getBytes() );
		if(data != null)
			outputStream.write( data );	
		
		//SEND ..................................................                                                                                                                                            .toByteArray()
		
	}
}
