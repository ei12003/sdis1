package sdis;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Backup implements Serializable{
	public static final int INIT_BACKUP_TIMEOUT = 500;
	public static final int MAX_TRIES = 5;
	ArrayList<Chunk> allBackedChunks, allStoredChunks;
	ConcurrentHashMap<String,String> backedFiles;
	ConcurrentHashMap<String,Integer> totalChunks;
	SubscribeChannel MDB, MC, MDR;
	public Chunk currentChunk;
	public static final int chuckSize = 64000;
	public String teststr;

	public Backup(SubscribeChannel MDB, SubscribeChannel MC, SubscribeChannel MDR)
			throws NoSuchAlgorithmException, IOException {
		allBackedChunks = new ArrayList<Chunk>();
		allStoredChunks = new ArrayList<Chunk>();
		backedFiles = new ConcurrentHashMap<String,String>();
		totalChunks = new ConcurrentHashMap<String,Integer>();
		this.MDB = MDB;
		this.MC = MC;
		this.MDR = MDR;
	}

	public synchronized void saveChunk(Message msg) {
		boolean go = true;
		if (msg.getChunkNo() == 10)
			System.out.println("sup");
		for (int i = 0; i < allStoredChunks.size(); i++) {
			 System.out.println("!!!!!!"+allStoredChunks.size()+"!"+allStoredChunks.get(i).chunkNo+"||"+msg.getChunkNo());
			if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
					&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
				allStoredChunks.get(i).resetStoredPeers();
				System.out.println("REPEATED: " + msg.getChunkNo());
				go = false;
			}
		}
		if (go) {
			System.out.println("????????");
			Chunk newChunk = new Chunk(msg.getFileId(), msg.getChunkNo(),
					msg.getReplicationDeg());
			newChunk.setData(msg.getData());
			System.out.println(msg.getData().length+"|"+newChunk.getSize());
			allStoredChunks.add(newChunk);
		}
		System.out.println("->" + allStoredChunks.size());
		// STORED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
		String header = "STORED" + " 1.0 " + msg.getFileId() + " "
				+ msg.getChunkNo()+Message.CRLF + Message.CRLF;
		try {
			int randomDelay = ThreadLocalRandom.current().nextInt(1, 400);
			Thread.sleep(randomDelay); // waits random delay before sending
			// message
			for (int i = 0; i < allStoredChunks.size(); i++) {
			if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
					&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
				
				if(!allStoredChunks.get(i).exceedsReplication()){
					MC.send(header.getBytes());
			        
				}
				else{
					System.out.println("REMOVING:"+allStoredChunks.size()+allStoredChunks.get(i).exceedsReplication());
					allStoredChunks.remove(i);
					
				}
			}
			}
			
			
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean backFile(String filename, int rep) throws NoSuchAlgorithmException, IOException, InterruptedException{
		if(!split(new File(filename),rep))
			return false;
		return true;
	}
	
	public boolean split(File file, int replicationDeg)
			throws NoSuchAlgorithmException, IOException, InterruptedException {
		int bytesRead, chunkNo = 0;
		boolean go = true;
		int bytesRead2 = 0;
		String filename = file.getName();
		String bitString = filename + file.lastModified();
		String fileID = SHA256.apply(bitString);
		BufferedInputStream fileBuffer = null;
		if(file.exists())
		fileBuffer = new BufferedInputStream(
				new FileInputStream(file));
		else{
			System.out.println("File doesn't exist!");
			return false;
		}
		
		byte[] buffer = new byte[chuckSize];
		byte[] newbuffer;
		Chunk chunk;

		while ((bytesRead = fileBuffer.read(buffer)) > 0) {
			bytesRead2 = bytesRead;
			go=true;
			newbuffer = Arrays.copyOfRange(buffer, 0,bytesRead);
			
			chunk = new Chunk(fileID, chunkNo, replicationDeg);
			if(!sendChunk(chunk, newbuffer, "MDB")){
				fileBuffer.close();
				return false;
			}

			
			for(int i=0;i<allBackedChunks.size();i++)
				if(allBackedChunks.get(i).chunkNo==chunk.chunkNo && allBackedChunks.get(i).fileId.equals(chunk.fileId))
					go=false;
			
			if(go)
				allBackedChunks.add(chunk);
			chunkNo++;
			totalChunks.put(fileID, chunkNo);

		}
		if(bytesRead2==64000){
			System.out.println("BYTESREAD2");
			newbuffer = null;
			
			chunk = new Chunk(fileID, chunkNo, replicationDeg);
			if(!sendChunk(chunk, newbuffer, "MDB")){
				fileBuffer.close();
				return false;
			}
			
			for(int i=0;i<allBackedChunks.size();i++)
				if(allBackedChunks.get(i).chunkNo==chunk.chunkNo && allBackedChunks.get(i).fileId.equals(chunk.fileId))
					go=false;
			if(go)
				allBackedChunks.add(chunk);
			chunkNo++;
			totalChunks.put(fileID, chunkNo);

		}
		
		fileBuffer.close();
		backedFiles.put(filename, fileID);
		return true;
	}

	public boolean sendChunk(Chunk chunk, byte[] data, String strChannel) throws IOException, InterruptedException {

		int timeout = INIT_BACKUP_TIMEOUT;
		int tries = 0;
		String header;


		while (tries < MAX_TRIES) {
			if(strChannel.equals("MDB")){
				currentChunk = chunk;
				chunk.resetStoredPeers();
				//PUTCHUNK <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF> <CRLF> <Body>
				header = "PUTCHUNK" + " 1.0 " + chunk.fileId + " " + chunk.chunkNo + " "
						+ chunk.replicationDeg + Message.CRLF + Message.CRLF;
				sendChunkToChannel(MDB, data, header);
				
				Thread.sleep(timeout);
				timeout *= 2;	
				
				System.out.println("TIMED OUT MDB" + chunk.isDesiredReplication()
						+ " # ChunkNo " + chunk.chunkNo + "# PeerStored "
						+ chunk.getNumberStored() + "#");

				if (chunk.isDesiredReplication())
					break;
			}
			else if(strChannel.equals("MDR")){
				//CHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF> <Body>
				header = "CHUNK"+ " 1.0 " + chunk.fileId + " " + chunk.chunkNo + Message.CRLF + Message.CRLF;
				int randomDelay = ThreadLocalRandom.current().nextInt(1, 400);
				Thread.sleep(randomDelay); // waits random delay before sending
				
				sendChunkToChannel(MDR, data, header);
				
				
				break;
				/*Thread.sleep(timeout);
				timeout *= 2;	
			
				System.out.println("TIMED OUT MDR" + " # ChunkNo " + chunk.chunkNo );

				if (chunk.isDesiredReplication())
					break;*/
				
			}
				
			tries++;
		}
		if (tries == MAX_TRIES)
			return false;

		else
			return true;

	}

	public void sendChunkToChannel(SubscribeChannel channel, byte[] data, String header) throws IOException {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header.getBytes());
		if (data != null){
			System.out.println("LEEEEn"+(new String(data)).length());
			outputStream.write(data);
		}

		channel.send(outputStream.toByteArray());
		System.out.println("SENDING CHUNK:"+header+"\n");

	}

	public void updateChunkPeers(Message msg, String peer) {
		for (int i = 0; i < allStoredChunks.size(); i++) {
			// System.out.println("!"+allStoredChunks.size()+"!"+allStoredChunks.get(i).chunkNo+"||"+msg.getChunkNo());
			if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
					&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
				System.out.println("REPEATED: " + msg.getChunkNo());
				allStoredChunks.get(i).addStoredPeer(peer);
			}
		}

	}
	
	public void updateAllBackedChunksFile() throws IOException, ClassNotFoundException{
		FileOutputStream fout = new FileOutputStream("allBackedChunks.bak");
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(fout);
		   oos.writeObject(allBackedChunks);
	        oos.close();
	}
	public void updateAllStoredChunksFile() throws IOException, ClassNotFoundException{
		FileOutputStream fout = new FileOutputStream("allStoredChunks.bak");
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(fout);
		   oos.writeObject(allStoredChunks);
	        oos.close();
		

	}
	public void updateBackedFilesFile() throws IOException, ClassNotFoundException{
		FileOutputStream fout = new FileOutputStream("backedFiles.bak");
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(fout);
		   oos.writeObject(backedFiles);
	        oos.close();
	}
	public void updateTotalChunksFile() throws IOException, ClassNotFoundException{
	
		
		FileOutputStream fout = new FileOutputStream("totalChunks.bak");
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(fout);
		   oos.writeObject(totalChunks);
	        oos.close();
	}

	public boolean removedSent(Message msg, String peer) throws IOException, InterruptedException {

		for(int i=0;i<allStoredChunks.size();i++){
			if(allStoredChunks.get(i).fileId.equals(msg.getFileId())
					&& allStoredChunks.get(i).chunkNo == msg.getChunkNo())
			{
				allStoredChunks.get(i).removePeer(peer);
				if(!allStoredChunks.get(i).isDesiredReplication()){
					if(!sendChunk(allStoredChunks.get(i), allStoredChunks.get(i).data, "MDB")){
						System.out.println("Couldn't send file.");
					return false;
				}
				}
					
				System.out.println("ChunkNo: "+msg.getChunkNo()+"removed");
				return true;
			}
		}
		
		System.out.println("I don't have that chunk.");
		return false;
	}
}
