package autosync.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autosync.properties.Message;
import autosync.properties.NeighborNode;

/**
 * An NetworkJoin object joins current node to an existing peer to peer network.
 * @author shichaodong
 * @version 1.0
 */
public class NetworkJoin extends IoHandlerAdapter{
	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkJoin.class);
	
	/** Start current node to listen for both remote node joining requests and command line requests. 
	 * 
	 */
	public static void startListen(){
		AutoSync.acceptor = new NioSocketAcceptor();
		DefaultIoFilterChainBuilder chainBuilder = AutoSync.acceptor.getFilterChain();
		chainBuilder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		IoSessionConfig config = AutoSync.acceptor.getSessionConfig();
		config.setMaxReadBufferSize(4096);
		config.setMinReadBufferSize(4096);
//		AutoSync.acceptor.setHandler(new NetworkJoin());
		AutoSync.acceptor.addListener(new IoServiceListener(){

			@Override
			public void serviceActivated(IoService service) throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void serviceIdle(IoService service, IdleStatus idleStatus)
					throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void serviceDeactivated(IoService service) throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void sessionCreated(IoSession session) throws Exception {
				InetSocketAddress inetSockAddr = (InetSocketAddress)session.getRemoteAddress();
				String remoteNodeId = inetSockAddr.getAddress().getHostAddress() + ":" + inetSockAddr.getPort();
				LOGGER.info("Session created, new connection received. from ACCEPTOR side." + remoteNodeId);
				
				// A new remote node has been found.
				if(!AutoSync.neighborNodesStr.contains(remoteNodeId)){
					NeighborNode newRemoteNode = new NeighborNode();
					newRemoteNode.setSession(session);
					AutoSync.neighborNodes.add(newRemoteNode);
					AutoSync.neighborNodesStr.add(remoteNodeId);
				}
			}

			@Override
			public void sessionDestroyed(IoSession session) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
		});
		try {
			// Start listening on PORT for incomming connections.
			AutoSync.acceptor.bind(new InetSocketAddress(AutoSync.PORT));
		} catch (IOException e) {
			LOGGER.error("Fail to bind to local port: " + AutoSync.PORT);
		}
		
		
	}
	/**
	 * Connect current node to remote node specified by <i>addr</i>.
	 * @param addr The remote node to be connected.
	 * @return NeighborNode (session, connector) of remote node.
	 */
	public static NeighborNode connectNode(InetSocketAddress addr){
		IoSession session = null;
		IoConnector connector = null;
		
		connector = new NioSocketConnector();
		// Build filter chain.
		DefaultIoFilterChainBuilder chainBuilder = connector.getFilterChain();
		chainBuilder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		// Configure the session.
		IoSessionConfig config = connector.getSessionConfig();
		config.setMaxReadBufferSize(4096);
		config.setMinReadBufferSize(4096);
		// Set connector's handler
		connector.setHandler(new NetworkJoin());
		
		// Get session to remote node.
		ConnectFuture future = connector.connect(addr);
		future.awaitUninterruptibly();
		session = future.getSession(); 
		// Create a NeighborNode object to represent current connected remote node.
		NeighborNode neighborNode = new NeighborNode();
		neighborNode.setConnector(connector);
		neighborNode.setSession(session);
		return neighborNode;
	}
	
	/**
	 * Query a list of connected neighboring nodes of <i>session</i> node excluding current node.
	 * @param session
	 * @return
	 */
	public static void queryNeighbors(IoSession session){
		String message = String.valueOf(AutoSyncProtocol.QUERY_REQ_NEIGHBOR);
		session.write(message);
	}
	
	/**
	 * Remote node indicated by session from the node list of current node.
	 * @param session To indicate a remote node which is to be removed from current node list.
	 */
	private void removeNode(IoSession session){
		InetSocketAddress inetSocketAddr = (InetSocketAddress)session.getRemoteAddress();
		String remoteNodeId = inetSocketAddr.getAddress().getHostAddress() + ":" + inetSocketAddr.getPort();
		boolean delSucceed = false;
		delSucceed = AutoSync.neighborNodesStr.remove(remoteNodeId);
		if(!delSucceed){
			LOGGER.error("Delete " + remoteNodeId + " failed. No such node in neighborNodeStr.");
		}
		
		int len = AutoSync.neighborNodes.size();
		int i = 0;
		for(; i < len; i ++){
			if(AutoSync.neighborNodes.get(i).getSession() == session){
				break;
			}
		}
		if(i < len){
			AutoSync.neighborNodes.remove(i);
		}else{
			LOGGER.error("Delete " + remoteNodeId + " failed. No such node in neighborNodes.");
		}
		
		AutoSync.neighborNodes.remove(i);
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LOGGER.info("Connection to " + session.getRemoteAddress().toString() + " created.");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		LOGGER.info("Connection to " + session.getRemoteAddress().toString() + " opened.");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LOGGER.info("Connection to " + session.getRemoteAddress().toString() + " closed.");
		// Session closed. The node it specifies needs to be removed from node list.
		removeNode(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
//		LOGGER.info("Connection to " + session.getRemoteAddress().toString() + " idle.");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {

		LOGGER.info("Connection to " + session.getRemoteAddress().toString() + " encounterred exception.");
		// Exception occurs. Session needs to be removed from current node list.
		removeNode(session);
	}
	
	

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String msg = (String) message;
		String[] records = msg.split(";");
		int msgType = Integer.valueOf(records[0]);
		
		InetSocketAddress inetSockAddr = (InetSocketAddress)session.getRemoteAddress();
		String remoteNodeId = inetSockAddr.getAddress().getHostAddress() + ":" + inetSockAddr.getPort();
		
		switch(msgType){
			case AutoSyncProtocol.QUERY_RES_NEIGHBOR: 
				// Get a list of neighboring nodes of node specified by session.
				for(int i = 1; i < records.length; i ++){
					String remoteNode = records[i];
					// A new remote unconnected node has been found.
					if(!AutoSync.neighborNodesStr.contains(remoteNode) && !AutoSync.nodeId.equals(remoteNode)){
						// Connect to this new node.
						String[] addrInfo = remoteNode.split(":");
						NeighborNode newNeighborNode = connectNode(new InetSocketAddress(addrInfo[0], AutoSync.PORT));
						AutoSync.neighborNodes.add(newNeighborNode);
						AutoSync.neighborNodesStr.add(remoteNode);
					}
				}
				
				
				break;
			case AutoSyncProtocol.QUERY_REQ_NEIGHBOR:
				
				// Send back the ip:port of neighboring nodes of current node to node specified by session.
				Message newMsg = new Message();
				newMsg.setType(AutoSyncProtocol.QUERY_RES_NEIGHBOR);
				for(String nodeId: AutoSync.neighborNodesStr){
					// Exclude node specified by session.
					if(nodeId != remoteNodeId){
						newMsg.addRecord(nodeId);
					}
				}
				session.write(newMsg.toString());
				
				break;
			default:
				break;
		}
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		
	}

}
