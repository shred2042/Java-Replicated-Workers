import java.util.HashMap;


public class ReduceWorker extends Thread{
	WorkPool reduceWp, compareWp;
	private HashMap<String, HashMap<String, Integer> > docHash;
	
	public ReduceWorker(WorkPool workpool, WorkPool compareWp) {
		this.reduceWp = workpool;
		this.compareWp = compareWp;
		docHash = new HashMap<String, HashMap<String, Integer>>();
	}  
	
	void processPartialSolution(ReduceTask task) {
		String docName = task.getDocumentName();
		if (docHash.containsKey(docName)) {
			;
		}
		else {
			docHash.put(docName, new HashMap<String, Integer>());
		}
		for (String key : task.getHash().keySet()) {
			if (docHash.get(docName).containsKey(key)) {
				docHash.get(docName).put(key, docHash.get(docName).get(key) + task.getHash().get(key));
			}
			else {
				docHash.get(docName).put(key, task.getHash().get(key));
			}
		}
	}
	
	public HashMap<String, HashMap<String, Integer> > getHash()
	{
		return docHash;
	}
	
	 @Override
	public void run() {
		//System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
		ReduceTask task;
		while (true) {
			task = (ReduceTask)reduceWp.getWork();
			if (task == null){				
				break;
	        }
			processPartialSolution(task);
		}
	}	
}
