package autosync.properties;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

/**
 * An NeighborNode object represents a connected neighboring node.
 * @author shichaodong
 * @version 1.0
 */
public class NeighborNode {
	// Data channel to a connected neighbor node.
	private IoSession session;
	// Service associated current session.
	private IoConnector connector;
	
	public NeighborNode(){
		session = null;
		connector = null;
	}
	
	public IoSession getSession(){return session;}
	public void setSession(IoSession s){session = s;}
	
	public IoConnector getConnector(){return connector;}
	public void setConnector(IoConnector c){connector = c;}
}
