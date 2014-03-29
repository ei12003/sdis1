package sdis;

public class Restore {
	SubscribeChannel MDR, MC;
	Backup backup;
	public Restore(SubscribeChannel MDR, SubscribeChannel MC, Backup backup){
		this.MDR = MDR;
		this.MC = MC;
		this.backup = backup;
	}
	
	public void getChunk(){
		//String header = "GETCHUNK" + " 1.0 " + msg.getFileId() + " "
			//	+ msg.getChunkNo() + " " + Message.CRLF + Message.CRLF;
	//	try {
		
	}
	
	
}
