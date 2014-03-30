package sdis;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Chunk implements Serializable{
	public String fileId;
	public int chunkNo;
	public int replicationDeg;
	public byte[] data;
	private ArrayList<String> storedPeers;
	private int chunkSize;

	public Chunk(String fileId, int chunkNo, int replicationDeg){
		this.fileId=fileId;
		this.chunkNo=chunkNo;
		this.replicationDeg=replicationDeg;
		chunkSize=0;

		storedPeers = new ArrayList<String>();
		try {
			storedPeers.add(InetAddress.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void addStoredPeer(String peer){
		boolean go=true;
		for(int i=0;i<storedPeers.size();i++){
			if(peer.equals(storedPeers.get(i)))
				go=false;
		}
		if(go){
			storedPeers.add(peer);
		}
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
		if(this.getNumberStored()>replicationDeg){
			//System.out.println("EXCEEDS");
			return true;
		}
		else
			return false;		
	}


	public synchronized int getSize(){
		return chunkSize;
	}

	public void setData(byte[] data) {
		this.data=data;
		chunkSize=data.length;
		
	}

	public void removePeer(String peer) {
		for(int i=0;i<storedPeers.size();i++){
			if(storedPeers.get(i).equals(peer))
				storedPeers.remove(i);
		}
		
	}
}
