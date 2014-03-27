package sdis;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Chunk {
	public String fileId;
	public int chunkNo;
	public int replicationDeg;
	private ArrayList<String> storedPeers;

	public Chunk(String fileId, int chunkNo, int replicationDeg){
		this.fileId=fileId;
		this.chunkNo=chunkNo;
		this.replicationDeg=replicationDeg;

		storedPeers = new ArrayList<String>();
	}
	
	public synchronized void addStoredPeer(String peer){
		storedPeers.add(peer);
	}
	
	public synchronized void resetStoredPeers(){
		storedPeers.clear();
	}
	
	public synchronized int getNumberStored(){
		return storedPeers.size();
	}
	
	public synchronized boolean isDesiredReplication(){
		if(this.getNumberStored()>=replicationDeg)
			return true;
		else
			return false;
	}
}
