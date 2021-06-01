import java.util.Comparator;

public class sdtNameSorter implements Comparator<Record>{

	@Override
	public int compare(Record arg0, Record arg1) {
		// TODO Auto-generated method stub
		return arg0.getSdtName().compareTo(arg1.getSdtName());
	}
	

}
