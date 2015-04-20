import java.util.HashMap;


public class CompareTask extends Task{
	private String doc1, doc2;
	private HashMap<String, Integer> h1, h2;
	private double X;
	
	public CompareTask(String doc1, HashMap<String, Integer> h1,  String doc2, HashMap<String, Integer> h2, double X) {
		this.setDoc1(doc1);
		this.setDoc2(doc2);
		this.setH1(h1);
		this.setH2(h2);
		this.setX(X);
	}

	public HashMap<String, Integer> getH2() {
		return h2;
	}

	public void setH2(HashMap<String, Integer> h2) {
		this.h2 = h2;
	}

	public HashMap<String, Integer> getH1() {
		return h1;
	}

	public void setH1(HashMap<String, Integer> h1) {
		this.h1 = h1;
	}

	public String getDoc1() {
		return doc1;
	}

	public void setDoc1(String doc1) {
		this.doc1 = doc1;
	}

	public String getDoc2() {
		return doc2;
	}

	public void setDoc2(String doc2) {
		this.doc2 = doc2;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}
}
