package autosync.util;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autosync.core.AutoSync;
import autosync.core.NetworkJoin;
import autosync.properties.NeighborNode;

/**
 * This TimerTask querys current node' neighboring connected node for their neighboring nodes.
 * @author shichaodong
 * @version 1.0
 */
public class QueryNodeTimerTask extends TimerTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryNodeTimerTask.class);
	@Override
	public void run() {
		// Query each neighboring connected node for their neighboring connected nodes and connect to new 
		// nodes.
		LOGGER.info("=================Querying neighbor nodes=================\n" + 
				           "Neighbor size: " + AutoSync.neighborNodes.size());
		for(int i = 0; i < AutoSync.neighborNodes.size(); i ++){
			NeighborNode tmpNode = AutoSync.neighborNodes.get(i);
			NetworkJoin.queryNeighbors(tmpNode.getSession());
		}	
	}

}
