package sdis;

public class SplitMessage {
	public  SplitMessage(){
		
	}
	public static String[] split(String message){
		String[] splits = message.split(" ");
		return splits;
		}
}
