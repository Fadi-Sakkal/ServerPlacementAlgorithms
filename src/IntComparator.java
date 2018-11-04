import java.util.Comparator;

public class IntComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer degree1, Integer degree2) {
		// TODO Auto-generated method stub
		if(degree1>degree2) {
			return 1;
		}
		else if(degree1<degree2) {
			return -1;
		}
		return 0;
	}

}
