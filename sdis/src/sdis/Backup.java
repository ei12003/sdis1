package sdis;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Backup {
	
	SubscribeChannel MDB;
	public static final int chuckSize = 64000;
	
	public Backup(SubscribeChannel MDB) throws NoSuchAlgorithmException, IOException{
		this.MDB=MDB;
		split(new File("file.jpg"),1);
	}
	
	
	public void split(File file, int replicationDeg) throws NoSuchAlgorithmException, IOException{
		
		int bytesRead;
		String bitString = file.getName()+file.lastModified();
		String fileID = SHA256.apply(bitString);
		BufferedInputStream fileBuffer = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[chuckSize];
		Chunk chunk = new Chunk(fileID,0,replicationDeg,null);
		
		while((bytesRead=fileBuffer.read(buffer))>0){
			sendChunk(chunk,buffer);
			chunk.chunkNo++;
		}
		//putChunk("oi",2,2,"oi".getBytes());
	}
	
	public void sendChunk(Chunk chunk, byte[] data){
		
		
		
	}
	
	public void putChunk(String fileId, int chunkNo, int replicationDeg, byte[] data) throws IOException{
		String header= "PUTCHUNK"+"1.0"+fileId+chunkNo+replicationDeg+Message.CRLF+Message.CRLF;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( header.getBytes() );
		if(data != null)
			outputStream.write( data );	
		
		//SEND OUTPUTSTREAM.toByteArray()
		
	}
}