package airasia.common;
import java.util.Comparator;


public class FlightUtilPremiumFlex implements Comparator<Flight>{

	public int compare(Flight o1, Flight o2) {
		return o1.getPremiumFlex().compareTo(o2.getPremiumFlex());
	}
}
