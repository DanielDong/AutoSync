package autosync.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * This class formats the messages transferred between nodes at application level.
 * Each message is finally transferred as a String object with semi-colon separating each field
 * in a message.
 * @author shichaodong
 *
 */
public class Message {
	private int type;
	private List<String> body;
	
	public Message(){}
	public Message(int t){
		type = t;
		body = new ArrayList<String>();
	}
	
	public void setType(int t){type = t;}
	public void addRecord(String record){body.add(record);}
	
	@Override
	public String toString(){
		StringBuilder message = new StringBuilder();
		message.append(type);
		message.append(";");
		for(int i = 0; i < body.size(); i ++){
			message.append(body.get(i));
			if(i < body.size() - 1)
				message.append(";");
		}
		return message.toString();
	}
}
