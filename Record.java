
public class Record {
	
	private String sdtName;
	private int pageNum;

	private int position;
	
	public Record(String sdtName,int pageNum,  int position) {
		this.sdtName = sdtName;
		this.position = position;
		this.pageNum = pageNum;
	}
	
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getSdtName() {
		return sdtName;
	}

	public void setSdtName(String sdtName) {
		this.sdtName = sdtName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
