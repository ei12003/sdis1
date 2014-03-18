import java.util.HashMap;

public class Database {
	HashMap<String, String> database;
	
	public Database() {
		database = new HashMap<String,String>();
	}
	
	public int register(String plate, String owner) {
		if(database.containsKey(plate))
			return -1;
		database.put(plate, owner);
		return database.size();
	}
	
	public String lookUp(String plate) {
		if(database.containsKey(plate))
			return database.get(plate);
		return "NOT_FOUND";
	}
}
