import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MainClass {
	public static void merge(HashMap<String, HashMap<String, Integer>> central, HashMap<String, HashMap<String, Integer>> temp) {
		for (String docName : temp.keySet()) {
			if (central.containsKey(docName)) {
				;
			}
			else {
				central.put(docName, new HashMap<String, Integer>());
			}
			for (String key : temp.get(docName).keySet()) {
				if (central.get(docName).containsKey(key)) {
					central.get(docName).put(key, central.get(docName).get(key) + temp.get(docName).get(key));
				}
				else {
					central.get(docName).put(key, temp.get(docName).get(key));
				}
			}
		}
		
	}
	
	/*public static void addToWp(WorkPool wp1, WorkPool wp2) {
		Task t = wp2.getWork();
		if (t != null) {
			wp1.putWork(t);
		}
		while (t != null) {
			System.out.println(t.toString());
			t = wp2.getWork();
			wp1.putWork(t);
		}
	}*/
	
	public static void main(String args[]) throws InterruptedException, IOException {
		
		int threads = Integer.parseInt(args[0]);
		
		FileInputStream in = new FileInputStream(new File(args[1]));
			 
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		//Read the input file
		int D = Integer.parseInt(br.readLine());
		double X = Double.parseDouble(br.readLine());
		int ND = Integer.parseInt(br.readLine());

		//Declare documents
		ArrayList<String> documents = new ArrayList<>();
		for (int i = 0; i < ND; i++)
		{
			documents.add(br.readLine());
		}
		 
		br.close();
		
		
		
		//Map
		//Declare workpool and workers
		WorkPool mapWorkPool = new WorkPool(threads);
		WorkPool reduceWorkPool = new WorkPool(threads);
		WorkPool compareWorkPool = new WorkPool(threads);
		ArrayList<MapWorker> mapWorkers;
		mapWorkers = new ArrayList<>();
		for (int i = 0; i < threads; i++)
		{
			mapWorkers.add(new MapWorker(mapWorkPool, reduceWorkPool));  
        }            
        
		//Add tasks to workpool
		for (int i = 0; i < documents.size(); i++)
		{
			File f = new File(documents.get(i));
			long fileSize = f.length();
			//System.out.println("File size is " + fileSize);
			long cursor = 0;
			while (cursor < fileSize)
			{
				if (D < fileSize - cursor)
				{
					mapWorkPool.putWork(new MapTask(documents.get(i), cursor, D));
					cursor += D;
				}
				else
				{
					mapWorkPool.putWork(new MapTask(documents.get(i), cursor, fileSize - cursor));
					cursor = fileSize;
				}
			}
		}
		
		//Start map threads
		for (int i = 0; i < threads; i++)
		{
			mapWorkers.get(i).start();
		}
		for (int i = 0; i < threads; i++)
		{
			mapWorkers.get(i).join();
		}

		System.out.println("Am terminat map");
		for (int i = 0; i < threads; i++) {
			reduceWorkPool.add(mapWorkers.get(i).getWp());
		}		

		System.out.println("Am terminat map");
		
		//declare reduce threads
		ArrayList<ReduceWorker> reduceWorkers;
		reduceWorkers = new ArrayList<>();
		for (int i = 0; i < threads; i++)
		{
			reduceWorkers.add(new ReduceWorker(reduceWorkPool, compareWorkPool));  
        }  
		
		
		//reduce workpool is already populated
		for (int i = 0; i < threads; i++) {
			reduceWorkers.get(i).start();
		}
		for (int i = 0; i < threads; i++) {
			reduceWorkers.get(i).join();
		}
		//add all the data into a central Hashmap
		HashMap<String, HashMap<String, Integer>> centralHash = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, HashMap<String, Integer>> tempHash = new HashMap<String, HashMap<String, Integer>>();
		for (int i = 0; i < threads; i++) {
			tempHash = reduceWorkers.get(i).getHash();
			merge(centralHash, tempHash);
		}
		
		System.out.println("Am terminat reduce");
		
		//declare compare threads
		ArrayList<CompareWorker> compareWorkers;
		compareWorkers = new ArrayList<>();
		for (int i = 0; i < threads; i++)
		{
			compareWorkers.add(new CompareWorker(compareWorkPool));  
        }  
		
		//add to compareWorkPool
		for (int i = 0; i < documents.size(); i++) {
			for (int j = i+1; j < documents.size(); j++) {
				compareWorkPool.putWork(new CompareTask(documents.get(i), centralHash.get(documents.get(i)),
										documents.get(j), centralHash.get(documents.get(j)), X));
			}
		}
		
		for (int i = 0; i < threads; i++) {
			compareWorkers.get(i).start();
		}
		for (int i = 0; i < threads; i++) {
			compareWorkers.get(i).join();
		}
		ArrayList<CompareResult> results = new ArrayList<CompareResult>();
		for (int i = 0; i < threads; i++) {
			results.addAll(compareWorkers.get(i).getResults());
		}
		Collections.sort(results, new SimComparator());
		
		PrintWriter writer = new PrintWriter(args[2], "UTF-8");
		DecimalFormat d = new DecimalFormat("0.0000");
		for (int i = 0; i < results.size(); i++) {
			writer.println(results.get(i).doc1 + ";" + results.get(i).doc2 + ";" + d.format(results.get(i).X).replace('.', ','));
		}
		writer.close();
	}     	
}

class SimComparator implements Comparator<CompareResult>{
	public int compare(CompareResult r1, CompareResult r2) {
		if (r1.X > r2.X) {
			return -1;
		}
		else if (r1.X < r2.X) {
			return 1;
		}
		else {
			return 0;
		}
	}
}
