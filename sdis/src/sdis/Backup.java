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
import java.util.concurrent.ThreadLocalRandom;

public class Backup {
	public static final int INIT_BACKUP_TIMEOUT = 500;
	public static final int MAX_TRIES = 5;
	ArrayList<Chunk> allBackedChunks, allStoredChunks;
	SubscribeChannel MDB, MC;
	public Chunk currentChunk;
	public static final int chuckSize = 64000;
	public String teststr;

	public Backup(SubscribeChannel MDB, SubscribeChannel MC)
			throws NoSuchAlgorithmException, IOException {
		allBackedChunks = new ArrayList<Chunk>();
		allStoredChunks = new ArrayList<Chunk>();
		this.MDB = MDB;
		this.MC = MC;
	}

	public synchronized void saveChunk(Message msg) {
		boolean go = true;
		if (msg.getChunkNo() == 10)
			System.out.println("sup");
		for (int i = 0; i < allStoredChunks.size(); i++) {
			// System.out.println("!"+allStoredChunks.size()+"!"+allStoredChunks.get(i).chunkNo+"||"+msg.getChunkNo());
			if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
					&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
				allStoredChunks.get(i).resetStoredPeers();
				System.out.println("REPEATED: " + msg.getChunkNo());
				go = false;
			}
		}
		if (go) {
			Chunk newChunk = new Chunk(msg.getFileId(), msg.getChunkNo(),
					msg.getReplicationDeg());
			newChunk.setData(msg.getBody());
			allStoredChunks.add(newChunk);
		}
		System.out.println("->" + allStoredChunks.size());
		// STORED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
		String header = "STORED" + " 1.0 " + msg.getFileId() + " "
				+ msg.getChunkNo() + " " + Message.CRLF + Message.CRLF;
		try {
			int randomDelay = ThreadLocalRandom.current().nextInt(1, 400);
			Thread.sleep(randomDelay); // waits random delay before sending
			// message
			for (int i = 0; i < allStoredChunks.size(); i++) {
			if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
					&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
				
				if(!allStoredChunks.get(i).exceedsReplication())
					MC.send(header.getBytes());
				else
					allStoredChunks.remove(i);
			}
			}
			
			
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean split(File file, int replicationDeg)
			throws NoSuchAlgorithmException, IOException {
		int bytesRead, chunkNo = 0;
		String bitString = file.getName() + file.lastModified();
		String fileID = SHA256.apply(bitString);
		BufferedInputStream fileBuffer = new BufferedInputStream(
				new FileInputStream(file));
		byte[] buffer = new byte[chuckSize];
		Chunk chunk;

		while ((bytesRead = fileBuffer.read(buffer)) > 0) {
			chunk = new Chunk(fileID, chunkNo, replicationDeg);
			if(!sendChunk(chunk, buffer))
				return false;

			allBackedChunks.add(chunk);
			chunkNo++;
		}
		return true;
	}

	public boolean sendChunk(Chunk chunk, byte[] data) throws IOException {

		int timeout = INIT_BACKUP_TIMEOUT;
		int tries = 0;

		while (tries < MAX_TRIES) {
			currentChunk = chunk;
			chunk.resetStoredPeers();
			putChunk(chunk.fileId, chunk.chunkNo, chunk.replicationDeg, data);

			try {
				Thread.sleep(timeout);
				timeout *= 2;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("TIMED OUT" + chunk.isDesiredReplication()
					+ " # ChunkNo " + chunk.chunkNo + "# PeerStored "
					+ chunk.getNumberStored() + "#");

			if (chunk.isDesiredReplication())
				break;
			tries++;
		}
		if (tries == MAX_TRIES)
			return false;

		else
			return true;

	}

	public void putChunk(String fileId, int chunkNo, int replicationDeg,
			byte[] data) throws IOException {
		String header = "PUTCHUNK" + " 1.0 " + fileId + " " + chunkNo + " "
				+ replicationDeg + " " + Message.CRLF + Message.CRLF;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(header.getBytes());
		if (data != null)
			outputStream.write(data);

		MDB.send(outputStream.toByteArray());
		System.out.println("PUTCHUNK " + fileId + "\n");

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
}
