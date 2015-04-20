import java.util.ArrayList;
import java.util.HashMap;



public class CompareWorker extends Thread {
	WorkPool wp;
	ArrayList<CompareResult> result;
	
	public CompareWorker(WorkPool wp) {
		this.wp = wp;
		result = new ArrayList<CompareResult>();
	}
	
	public void processPartialSolution(CompareTask task) {
		double sim = 0;
		double h1_words_nr = 0, h2_words_nr = 0;
		HashMap<String, Integer> h1 = task.getH1();
		HashMap<String, Integer> h2 = task.getH2();
		for (String key : h1.keySet()) {
			h1_words_nr = h1_words_nr + h1.get(key);
		}
		for (String key : h2.keySet()) {
			h2_words_nr = h2_words_nr + h2.get(key);
		}
		System.out.println(h1_words_nr + " " + h2_words_nr);
		for (String key : h1.keySet()) {
			if (h2.containsKey(key)) {
				sim = sim + ((double)h1.get(key)/h1_words_nr) * ((double)h2.get(key)/h2_words_nr);
			}
		}
		if (sim * 100 > task.getX()) {
			result.add(new CompareResult(task.getDoc1(), task.getDoc2(), sim * 100));
		}
	}
	
	public ArrayList<CompareResult> getResults() {
		return result;
	}
	
	@Override
	public void run() {
		//System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
		CompareTask task;
		while (true) {
			task = (CompareTask)wp.getWork();
			if (task == null){				
				break;
	        }
			processPartialSolution(task);
		}
	}	
}
