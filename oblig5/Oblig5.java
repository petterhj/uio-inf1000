// Imports
import java.io.*;
import java.lang.Math;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.DecimalFormat;


//  Class: Oblig5
// =================================================================================
class Oblig5 {
    // Main
    public static void main(String[] args) {
        // Header
        System.out.println("\n================================================================================");
        System.out.println(" Reiseplanlegger");
        System.out.println("================================================================================\n");
		
        // Initiate planner
        Planner pl = new Planner();
        
        // Footer (spacer)
        System.out.println();
    }
}


//  Class: Planner
// =================================================================================
class Planner {
    // Variables
    private final String DATAFILE = "trikkogtbaneutf8-ver2.txt";
    private final double TRAVELTIME_TRAM = 1.4;
    private final double TRAVELTIME_SUBW = 1.8;
    private final double TRANSFERTIME_TRAM = 5.0;
    private final double TRANSFERTIME_SUBW = 7.5;
    private final double TRANSFERTIME = 3.0;
	
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
                } 
                // Station
                else {
                    stationNumber++;
                    Station station;
                    
                    if (this.isStation(dataline)) {
                        // Get existing station
                        station = this.getStationByName(dataline);
                    } else {
                        // Add new station
                        station = new Station();
                        station.setName(dataline);
                        stations.put(station.getName().toLowerCase(), station);
                    }
                    
                    // Add line to station
                    station.addLine(this.getLineByNumber(lineNumber));

                    // Add station to line
                    this.getLineByNumber(lineNumber).addStation(station);
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
        
        // Find routes
        this.findRoutes(origin, destination);
    }
    
    // Validate user input
    private String getAndValidateInput(String q) {
        Scanner input = new Scanner(System.in);
        String stationName;
        int attempts = 0;
        
        do {
            String query = ((attempts == 0) ? q + ": " : "Fant ikke stasjon, prøv igjen: ");
            System.out.print("  > " + query);
            stationName = input.nextLine().replace(' ', '-');
            attempts++;
        } while ((!this.isStation(stationName)) || (stationName.equalsIgnoreCase("Ringen")));
        
        return stationName;
    }
    
    // Find routes
    private void findRoutes(Station from, Station to) {
        // Header
        System.out.println("\n\n Aktuelle reiseruter mellom " + from.getName() + " og " + to.getName());
        System.out.println("--------------------------------------------------------------------------------\n");
        
        ArrayList<Line> fromLines = from.getLines();
        ArrayList<Line> toLines = to.getLines();
        
        DecimalFormat df = new DecimalFormat("#,##0.0");
        
        double fastest = 999;
        String fastest_string = "";
        
        // Direct routes
        ArrayList<Line> direct = new ArrayList<Line>(fromLines);
        direct.retainAll(toLines);
        
        if(!direct.isEmpty()) {
            System.out.println(" Direkterute(r):");
            
            for (Line l : direct) {
                // Travel time
                int stops = Math.abs(l.getStationNumber(to) - l.getStationNumber(from));
                double time = ((l.getType() == "trikk") ? (stops * TRAVELTIME_TRAM) : (stops * TRAVELTIME_SUBW));
                
                // Output
                String output  = "   o Ta " + l.getType() + " linje " + l.getNumber() + " fra: " + from.getPrettyName();
                       output += " til " + to.getPrettyName() + " retning " + l.getEndOfLine(from, to).getPrettyName() + ".\n";
                       output += "   o Estimert reisetid: " + df.format(time) + " min.\n";
                
                System.out.println(output);
                
                if (time < fastest) {
                    fastest = time;
                    fastest_string = output;
                }
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
                            // Travel time
                            int stopsL1 = Math.abs(l.getStationNumber(s) - l.getStationNumber(from));
                            double timeL1 = ((l.getType() == "trikk") ? (stopsL1 * TRAVELTIME_TRAM) : (stopsL1 * TRAVELTIME_SUBW));
                            double wait = (((sl.getType() == "trikk") ? TRANSFERTIME_TRAM : TRANSFERTIME_SUBW) + TRANSFERTIME);
                            int stopsL2 = Math.abs(sl.getStationNumber(to) - sl.getStationNumber(s));
                            double timeL2 = ((sl.getType() == "trikk") ? (stopsL2 * TRAVELTIME_TRAM) : (stopsL2 * TRAVELTIME_SUBW));
                            double time = timeL1 + timeL2 + wait;
                            
                            // Output
                            String output  = "   o Ta " + l.getType() + " linje " + l.getNumber() + " fra: " + from.getPrettyName();
                                   output += " til " + s.getPrettyName() + " retning " + l.getEndOfLine(from, s).getPrettyName() + ",\n";
                                   output += "   | og deretter " + sl.getType() + " linje " + sl.getNumber() + " retning " + sl.getEndOfLine(s, to).getPrettyName();
                                   output += " til " + to.getPrettyName() + ".\n";
                                   output += "   o Estimert reisetid: " + df.format(time) + " min.\n";
                            
                            System.out.println(output);
                            
                            if (time < fastest) {
                                fastest = time;
                                fastest_string = output;
                            }
                        }
                    }
                }
            }
        }
        
        // Shortest travel time
        System.out.println(" Raskeste reisevei:");
        System.out.println(fastest_string);
        
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
        return this.getLines().get(n);
    }
	
    // Return station (by name)
    private Station getStationByName(String n) {
        return this.getStations().get(n.toLowerCase().trim());
    }
    
    // Check if existing station (by name)
    private boolean isStation(String n) {
        if (this.getStations().get(n.toLowerCase().trim()) == null)
            return false;
		
        return true;
    }
    
    // Return all lines
    private HashMap<Integer, Line> getLines() {
        return this.lines;
    }
    
    // Return all stations
    private HashMap<String, Station> getStations() {
        return this.stations;
    }
}


//  Class: Line
// =================================================================================
class Line {
    // Variables
    private int number;
    private ArrayList<Station> stations = new ArrayList<Station>();
    
	
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
	
    // Line number
    void setNumber(int n) {
        this.number = n;
    }
    int getNumber() {
        return this.number;
    }
    
    // Stations
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

    // Type
    String getType() {
        return ((this.getNumber() < 10) ? "t-bane" : "trikk");
    }
}


//  Class: Station
// =================================================================================
class Station {
    // Variables
    private String name;
    private ArrayList<Line> lines = new ArrayList<Line>();
    
	
    // Add line to station
    void addLine(Line l) {
        lines.add(l);
    }

    // Return lines
    ArrayList<Line> getLines() {
        return this.lines;
    }
    
    // Name
    void setName(String n) {
        this.name = n;
    }
    String getName() {
        return this.name;
    }
    String getPrettyName() {
        return this.getName().replace("-", " ");
    }
}

