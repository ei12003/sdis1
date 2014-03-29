package sdis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Chunk {
	public String fileId;
	public int chunkNo;
	public int replicationDeg;
	public String data;
	private ArrayList<String> storedPeers;
	private int chunkSize;

	public Chunk(String fileId, int chunkNo, int replicationDeg){
		this.fileId=fileId;
		this.chunkNo=chunkNo;
		this.replicationDeg=replicationDeg;

		storedPeers = new ArrayList<String>();
		try {
			storedPeers.add(InetAddress.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public synchronized boolean exceedsReplication(){
		if(this.getNumberStored()>replicationDeg)
			return true;
		else
			return false;		
	}

	public void setData(String body) {
		this.data=body;
		chunkSize=data.length();
	}
	public synchronized int getSize(){
		return chunkSize;
	}
}
