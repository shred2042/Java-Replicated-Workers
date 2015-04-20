
public class MapTask extends Task{
	private String documentName;
	private long offset, dim;
	
	public MapTask(String documentName, long offset, long dim)
	{
		this.documentName = documentName;
		this.offset = offset;
		this.dim = dim;
	}

	public String getDocumentName() {
		return documentName;
	}

	public long getDim() {
		return dim;
	}

	public void setDim(long dim) {
		this.dim = dim;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
		
}
