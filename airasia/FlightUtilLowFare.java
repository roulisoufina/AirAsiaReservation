package airasia.common;
import java.util.Comparator;


public class FlightUtilLowFare implements Comparator<Flight>{

	public int compare(Flight o1, Flight o2) {
		return o1.getLowFare().compareTo(o2.getLowFare());
	}

}
