package sdis;

import java.util.Arrays;

public class Message {
	// <MessageType> <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>
	public static final char CHARSQ1=0xD;
	public static final char CHARSQ2=0xA;
	public static final String CRLF=""+CHARSQ1+CHARSQ2;
	private String messageType;
	private String version;
	private String fileId;
	private int chunkNo;
	private int replicationDeg;
	private byte[] body;

	public byte[] splitBody(byte[] data,int part){
		for(int i=0;i<data.length;i++){
			if(data[i]==13 && data[i+1]==10 && data[i+2]==13 && data[i+3]==10){
				if(part==1){
				byte[] body = Arrays.copyOfRange(data, i+4, data.length);
					return body;
				}
				else{
					byte[] body = Arrays.copyOfRange(data, 0, i);
					return body;
				}
			}
			
		}
		
		return null;
	}
	
	public Message(byte[] messageBytes) {
		String msg = new String(messageBytes);
		String[] temp = msg.split(" ",6);
		messageType = temp[0];

		if (messageType.equals("DELETE")) {

			fileId = new String(splitBody(temp[1].getBytes(),2));
			
		} else {
			if (messageType.equals("PUTCHUNK")){
				replicationDeg = Integer.parseInt(temp[4].split(CRLF+CRLF)[0]);
				chunkNo = Integer.parseInt(temp[3]);
			}
			else
				chunkNo = Integer.parseInt(temp[3].split(CRLF+CRLF)[0]);
			version = temp[1];
			fileId = temp[2];

			if (messageType.equals("PUTCHUNK") || messageType.equals("CHUNK")){
				body=splitBody(messageBytes,1);
				
			}
		}
	}

	public String getMessageType() {
		return messageType;
	}

	public String getVersion() {
		return version;
	}

	public String getFileId() {
		return fileId;
	}

	public int getReplicationDeg() {
		return replicationDeg;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public byte[] getData() {
		// TODO Auto-generated method stub
		return body;
	}

	


}
