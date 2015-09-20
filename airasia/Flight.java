import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Flight implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7188262203171436773L;
	private String origin;
	private String destination;
	private Date departureDate;
	private Date arrivalDate;
	private String flightNumber;
	private BigDecimal lowFare;
	private BigDecimal premiumFlex;
	private boolean departStatus;
	private boolean returnStatus;
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Date getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}
	public Date getArrivalDate() {
		return arrivalDate;
	}
	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	public BigDecimal getLowFare() {
		return lowFare;
	}
	public void setLowFare(BigDecimal lowFare) {
		this.lowFare = lowFare;
	}
	public BigDecimal getPremiumFlex() {
		return premiumFlex;
	}
	public void setPremiumFlex(BigDecimal premiumFlex) {
		this.premiumFlex = premiumFlex;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public boolean isDepartStatus() {
		return departStatus;
	}
	public void setDepartStatus(boolean departStatus) {
		this.departStatus = departStatus;
	}
	public boolean isReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(boolean returnStatus) {
		this.returnStatus = returnStatus;
	}
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return "FlightNumber:"+getFlightNumber()+"|Time:"+sdf.format(getDepartureDate())+" to "+sdf.format(getArrivalDate())+"|Origin:"+getOrigin()+"|Destination:"+getDestination()+"|LowFare:"+getLowFare()+"|PremiumFlex:"+getPremiumFlex();
	}
	
}
