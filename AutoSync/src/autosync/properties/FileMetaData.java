package autosync.properties;

/**
 * One FileMetaData object records meta data of one specific file(file name, file length and node id).
 * 
 * @author shichaodong
 * @version 1.0
 */
public class FileMetaData {
	private String fileName;
	// File length in bytes.
	private long fileLen;
	private String nodeId;
	
	public String getFileName() {return fileName;}
	public void setFileName(String filename){fileName = filename;}
	
	public long getFileLen(){return fileLen;}
	public void setFileLen(long filelen){fileLen = filelen;}
	
	public String getNodeId(){return nodeId;}
	public void setNodeId(String nodeid){nodeId = nodeid;}
}
