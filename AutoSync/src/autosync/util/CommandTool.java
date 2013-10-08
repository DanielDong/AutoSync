package autosync.util;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autosync.core.AutoSyncProtocol;
import autosync.properties.Message;

/**
 * Utility to print out neighboring connected nodes of current node.
 * @author shichaodong
 * @version 1.0
 */
public class CommandTool extends IoHandlerAdapter{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandTool.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 2){
			LOGGER.error("Both remote node's IP and PORT need to be provided.");
			System.out.println("=================Usage Instruction=================\n" +
					           "> java CommandTool IP PORT.");
		}else{
			IoConnector connector = new NioSocketConnector();
			DefaultIoFilterChainBuilder chainBuilder = connector.getFilterChain();
			chainBuilder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
			
			IoSessionConfig config = connector.getSessionConfig();
			config.setMaxReadBufferSize(4096);
			config.setMinReadBufferSize(4096);
			
			String remoteNodeIp = args[0];
			String port = args[1];
			ConnectFuture future = connector.connect(new InetSocketAddress(remoteNodeIp, Integer.valueOf(port)));
			IoSession session = null;
			// 3 attempts to connect to the remote node.
			boolean isSuccessful = true;
			int cnt = 3;
			do{
				isSuccessful = future.awaitUninterruptibly(3000);
				cnt --;
			}while(!isSuccessful && cnt > 0);
			
			// Connection fails.
			if(!isSuccessful){
				LOGGER.error("Cannot connect to " + remoteNodeIp + ":" + port);
				System.out.println("Cannot connect to " + remoteNodeIp + ":" + port);
			}
			// Connection succeeds.
			else{
				System.out.println("=================Usage Instruction=================\n" + 
						           "> 1 for Query neighboring nodes.\n" + 
						           "> q for QUIT");
				// Get connection channel to the remote node.
				session = future.getSession();
				Scanner s = new Scanner(System.in);
				String input = s.nextLine();
				
				Message msg = null;
				while(!input.equalsIgnoreCase("q")){
					// Current command to be executed.
					int cmd = Integer.valueOf(input);
					switch(cmd){
					// Send query request to remote node
					case AutoSyncProtocol.CL_QUERY_REQ_NEIGHBOR:
						msg = new Message();
						msg.setType(AutoSyncProtocol.CL_QUERY_REQ_NEIGHBOR);
						session.write(msg.toString());
						break;
					default:
						break;
					}
					input = s.nextLine();
				}
			}
			
		}
		
		
	}
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// New message from remote (local) querying node.
		Message msg = (Message) message;
		String[] msgArr = msg.toString().split(";");
		int cmd = Integer.valueOf(msgArr[0]);
		switch(cmd){
			case AutoSyncProtocol.CL_QUERY_RES_NEIGHBOR:
				int len = msgArr.length;
				System.out.println("=================Neighboring Nodes=================\n" + 
						           "In total " + (len - 1));
				
				for(int i = 1; i < len; i ++){
					System.out.println(msgArr[i]);
				}
				break;
			default:
				break;
		}
		
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		
	}

}
