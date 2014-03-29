package sdis;

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
	private String body;

	public Message(String message) {
		String msg = new String(message);
		String[] temp = msg.split(" ",6);
		messageType = temp[0];

		if (messageType.equals("DELETE")) {
			fileId = temp[1];
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
				body=msg.split(CRLF+CRLF,2)[1];
				//System.out.println(body.length()+"$$$$$$$$$$$\n"+body+"\n$$$$$$$$$$");
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
		return null;
	}

	public String getBody() {
		return body;
	}


}
