// Imports
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;


// 	Class: Oblig5
// =================================================================================
class Oblig5 {
    // Main
    public static void main(String[] args) {
        final String RD = "\u001B[31m";
        final String GN = "\u001B[32m";
        final String YL = "\u001B[33m";
        final String RT = "\u001B[0m";
        
		// Header
		System.out.println("\n================================================================================");
		System.out.println(" "+RD+"Reiseplanlegger"+RT);
		System.out.println("================================================================================\n");
		
		// Initiate planner
        Planner pl = new Planner();
        
        // Footer (spacer)
        System.out.println();
    }
}


// 	Class: Planner
// =================================================================================
class Planner {
    // Variables
    private final String DATAFILE = "trikkogtbaneutf8-ver2.txt";
    //private final String DATAFILE = "testdata.txt";
	
    private HashMap<Integer, Line> lines = new HashMap<Integer, Line>();
	private HashMap<String, Station> stations = new HashMap<String, Station>();
    
    // Constructor
    Planner() {
        // Parse data file
        this.parseData();
        
        // Get user origin/destination
        this.queryFromTo();
    }
    
    // Parse data file
    private void parseData() {
        try {
            BufferedReader data = new BufferedReader(new FileReader(DATAFILE));
            
			String dataline;
			int lineNumber = 0;
			int stationNumber = 0;
			
            while ((dataline = data.readLine()) != null) {
                // Line
                if (dataline.toLowerCase().contains("*linje*")) {
					lineNumber = Integer.parseInt(dataline.toLowerCase().split("\\*linje\\* ")[1]);
					stationNumber = 0;
					
                    // Add new line
                    Line line = new Line();
					line.setNumber(lineNumber);
                    lines.put(line.getNumber(), line);
                    
                    // DEBUG
                    //System.out.println("\n" + dataline + " -- " + lineNumber);
				} 
				// Station
				else {
					stationNumber++;
                    
                    Station station;
                    
					if (this.isStation(dataline)) {
						// Get existing station
                        station = this.getStationByName(dataline);
                        
                        // DEBUG
						//System.out.print("EXT\t");
					} else {
						// Add new station
                        station = new Station();
                        station.setName(dataline);
						stations.put(station.getName().toLowerCase(), station);
                        
                        // DEBUG
                        //System.out.print("NEW\t");
					}
                    
                    // Add line to station
                    station.addLine(this.getLineByNumber(lineNumber));
                    
                    // Add station to line
                    this.getLineByNumber(lineNumber).addStation(station);
                    
                    // DEBUG
                    //System.out.println("S" + stationNumber + " -- " + "L" + this.getLineByNumber(lineNumber).getNumber() + " -- " + station.getLines().size() + " --- " + station.getName());
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
        System.out.println(" Hvor skal vi reise hen?");
        
        Station origin = this.getStationByName(this.getAndValidateInput("Fra"));
        Station destination = this.getStationByName(this.getAndValidateInput("Til"));
        
        //Station origin = this.getStationByName("Skogen");
        //Station destination = this.getStationByName("Bøler");
        
        //Station origin = this.getStationByName("Sannergata");
        //Station destination = this.getStationByName("Jernbanetorget");
        
        // Find routes
        this.findRoutes(origin, destination);
    }
        
    private String getAndValidateInput(String q) {
        Scanner input = new Scanner(System.in);
        String stationName;
        int attempts = 0;
        
        do {
            String query = ((attempts == 0) ? q + ": " : "Fant ikke stasjon, prøv igjen: ");
            System.out.print("  > " + query);
            stationName = input.nextLine().replace(' ', '-');
            attempts++;
        } while (!this.isStation(stationName));
        
        return stationName;
    }
    
    // Find routes
    private void findRoutes(Station from, Station to) {
		// Header
		System.out.println("\n\n Aktuelle reiseruter mellom " + from.getName() + " og " + to.getName());
		System.out.println("--------------------------------------------------------------------------------\n");
        
        ArrayList<Line> fromLines = from.getLines();
        ArrayList<Line> toLines = to.getLines();
        
        
        // DEBUG START
        /*
        System.out.println("Linjer - " + from.getName() + ":");
        for (Line l : fromLines)
            System.out.println(" > linje " + l.getNumber() + ", stasjon nr.: " + l.getStationNumber(from));
        
        System.out.println("Linjer - " + to.getName() + ":");
        for (Line l : toLines)
            System.out.println(" > linje " + l.getNumber() + ", stasjon nr.: " + l.getStationNumber(to));
        
        System.out.println();
        */
        // DEBUG END
        
        // Direct routes
        ArrayList<Line> direct = new ArrayList<Line>(fromLines);
        direct.retainAll(toLines);
        
        if(!direct.isEmpty()) {
            System.out.println(" Direkterute(r):");
            
            for (Line l : direct) {
                System.out.print("   o Ta " + l.getType() + " linje " + l.getNumber() + " fra: " + from.getName());
                System.out.println(" til " + to.getName() + " retning " + l.getEndOfLine(from, to).getName() + ".");
                System.out.println("   o Estimert reisetid: ... .\n");
            }
        }
        
        // Indirect routes
        else {
            System.out.println(" Omstigningsrute(r):");
            
            for (Line l : fromLines) {
                for (Station s : l.getStations()) {
                    for (Line sl : s.getLines()) {
                        // Found route (with only one transfer!)
                        if(sl.hasStation(to)) {
                            System.out.print("   o Ta " + l.getType() + " linje " + l.getNumber() + " fra: " + from.getName());
                            System.out.println(" til " + s.getName() + " retning " + l.getEndOfLine(from, s).getName() + ",");
                            System.out.print("   | og deretter " + sl.getType() + " linje " + sl.getNumber() + " retning " + sl.getEndOfLine(s, to).getName());
                            System.out.println(" til " + to.getName() + ".");
                            System.out.println("   o Estimert reisetid: ... .\n");
                            
                        }
                    }
                }
            }
        }
        
        // Travel more
        while (true) {
            System.out.print(" > Reise mer (j/n)? ");
        
            Scanner input = new Scanner(System.in);
            String c = input.nextLine().trim();
            
            if (c.equalsIgnoreCase("j")) {
                System.out.println();
                this.queryFromTo();
            }
            
            return;
        }
    }

    
    // Return line (by number)
    private Line getLineByNumber(int n) {
        Line l = this.getLines().get(n);
        
        if (l != null)
            return l;
            
        return null;
    }
	
	// Return station (by name)
	private Station getStationByName(String n) {
        Station s = this.getStations().get(n.toLowerCase().trim());
        
		if (s != null)
			return s;
		
		return null;
	}
    
    // Check if existing station (by name)
	private boolean isStation(String n) {
		if (this.getStations().get(n.toLowerCase().trim()) == null)
			return false;
		
		return true;
	}
    
    // Getters/Setters
    private HashMap<Integer, Line> getLines() {
        return this.lines;
    }
	private HashMap<String, Station> getStations() {
		return this.stations;
	}
}


// 	Class: Line
// =================================================================================
class Line {
	/*	Variables
	------------------------------------ */
	private int number;
	
	private ArrayList<Station> stations = new ArrayList<Station>();
    
	
	/*	Methods
	------------------------------------ */
	
	// Add station to line
	void addStation(Station s) {
		stations.add(s);
	}
	
	// Check if line goes through station
    boolean hasStation(Station s) {
        if (this.getStations().indexOf(s) < 0)
            return false;

		return true;
    }
	
	// Getters and setters
	void setNumber(int n) {
		this.number = n;
	}
	int getNumber() {
		return this.number;
	}
    ArrayList<Station> getStations() {
        return this.stations;
    }
    int getStationNumber(Station s) {
        return this.getStations().indexOf(s) + 1;
    }
    Station getEndOfLine(Station from, Station to) {
		if (this.getStations().indexOf(from) < this.getStations().indexOf(to))
            return this.getStations().get(this.getStations().size() - 1);
        else
            return this.getStations().get(0);
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
	private String name;
    
    private ArrayList<Line> lines = new ArrayList<Line>();
	
	
	/*	Methods
	------------------------------------ */
	
	// Add line to station
	void addLine(Line l) {
		lines.add(l);
	}
	
	// Getters and setters
    ArrayList<Line> getLines() {
        return this.lines;
    }
	void setName(String n) {
		this.name = n;
	}
	String getName() {
		return this.name;
	}
}


// 	Class: Transfer
// =================================================================================
class Transfer {

}
