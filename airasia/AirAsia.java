

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AirAsia {

public static void main(String[] args) {
		
		int key = 0;
		do{
			Scanner scan = new Scanner(System.in);
			System.out.println("\nWelcome to AirAsia airline!");	
			System.out.println("==============================   COUNTRY CODE  ===================================");
			System.out.println("Jakarta(CGK)| Bali(DPS)| Bandung(BDO)| Lombok(LOP)  | Medan(KNO)| Surabaya(SUB)| Yogyakarta(JOG)");
			System.out.println("Kualalumpur(KUL)| Singapore(SIN)| Bangkok(DMK)| Hongkong(HKG)| Hanoi(HAN)| Yangon(RGN)");
			System.out.println("or other country code, please take a look at airasia.com");
			System.out.println("==============================   COUNTRY CODE  ===================================\n");
			
			System.out.println("Origin:");
			String origin = scan.next().toUpperCase().trim();
			System.out.println("Destination:");
			String destination = scan.next().toUpperCase().trim();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date todayDate = new Date();
			String todayString = sdf.format(todayDate);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println("Depart Date(dd-mm-yyyy):");
			String departDate = null;
			Date inputDepartDate = new Date();
			try {
				inputDepartDate = sdf.parse(scan.next());
				while(inputDepartDate.before(sdf.parse(todayString))){
					System.err.println("invalid depart date");
					System.out.println("Depart Date(dd-mm-yyyy):");
					inputDepartDate = sdf.parse(scan.next());
				}
				departDate = sdf2.format(inputDepartDate);
				
			} catch (ParseException e) {
				System.err.println("ParseException inputDepartDate");
				e.printStackTrace();
			}
			
			System.out.println("Return Date?(y/n)");
			String keyReturn=scan.next().toLowerCase();
			while(!keyReturn.equalsIgnoreCase("y") && !keyReturn.equalsIgnoreCase("n")){
				System.err.println("invalid answer");
				System.out.println("Return Date?(y/n)");
				keyReturn=scan.next().toLowerCase();
			}
			String returnDate=null;
			if(keyReturn.equalsIgnoreCase("y")){
				System.out.println("Return Date:");
				try {
					Date inputReturnDate = sdf.parse(scan.next());
					while(inputReturnDate.before(inputDepartDate)){
						System.err.println("Return Date should be after Depart Date. Please input valid return date");
						System.out.println("Return Date:");
						inputReturnDate = sdf.parse(scan.next());
					}
					returnDate = sdf2.format(inputReturnDate);
				} catch (ParseException e) {
					System.err.println("ParseException inputReturnDate");
					e.printStackTrace();
				}
			}
			
			System.out.println("1. Get Flight Information");
			System.out.println("2. Get the cheapest Fare");
			System.out.println("3. Exit");
			System.out.println("input your option(1/2/3):");
			key=scan.nextInt();
			
			//loop while key is invalid
			while(key!=1 && key!=2 && key!=3){
				System.err.println("invalid option");
				System.out.println("1. Get Flight Information");
				System.out.println("2. Get the cheapest Fare");
				System.out.println("3. Exit");
				System.out.println("input(1/2):");
				key=scan.nextInt();
			}
			
			switch (key) {
			case 1:
				System.out.println("Loading... ");
				try {
					List<Flight> flights = searchFlight(origin,destination,departDate,returnDate);
					if(null!=flights && flights.size()>0)
						readFile();
					
				} catch(IndexOutOfBoundsException io){
					System.err.println("Flight not available");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			case 2:
				System.out.println("Loading... ");
				try {
					List<Flight> flights = searchFlight(origin,destination,departDate,returnDate);	
					if(null!=flights && flights.size()>0)
						getCheapestLowFare(flights);
				} catch(IndexOutOfBoundsException io){
					System.err.println("Flight not available");
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
				break;
			case 3 : System.out.println("See you again");
					 break;
			default:
				break;
			}
		
		}while(key==1||key==2);
		
	}
	
	/*
	 * Method searchFlight to get Flight Information according search criteria
	 * 1) with two options way: One Way or Return
	 * 2) with two type of fare details: Low Fare or Premium Flex
	 * 
	 * Necessary information to be extracted:
	 * 1) Depart and arrive information (from/to, when, flight number, and other information) of flights options
	 * 2) Fare value (try to get the cheapest fare).
	 * */
	public static List<Flight> searchFlight(String origin,String destination,String departureDate,String arrivalDate) throws IndexOutOfBoundsException, Exception{
		
		List<Flight> flightsFinal = null;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		
/* 
 * url return	
 * https://booking.airasia.com/Flight/InternalSelect?o1=CGK&d1=DPS&dd1=2015-09-20&dd2=2015-09-25&r=true&ADT=1&CHD=0&inl=0&s=true&mon=true&culture=en-GB&cc=IDR
 * url one way	
 * https://booking.airasia.com/Flight/InternalSelect?o1=CGK&d1=DPS&dd1=2015-09-20&ADT=1&CHD=0&inl=0&s=true&mon=true&culture=id-ID&cc=IDR 
 * */
			
			
			//case one way, this is document without set return date
			Document doc1 = Jsoup.connect("https://booking.airasia.com/Flight/InternalSelect?o1="+origin+"&d1="+destination+"&dd1="+departureDate+"&ADT=1&CHD=0&inl=0&s=true&mon=true&culture=id-ID&cc=IDR")
							.userAgent("Chrome").get();
			Document doc2 = null;
			Document docFinal = doc1;
			
			
			if(null!=arrivalDate && arrivalDate.length()>0){
				//case return, this is document with set departure date and return date
				doc2 = Jsoup.connect("https://booking.airasia.com/Flight/InternalSelect?o1="+origin+"&d1="+destination+"&dd1="+departureDate+"&dd2="+arrivalDate+"&r=true&ADT=1&CHD=0&inl=0&s=true&mon=true&culture=en-GB&cc=IDR")
						.get();
				docFinal = doc2;
			}
			
			Element table1 = doc1.select("table[class=table avail-table]").get(0);	
			if(null!=table1){
				
				//case return
				List<String> flights = null;
				if(null!=arrivalDate && arrivalDate.length()>0){
					flights = new ArrayList<String>();
					Elements item = doc1.select("div[class=carrier-hover-bold]");
					for(int i=0;i<item.size();i++){
						if(i%2==0){
							flights.add(item.get(i).text());
						}
					}	
				}
				
//				System.out.println("===== FLIGHT NUMBER =====");
				List<String> listFlightNumb = new ArrayList<String>();
				Elements flight = docFinal.select("div[class=carrier-hover-bold]");
				for(int i=0;i<flight.size();i++){
					if(i%2==0){
						listFlightNumb.add(flight.get(i).text());
					}
				}
			
//				System.out.println("===== TIME =====");
				List<String> listDepartTime = new ArrayList<String>();
				List<String> listArriveTime = new ArrayList<String>();
				Elements time = docFinal.select("div[class=avail-table-bold]");
				for(int i=0;i<time.size();i++){
					if(i%2==0){
						listDepartTime.add(time.get(i).text());
					}else{
						listArriveTime.add(time.get(i).text());
					}
				}
				
				
//				System.out.println("===== FARE =====");
				List<BigDecimal> listLowFare = new ArrayList<BigDecimal>();
				List<BigDecimal> listPremiumFlex = new ArrayList<BigDecimal>();
				Elements price = docFinal.select("div[class=avail-fare-price]");
				for(int i=0;i<price.size();i++){
					String[] itemSplit = price.get(i).text().split(" ");
						
					try {
						double dobPrice = 0;
						if(itemSplit[0].contains(","))
							dobPrice= Double.valueOf(itemSplit[0].replace(",", ""));
						else if(itemSplit[0].contains("."))
							dobPrice= Double.valueOf(itemSplit[0].replace(".", ""));
						BigDecimal fare = new BigDecimal(dobPrice);
						
						if(i%2==0){
							listLowFare.add(fare);
						}else{
							listPremiumFlex.add(fare);
						}
					}catch(NumberFormatException ne){
						System.err.println("NumberFormatException:"+ne.getMessage());
					}catch(Exception e){
						System.out.println("Exception:"+e.getMessage());
					}
					
				}
				
				
				List<Flight> listFlight = convertToObject(listDepartTime, listArriveTime, listFlightNumb, listLowFare, listPremiumFlex, departureDate, arrivalDate, origin, destination);
				List<Flight> flightsDepart = new ArrayList<Flight>();
				List<Flight> flightReturn = new ArrayList<Flight>();
				
				if(null!=listFlight & listFlight.size()>0){
					
					//try put into a file
					fos = new FileOutputStream("F://TestFile//flight.txt");
					oos = new ObjectOutputStream(fos);
					
					//case return
					if(null!=arrivalDate && arrivalDate.length()>0){
						for(int i=0;i<flights.size();i++){
							listFlight.get(i).setDepartStatus(true);
							flightsDepart.add(listFlight.get(i));
						}
						if(listFlightNumb.size()>flights.size()){
							for(int j=flights.size();j<listFlightNumb.size();j++){
								listFlight.get(j).setOrigin(destination);
								listFlight.get(j).setDestination(origin);
								listFlight.get(j).setReturnStatus(true);
								flightReturn.add(listFlight.get(j));
							}
						}
						
					}else{
						for(Flight obj:listFlight){
							obj.setDepartStatus(true);
						}
					}
					// write to file
					oos.writeObject(listFlight);
					oos.flush();
					
					flightsFinal = listFlight;
				}
			}
			else{
				System.out.println("Flight not available");
			}
			
		//close io	
		if(oos!=null){
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return flightsFinal;
		
	}
	
	/*
	 * method convertToObject 
	 * convert to Flight object
	 * */
	public static List<Flight> convertToObject(List<String> listDepartTime,
			List<String> listArriveTime, List<String> listFlightNumb,
			List<BigDecimal> listLowFare, List<BigDecimal> listPremiumFlex,
			String departureDate, String arrivalDate,
			String origin, String destination){
	
		List<Flight> listFlight = new ArrayList<Flight>();
		
		for(int i=0;i<listFlightNumb.size();i++){
			Flight flight = new Flight();
			flight.setOrigin(origin);
			flight.setDestination(destination);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date deptDate = sdf.parse(departureDate+" "+listDepartTime.get(i));
				flight.setDepartureDate(deptDate);
				
				Date arrDate = null;
				if(null!=arrivalDate && arrivalDate.length()>0)
					arrDate = sdf.parse(arrivalDate+" "+listArriveTime.get(i));
				else
					arrDate = sdf.parse(departureDate+" "+listArriveTime.get(i));
				flight.setArrivalDate(arrDate);
				
			} catch (ParseException e) {
				System.err.println("ParseException");
				e.printStackTrace();
			}
			flight.setFlightNumber(listFlightNumb.get(i));
			flight.setLowFare(listLowFare.get(i));
			flight.setPremiumFlex(listPremiumFlex.get(i));
			
			listFlight.add(flight);
		}
		
		return listFlight;
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public static void readFile(){
		try {
			FileInputStream fis = new FileInputStream("F://TestFile//flight.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Flight> flights = (ArrayList<Flight>)ois.readObject();
			
			if(null!=flights && flights.size()>0){
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
				
				System.out.println("\nDepart("+flights.get(0).getOrigin()+"=>"+flights.get(0).getDestination()+") "+sdf.format(flights.get(0).getDepartureDate()));
				boolean returnExist=false;
				for(Flight obj:flights){
					if(obj.isDepartStatus())
						System.out.println(obj);
					else if(obj.isReturnStatus())
						returnExist=true;
				}
				
				if(returnExist){
					System.out.println("\nReturn("+flights.get(0).getDestination()+"=>"+flights.get(0).getOrigin()+") "+sdf.format(flights.get(0).getArrivalDate()));
					for(Flight obj:flights){
						if(obj.isReturnStatus())
							System.out.println(obj);
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException");
			e.printStackTrace();
		}
		
	}
	
	/*
	 * method getCheapestLowFare
	 * display the cheapest fare of flight following low fare and premium flex
	 * 
	 * */
	public static void getCheapestLowFare(List<Flight> flights){
		//Departure Flight
		System.out.println("Departure Flight");
		List<Flight> flightDeparts = new ArrayList<Flight>();
		boolean returnExist=false;
		for(Flight obj:flights){
			if(obj.isDepartStatus()){
				flightDeparts.add(obj);
			}else if(obj.isReturnStatus())
				returnExist=true;
		}
		Collections.sort(flightDeparts, new FlightUtilLowFare());
		System.out.println("Flight with the cheapest low fare: ");
		System.out.println(flightDeparts.get(0));
		if(flightDeparts.size()>1){
			for(int i=1;i<flightDeparts.size();i++){
				Flight obj = (Flight)flightDeparts.get(i);
				if(obj.getLowFare().compareTo(flightDeparts.get(0).getLowFare())==0)
					System.out.println(obj);
			}
		}
		Collections.sort(flightDeparts, new FlightUtilPremiumFlex());
		System.out.println("Flight with the cheapest premium flex: ");
		System.out.println(flightDeparts.get(0));
		if(flightDeparts.size()>1){
			for(int i=1;i<flightDeparts.size();i++){
				Flight obj = (Flight)flightDeparts.get(i);
				if(obj.getPremiumFlex().compareTo(flightDeparts.get(0).getPremiumFlex())==0)
					System.out.println(obj);
			}
		}
		
		//Return Flight
		if(returnExist){
			System.out.println("\nReturn Flight");
			List<Flight> flightReturns = new ArrayList<Flight>();
			for(Flight obj:flights){
				if(obj.isReturnStatus()){
					flightReturns.add(obj);
				}
			}
			Collections.sort(flightReturns, new FlightUtilLowFare());
			System.out.println("Flight with the cheapest low fare :");
			System.out.println(flightReturns.get(0));
			if(flightReturns.size()>1){
				for(int i=1;i<flightReturns.size();i++){
					Flight obj = (Flight)flightReturns.get(i);
					if(obj.getLowFare().compareTo(flightReturns.get(0).getLowFare())==0)
						System.out.println(obj);
				}
			}
			
			Collections.sort(flightReturns, new FlightUtilPremiumFlex());
			System.out.println("\nFlight with the cheapest premium flex :");
			System.out.println(flightReturns.get(0));
			if(flightReturns.size()>1){
				for(int i=1;i<flightReturns.size();i++){
					Flight obj = (Flight)flightReturns.get(i);
					if(obj.getPremiumFlex().compareTo(flightReturns.get(0).getPremiumFlex())==0)
						System.out.println(obj);
				}
			}
		}
	}

}
