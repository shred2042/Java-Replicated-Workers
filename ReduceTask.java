import java.util.HashMap;


public class ReduceTask extends Task{
	private HashMap<String, Integer> docHash;
	private String documentName;
	
	public ReduceTask(String documentName, HashMap<String, Integer> docHash) {
		this.documentName = documentName;
		this.docHash = docHash;
	}
	
	public HashMap<String, Integer> getHash() {
		return docHash;
	}
	
	public String getDocumentName() {
		return documentName;
	}
}
