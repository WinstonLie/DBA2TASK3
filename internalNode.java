
public class internalNode {
	
	private boolean isRoot;
	private String key;
	private leafNode pointedLeafNode;
	
	public internalNode(String key) {
		
		this.key = key;
		
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public leafNode getPointedLeafNode() {
		return pointedLeafNode;
	}

	public void setPointedLeafNode(leafNode pointedLeafNode) {
		this.pointedLeafNode = pointedLeafNode;
	}
	

}
