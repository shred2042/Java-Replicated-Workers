import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MapWorker extends Thread {
	WorkPool wp, reduceWp;
	//private HashMap<String, HashMap<String, Integer> > docHash;

	public MapWorker(WorkPool workpool, WorkPool reduceWp) {
		this.wp = workpool;
		this.reduceWp = reduceWp;
	}        

	/**
	 * Procesarea unei solutii partiale. Aceasta poate implica generarea unor
	 * noi solutii partiale care se adauga in workpool folosind putWork().
	 * @throws IOException 
	 */
	void processPartialSolution(MapTask task) throws IOException {  
		//get string from file
		HashMap<String, Integer> docHash = new HashMap<String, Integer>();
		RandomAccessFile f = new RandomAccessFile(task.getDocumentName(), "r");
		boolean end_of_file = false;
		boolean begin_of_file = false;
		if (task.getOffset() == 0) {
			begin_of_file = true;
		}
		else {
			f.seek(task.getOffset() - 1);
		}
		int dim = (int)task.getDim();
		String add;
		byte[] b = new byte[dim + 30];
		try {
			f.readFully(b);
		}
		catch (EOFException e) {
			if (begin_of_file == false) {
				f.read(b, 0, dim + 1);				
			}
			else {
				f.read(b, 0, dim);
			}
			end_of_file = true;
			long len = f.length();
			f.seek(task.getOffset() - 1);
			b = new byte[(int)(len - task.getOffset())];
			f.readFully(b);
		}
		String s = new String(b);
		//System.out.println(s);
		//if the task begins in the middle of a word, ignore it
		if (task.getOffset() != 0)
		{
			boolean flag = false;
			for (int i = 0; i < dim; i++) {
				char c = s.charAt(i);
				if (c == '_' || c == ';' || c == ':' || c == '?' || c == '~' ||
						c == '\\' || c == '.' || c == ',' || c == '>' || c == '<' || 
						c == '~' || c == '`' || c == '[' || c == ']' || c == '{' ||
						c == '}' || c == '(' || c == ')' || c == '!' || c == '@' ||
						c == '#' || c == '$' || c == '%' || c == '^' || c == '&' || 
						c == '-' || c == '+' || c == '\'' || c == '*' || c == '|' ||
						c == '\"' || c == '\t' || c == '\n' || c == ' ' ) {
					s = s.substring(i + 1, s.length());
					dim = dim - i;
					flag = true;
					break;
				}
			}
			if (flag == false) {
				f.close();
				return;
			}
		}
		//if the task ends in the middle of a word, add the entire word to the task
		if (end_of_file == false || end_of_file == true)
		{
			for (int i = 0; i < s.length() - dim; i++)
			{
				//find out where the word ends; modify the task dimension accordingly
				char c = s.charAt(dim + i - 1);
				if (c == '_' || c == ';' || c == ':' || c == '?' || c == '~' ||
						c == '\\' || c == '.' || c == ',' || c == '>' || c == '<' || 
						c == '~' || c == '`' || c == '[' || c == ']' || c == '{' ||
						c == '}' || c == '(' || c == ')' || c == '!' || c == '@' ||
						c == '#' || c == '$' || c == '%' || c == '^' || c == '&' || 
						c == '-' || c == '+' || c == '\'' || c == '*' || c == '|' ||
						c == '\"' || c == '\t' || c == '\n' || c == ' ' )
				{
					task.setDim(dim + i);
					dim = dim + i;
					s = s.substring(0, dim);
					//System.out.println(s);
					break;
				}
			}
		}
		//System.out.println(s);
		
		//add the words to the hashmap

		//System.out.println(s);
		StringTokenizer st = new StringTokenizer(s, "_;:/?~\\.,><~`[]{}()!@#$%^&-+'=*|\"\t\n ", false);
		while (st.hasMoreTokens()) {
			add = st.nextToken().toLowerCase();
			if (docHash.containsKey(add))
			{
				docHash.put(add, docHash.get(add) + 1);
			}
			else
			{
				docHash.put(add, 1);
			}
			//System.out.println("Am adaugat " + add + ".");
		}		
		reduceWp.putWork(new ReduceTask(task.getDocumentName(), docHash));
		
		f.close();		
	}
	
	public WorkPool getWp(){
		return reduceWp;
	}
	
    @Override
	public void run() {
		//System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
		MapTask task;
		while (true) {			
			task = (MapTask)wp.getWork();
			if (task == null){				
				break;
            }
			try {
				processPartialSolution(task);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Add the partial solutions to the Reduce workpool
		//reduceWp.putWork(new ReduceTask(docHash));
	}	
}