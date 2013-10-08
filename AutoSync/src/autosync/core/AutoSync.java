package autosync.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.service.IoAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autosync.properties.FileMetaData;
import autosync.properties.NeighborNode;
import autosync.util.QueryNodeTimerTask;

public class AutoSync {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AutoSync.class);
	
	// A list of connected neighboring nodes.
	public static List<NeighborNode> neighborNodes;
	// A list of files stored in the specified sync folder.
	public static List<FileMetaData> fileMetaList;
	// Current node's id (ip plus port) with format of "ip:port".
	public static String nodeId;
	// A list of connected neighboring nodes with form of ip:port
	public static List<String> neighborNodesStr;
	// Public listening port
	public static final int PORT = 9000;
	// Acceptor to accept remote connections from PORT
	public static IoAcceptor acceptor;
	// Periodically query neighboring node for their neighboring nodes.
	public static Timer timer;
	
	public static void initialize(){
		neighborNodes = Collections.synchronizedList(new ArrayList<NeighborNode>());
		fileMetaList = Collections.synchronizedList(new ArrayList<FileMetaData>());
		neighborNodesStr = Collections.synchronizedList(new ArrayList<String>());
		acceptor = null;
		timer = null;
		try {
			nodeId = InetAddress.getLocalHost().getHostAddress() + ":" + PORT;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error("Local host IP cannot be determined.");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize application
		AutoSync.initialize();
		LOGGER.info("=================Usage Instruction=================\n" +
                     "> java AutoSync [IP PORT]");
		
		if(args.length < 2){
			// Start listening on PORT for incoming connections.
			NetworkJoin.startListen();
		}else{
			// Start listening on PORT for incoming connections.
			NetworkJoin.startListen();
			// Connect an existing node to join the peer to peer network.
			String remoteNodeIp = args[0];
			int port = Integer.valueOf(args[1]);
			InetSocketAddress inetSockAddr = new InetSocketAddress(remoteNodeIp, port);
			NeighborNode newRemoteNode = NetworkJoin.connectNode(inetSockAddr);
			neighborNodes.add(newRemoteNode);
			neighborNodesStr.add(remoteNodeIp + ":" + port);
			
			// Query neighboring connected nodes periodically.
			TimerTask queryTimerTask = new QueryNodeTimerTask();
			timer = new Timer();
			timer.schedule(queryTimerTask, 1000, 2000);
		}
	}

}
