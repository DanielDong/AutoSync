package autosync.core;

public class AutoSyncProtocol {
	// To test availability of one connected neighboring node periodically.
	public static final int HEART_BEAT_REQ = 11;
	public static final int HEART_BEAT_RES = 12;
	// To query a list of file meta data from a connected neighboring node.
	public static final int QUERY_REQ_NEIGHBOR      = 21;
	public static final int QUERY_RES_NEIGHBOR      = 22;
	// To join to the peer to peer network via a node.
	public static final int JOIN_REQ       = 31;
	public static final int JOIN_RES       = 32;
	public static final int CL_QUERY_REQ_NEIGHBOR = 41;
	public static final int CL_QUERY_RES_NEIGHBOR = 42;
	
}
