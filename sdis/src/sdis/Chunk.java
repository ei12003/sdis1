package sdis;

public class Chunk {
	public String fileId;
	public int chunkNo;
	public int replicationDeg;
	public byte[] data;
	
	public Chunk(String fileId, int chunkNo, int replicationDeg, byte[] data){
		this.fileId=fileId;
		this.chunkNo=chunkNo;
		this.replicationDeg=replicationDeg;
		this.data=data;
	}
	
}
