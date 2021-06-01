
public class leafNode {
	
	private Record record1;
	private Record record2;
	private Record record3;
	private Record record4;
	
	private leafNode prevNode;
	private leafNode nextNode;
	
	public leafNode(Record record1, Record record2, Record record3, Record record4) {
		this.record1 = record1;
		this.record2 = record2;
		this.record3 = record3;
		this.record4 = record4;
	}

	public Record getRecord1() {
		return record1;
	}

	public void setRecord1(Record record1) {
		this.record1 = record1;
	}

	public Record getRecord2() {
		return record2;
	}

	public void setRecord2(Record record2) {
		this.record2 = record2;
	}

	public Record getRecord3() {
		return record3;
	}

	public void setRecord3(Record record3) {
		this.record3 = record3;
	}

	public leafNode getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(leafNode prevNode) {
		this.prevNode = prevNode;
	}

	public leafNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(leafNode nextNode) {
		this.nextNode = nextNode;
	}

	public Record getRecord4() {
		return record4;
	}

	public void setRecord4(Record record4) {
		this.record4 = record4;
	}

}
