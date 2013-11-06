// Imports
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;


// 	Class: Oblig5
// =================================================================================
class Oblig5 {
    // Main
    public static void main(String[] args) {
		// Header
		System.out.println("============================================");
		System.out.println(" Reiseplanlegger");	
		System.out.println("============================================\n");
		
		// Initiate planner
        Planner pl = new Planner();
    }
}


// 	Class: Planner
// =================================================================================
class Planner {
    // Variables
    private final String DATAFILE = "trikkogtbaneutf8.txt";
	
	//private ArrayList<Station> stations;
	private HashMap<String, Station> stations = new HashMap<String, Station>();
    
    // Constructor
    Planner() {
        // Parse data file
        this.parseData();
        
        // Get user origin/destination
        //this.queryFromTo();
    }
    
    // Parse data file
    private void parseData() {
        try {
            BufferedReader data = new BufferedReader(new FileReader(DATAFILE));
            
			String line;
			int lineNumber = 0;
			int stationNumber = 0;
			
            while ((line = data.readLine()) != null) {
				// Line
                if (line.toLowerCase().contains("*linje*")) {
					lineNumber = Integer.parseInt(line.toLowerCase().split("\\*linje\\* ")[1]);
					stationNumber = 0;
					System.out.println(line + " -- " + lineNumber);
					
					// Add line
				} 
				// Station
				else {
					stationNumber++;
					System.out.print("S" + stationNumber + " -- " + "L" + lineNumber + " -- " + line);
					
					// Add station
					Station station = new Station();
					station.setName(line);
					
					if (this.isStation(line)) {
						// Add new line
						System.out.println(" --- EXISTING");
					} else {
						// Add new station
						stations.put(station.getName(), station);
					}
				}				
            }
            
            data.close();

        } catch(IOException e) {
            // Exit
            System.out.println("[FEIL] Kunne ikke lese datafilen (" + DATAFILE + ")!");
            System.exit(1);
        }
    }
    
    // Query 
    private void queryFromTo() {
		// User input
        Scanner input = new Scanner(System.in);
        
        System.out.println(" Hvor skal vi reise hen?");
        System.out.print("  > Fra: ");
        
        String origin = input.nextLine().trim();
        
        System.out.print("  > Til: ");
        
        String destination = input.nextLine().trim();
		
		// Find routes
		// TODO: VALIDATE STATION NAMES!
		//this.findRoutes(
    }
    
    // Find routes
    private void findRoutes(Station from, Station to) {
		// Header
		System.out.println(" Aktuelle reiseruter");
		System.out.println("--------------------------------------------\n");
		
		// 
        // Skriv metoden "void beregnRuter( )" som ut fra L1 og L2 finner, og får skrevet ut enten 
        // én reisevei (punkt 3), eller en rekke reiseveier (punkt 4) fra xxx til yyy. Alle reiseveier som 
        // finnes skrives ut som vist i punkt 3 eller punkt 4. f) 
    }
	
	
	// Return stations
	private HashMap<String, Station> getStations() {
		return this.stations;
	}
	
	// Check if existing station (by name)
	private boolean isStation(String n) {
		if (this.getStations().get(n) == null)
			return false;
		
		return true;
	}
	
	/*
	
	// Return station (by name)
	private Station getStationByName(String n) {
		for (Station s : this.getStations())
            if(s.getName().equalsIgnoreCase(n))
                return s;

        return null;
	}
	*/
}


// 	Class: Line
// =================================================================================
class Line {
	/*	Variables
	------------------------------------ */
	private int number;
    private String type;
	
	private ArrayList<Station> stations;
    
	
	/*	Methods
	------------------------------------ */
	
	// Add station to line
	void newStation(Station s) {
		stations.add(s);
	}
	
	// Check if line goes through station
    boolean hasStation(Station s) {
        // som returnerer sann hvis stasjonen st befinner seg på den linja som Linje-objektet representerer.
		return true;
    }
	
	// 
	String direction(Station from, Station to) {
		return "";
	}
	
	// Getters and setters
	void setNumber(int n) {
		this.number = n;
	}
	int getNumber() {
		return this.number;
	}
	String getType() {
		return ((this.getNumber() < 10) ? "t-bane" : "trikk");
	}
}


// 	Class: Station
// =================================================================================
class Station {
	/*	Variables
	------------------------------------ */
	private String id;
	private String name;
	
	private ArrayList<Line> lines;
	
	
	/*	Methods
	------------------------------------ */
	
	// Add line to station
	void newLine(Line l, int stationNumber) {
		
	}
	
	
	public boolean hasLine(Line l) {
		return true;
	}
	
	
	// Getters and setters
	public void setName(String n) {
		this.name = n;
	}
	public String getName() {
		return this.name;
	}
}


// 	Class: Transfer
// =================================================================================
class Transfer {
    
}
