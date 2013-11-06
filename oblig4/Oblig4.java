// Imports
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Calendar;


// =================================================================================
// Class: Oblig4
//=================================================================================
class Oblig4 {
    // Variables
    static Building ab;

    // Main
    public static void main(String[] args) {
        // Construct building, set initial values
        ab = new Building();
        ab.setName("Utsyn");
        ab.setFloors(3);
        ab.setRoomsPerFloor(6);
        ab.setDeposit(15000);
        ab.setFee(3000);
        ab.setRoomExpense(1200);
        ab.setFloorExpense(1700);

        // Initialize
        HousingSystem hs = new HousingSystem(ab);
    }
}

//=================================================================================
// Class: HousingSystem
//=================================================================================
class HousingSystem {
    //  Variables
    // -------------------------------------------------------------------------
    static final String DATAFILE = "hybeldata2.txt";
    static final String KILLFILE = "torpedo.txt";
    static final String EMPTY_ROOM = "TOM HYBEL";
    private Building b;
    private InputHelper in;
    private OutputHelper out;
    
    //  Constructor
    // -------------------------------------------------------------------------
    HousingSystem(Building b) {
        // Helpers
        in = new InputHelper();
        out = new OutputHelper();
        
        // Set building
        this.b = b;
        
        // Parse data
        this.parseData(DATAFILE);
        
        // Header and menu
        this.header();
        this.mainMenu();
    }
    
    //  Methods
    // -------------------------------------------------------------------------
    
    // Parse data file
    public void parseData(String datafile) {
        try {
            BufferedReader data = new BufferedReader(new FileReader(datafile));

            // Stats
            String[] stats = data.readLine().split(";");

            b.setLastRunMonth(Integer.parseInt(stats[0].trim()));
            b.setLastRunYear(Integer.parseInt(stats[1].trim()));
            b.setTotalProfit(Integer.parseInt(stats[2].trim()));
            b.setUptime(Integer.parseInt(stats[3].trim()));
            
            int rent = Integer.parseInt(stats[4].trim());
            int rentTop = Integer.parseInt(stats[5].trim());
            
            // Rooms
            ArrayList<Room> rooms = new ArrayList<Room>();
            String line;

            while ((line = data.readLine()) != null) {
                String[] d = line.split(";");

                // Room
                Room room = new Room();
                room.setFloor(Integer.parseInt(d[0]));
                room.setId(d[1]);
                room.setRent(rent);

                if (room.getFloor() == b.getFloors()) room.setRent(rentTop);

                // Tenant
                if(!d[3].equals(EMPTY_ROOM)) {
                    Tenant tenant = new Tenant();
                    tenant.setName(d[3]);
                    tenant.setCredit(Integer.parseInt(d[2]));
                    room.setTenant(tenant);
                }

                rooms.add(room);
            }
            
            b.setRooms(rooms);

            data.close();

        } catch(IOException e) {
            // No file: Generate data
            Calendar cal = Calendar.getInstance();

            b.setLastRunMonth((cal.get(Calendar.MONTH)+1));
            b.setLastRunYear(cal.get(Calendar.YEAR));
            b.setTotalProfit(0);
            b.setUptime(0);
            
            ArrayList<Room> rooms = new ArrayList<Room>();
            
            for (int i = 0; i < b.getFloors(); i++) {
                for (int j = 0; j < b.getRoomsPerFloor(); j++) {
                    String letter = "" + (char) ((j+1) + 64);
                    
                    Room empty = new Room();
                    empty.setRent(0);
                    empty.setTenant(null);
                    empty.setFloor((i+1));
                    empty.setId(letter);
                
                    rooms.add(empty);
                }
            }
            
            b.setRooms(rooms);
        }
    }
    
    // Save data to file
    public void saveData() {
    
    }
    
    // Header
    public void header() {
        // Header
        out.printBigSep();
        out.println(" Gulbrand Grås Husleiesystem");
        out.printBigSep();
        
        out.println("\n Hybelhus: " + b.getName());
    }
    
    // Main menu
    public void mainMenu() {
        // Options
        out.println("\n Meny:");
        out.println("\t1. Skriv oversikt");
        out.println("\t2. Registrer ny leietager");
        out.println("\t3. Registrer betaling fra leietager");
        out.println("\t4. Registrer frivilling utflytting");
        out.println("\t5. Månedskjøring av husleie");
        out.println("\t6. Kast ut leietagere");
        out.println("\t7. Øk husleien");
        out.println("\t8. Avslutt");

        int option = -1;

        while (option != 0) {
            switch (in.getInt("Velg ønsket funksjon", 1, 8)) {
                case 1: this.printOverview(); break;
                case 2: this.newTenant(); break;
                case 3: this.registerPayment(); break;
                case 4: this.removeTenant(); break;
                case 5: this.runPayments(); break;
                case 6: this.evictTenant(); break;
                case 7: this.getRicher(); break;
                case 8: this.exitSystem(); break;
            }
        }
    }
    
    // Function (1): Print overview
    public void printOverview() {
        out.println("\n [1] Hybeloversikt for " + b.getName());
        out.printSmallSep();

        // Rooms
        int prevFloor = 1;

        for (Room room : b.getRooms()) {
            if (room.getFloor() > prevFloor) out.println("");
            prevFloor = room.getFloor();
        
            // Tenant
            Tenant t = room.getTenant();
            String name = (room.isOccupied() ? t.getName() : "( LEDIG )");
            int credit = (room.isOccupied() ? t.getCredit() : 0);
            
            out.print(" " + room.getName());
            out.print("\t\t" + name);
            out.println("\t\t" + credit + " kr.");
        }

        // Stats
        out.println("\n - Dato:\t\t" + b.getLastRunMonth() + "/" + b.getLastRunYear());
        out.println(" - Driftstid:\t\t" + b.getUptime() + " måneder");
        out.println(" - Fortjeneste:\t\t" + b.getTotalProfit() + " kr.");
    }
    
    // Function (2): Add new tenant
    public void newTenant() {    
        // Header
        out.println("\n [2] Registrer ny leietager i " + b.getName());
        out.printSmallSep();

        // Unoccupied rooms
        ArrayList<Room> unoccupied = b.getUnoccupiedRooms();
        
        if(unoccupied.size() > 0) {
            // List unoccupied rooms
            out.println(" - Ledige rom:");
        
            for (Room room : unoccupied) {
                out.println("\t" + room.getName() + " (" + room.getRent() + " kr.)");
            }
            
            // Select room
            out.println("");    
            Room room = b.getRoomByName(in.getString("Velg ledig leilighet"));
            
            if (room != null) {
                if (!room.isOccupied()) {
                    // Register new tenant
                    Tenant nt = new Tenant();
                    nt.setName(in.getString("Navn på ny leietaker"));
                    nt.setCredit(b.getDeposit() - room.getRent());
                    room.setTenant(nt);
                    
                    out.println("\n\t- " + nt.getName() + " flytter inn i leilighet " + room.getName());
                    out.println("\t\t- Leie:\t\t\t" + room.getRent() + " kr.");
                    out.println("\t\t- Depositum:\t\t" + b.getDeposit() + " kr.");
                    out.println("\t\t- Gjenværende saldo:\t" + nt.getCredit() + " kr.");
                }
                else out.println("\t- Rommet er ikke ledig. Må enten først benytte menyvalg 4 for frivillig utflytting, eventuelt menyvalg 6 for \"frivillig\" utflytting!");
            }
            else out.println("\t- Ugyldig romnummer. Prøv igjen!");
        }
        else out.println(" - Ingen ledige rom, desverre!");
    }
    
    // Function (3): Register payment
    public void registerPayment() {
        // Header
        out.println("\n [3] Registrer betaling fra leietager");
        out.printSmallSep();
        
        // Select room
        Room room = b.getRoomByName(in.getString("Velg leilighet"));
        
        if (room != null) {
            // Check if room is occupied
            if (room.isOccupied()) {
                // Register new payment
                out.println("\t- Beboer: " + room.getTenant().getName());
                out.println("\t- Husleie: " + room.getRent() + " kr.");
                
                int payment = in.getInt("Innbetalt beløp", 0, 999999);
                
                out.println("\n\t- Betaling (" + payment + " kr.) registrert! Bokføres leietagers saldo.");
            }
            else out.println("\t- Rommet er ubebodd!");
        }
        else out.println("\t- Ugyldig romnummer. Prøv igjen!");
    }
    
    // Function (4): Register tenant moving out
    public void removeTenant() {
        System.out.println("\n [4] Registrer frivillig utflytting");
        out.printSmallSep();

        // Check if tenant exists
        Room r = b.getRoomByTenantName(in.getString("Navn på leietager"));
        
        if (r != null) {
            Tenant t = r.getTenant();
            
            // Check if rent payable
            if(t.getCredit() >= r.getRent()) {
                // Remove tenant
                out.println("\n\t- " + t.getName() + " flyttet ut fra leilighet " + r.getName() + "!");
                out.println("\t\t- Leie:\t\t" + r.getRent() + " kr.");
                out.println("\t\t- Saldo:\t" + t.getCredit() + " kr.");
                out.println("\t\t- Utbetales:\t" + (t.getCredit() - r.getRent()) + " kr.");
                r.setTenant(null);
            }
            else {
                // Remove tenant
                out.println("\n\t- " + t.getName() + " kan ikke flytte ut fra leilighet " + r.getName() + "!");
                out.println("\t\t- Leie:\t\t" + r.getRent() + " kr.");
                out.println("\t\t- Saldo:\t" + t.getCredit() + " kr.");
                out.println("\t\t- Skylder:\t" + (t.getCredit() - r.getRent()) + " kr.");
            }
        }
        else out.println("\t- Leietager ble ikke funnet i " + b.getName() + "!");
    }
    
    // Function (5): Run payments
    public void runPayments() {
        // Header
        System.out.println("\n [5] Månedskjøring av husleie for " + b.getName());
        out.printSmallSep();
        
        out.println(" - Forrige månedskjøring: " + b.getLastRunMonth() + "/" + b.getLastRunYear() + "\n");
        
        // User confirmation
        while (true) {
            String c = in.getString("Ønsker du å utføre månedskjøring for " + b.getNextRunMonth() + "/" + b.getNextRunYear() + " (j/n)?");

            if (c.equalsIgnoreCase("j")) {
                // Execute
                int monthProfit = 0;
                
                for (Room r : b.getRooms()) {
                    int roomExpense = b.getRoomExpense();
                    int roomProfit = 0;
                    
                    // Occupied room
                    if (r.isOccupied()) {
                        Tenant t = r.getTenant();
                        
                        // $$$
                        int rent = r.getRent();
                        int credit = t.getCredit();
                        int newCredit = (credit - rent);
                        int in = ((credit < rent) ? credit : rent);
                        roomProfit = (in - roomExpense);
                        
                        out.print("\tROM: " + r.getName());
                        out.print("\t\tLEIE: " + rent);
                        out.print("\tSALDO: " + credit);
                        out.print("\t\tGJENVÆRENDE: " + newCredit);
                        out.print("\tINN: " + in);
                        out.print("\tUTGIFT: " + roomExpense);
                        out.print("\tPROFIT: " + roomProfit);
                        out.println("");
                        
                        t.setCredit(newCredit);
                    }
                    // Unoccupied room
                    else {
                        roomProfit = (roomProfit - roomExpense);
                        
                        out.print("\tROM: " + r.getName());
                        out.print("\t\t\t\t\t\t\t\t\t\t\t\tUTGIFT: " + roomExpense);
                        out.print("\tPROFIT: " + roomProfit);
                        out.println("");
                    }
                    
                    monthProfit = monthProfit + roomProfit;
                }
                
                // Common areas (pure expense)
                int floorExpenses = (b.getFloors() * b.getFloorExpense());
                
                monthProfit = (monthProfit - floorExpenses);
                
                //out.println("\n\tFELLESAREAL: " + b.getFloors() + " á " + b.getFloorExpense() + " = " + floorExpenses);

                // Update building stats
                b.setLastRunYear(b.getNextRunYear());
                b.setLastRunMonth(b.getNextRunMonth());
                b.setUptime(b.getUptime() + 1);
                b.setTotalProfit(b.getTotalProfit() + monthProfit);
                
                // Output
                out.println("\n - Resultat av månedskjøring:");
                out.println("\t- Måned:\t\t" + b.getLastRunMonth());
                out.println("\t- År:\t\t\t" + b.getLastRunYear());
                out.println("\t- Driftstid:\t\t" + b.getUptime() + " måned(er)");
                out.print("\t- Husleiesatser:");
                
                int[] rates = b.getCurrentRentRates();
                for (int i = 0; i < rates.length; i++) out.print("\tetg. " + (i+1) +":\t" + rates[i] + " kr.");
                
                out.println("\n\t- Månedsprofitt:\t" + monthProfit + " kr.");
                out.println("\t- Profitt totalt:\t" + b.getTotalProfit() + " kr.");
                out.println("\t- Gjennomsnitt:\t\t" + (b.getTotalProfit() / b.getUptime()) + " kr.");
                
                // Done
                return;
            }
            if (c.equalsIgnoreCase("n")) return;
        }
    }
    
    // Function (6): Evict tenant
    public void evictTenant() {
        // Header
        System.out.println("\n [6] Kast ut leietagere");
        out.printSmallSep();
        
        // Find debt-ridden tenants
        for (Room r : b.getRooms()) {
            // Occupied room
            if (r.isOccupied()) {
                // Tenant
                Tenant t = r.getTenant();
                
                // Ows more than one rent
                if(t.getCredit() < -r.getRent()) {
                    // Calculate claim
                    int debt = -t.getCredit();
                    int claim = (b.getFee() + debt);
                    
                    // Add to profits
                    b.setTotalProfit(b.getTotalProfit() + ((b.getFee()/2) + debt));
                    
                    // Notify mercenary
                    this.killTenant(r.getFloor(), r.getId(), claim);
                    
                    // Set unoccupied
                    r.setTenant(null);
                    
                    // Output
                    out.println(" " + r.getName() + ": " + t.getName() + "\tSkylder: " + t.getCredit() + "kr. \tKrav: " + claim + "kr. \tMerknad: Hole tilkalt!");
                }
                
            }
        }
        
    }
    
    // Function (7): Increase rental fees
    public void getRicher() {
        // Header
        System.out.println("\n [7] Øk husleien");
        out.printSmallSep();
        
        // Current rates
        int[] current = b.getCurrentRentRates();
        int[] newrates = new int[current.length];
        
        out.println(" - Gjeldende satser:");
        for (int i = 0; i < current.length; i++)
            out.println("\t- Etg. " + (i+1) +":\t" + current[i] + " kr.");
        out.println("");
            
        // Change rates
        while (true) {
            String c = in.getString("Ønsker du å endre satsene (j/n)?");

            if (c.equalsIgnoreCase("j")) {
                // Update rate per floor
                for (int i = 0; i < newrates.length; i++) {
                    int rate = in.getInt("Ny husleie for etg. " + (i+1), 0, 9999999);
                    newrates[i] = rate;
                    
                    for (Room r : b.getRooms())
                        if(r.getFloor() == (i+1))
                            r.setRent(rate);
                }
                
                // New rates
                out.println("\n - Nye satser:");

                for (int i = 0; i < newrates.length; i++)
                    out.println("\t- Etg. " + (i+1) +":\t" + newrates[i] + " kr.");
            
                // Done
                return;
            }
            if (c.equalsIgnoreCase("n")) return;
        }
    }
    
    // Function (8): Quit
    public void exitSystem() {
        // Save to datafile
        out.println("\t- Lagrer data...");
        
        try {
            // Write to file
            PrintWriter file = new PrintWriter(new FileWriter(DATAFILE));

            int[] rent = b.getCurrentRentRates();
              
            file.println(b.getLastRunMonth() + ";" + b.getLastRunYear() + ";" + b.getTotalProfit() + ";" + b.getUptime() + ";" + rent[0] + ";" + rent[(rent.length-1)]);
            
            for (Room r : b.getRooms()) {
                String name = (r.isOccupied() ? r.getTenant().getName() : EMPTY_ROOM);
                int credit = (r.isOccupied() ? r.getTenant().getCredit() : 0);
                
                file.println(r.getFloor() + ";" + r.getId() + ";" + credit + ";" + name);
            }
                
            file.close();
        } catch (IOException e) {
            System.out.println("\t\t- Kunne ikke skrive til fil!");
        }
        
        // Exit system
        out.println("\t- Takk for nå!\n");
        System.exit(0);
    }
    
    // Function: Notify mercenary 
    public void killTenant(int floor, String room, int claim) {
        // Get tenant
        String name = b.getRoomByName(floor + room).getTenant().getName();
    
        // Write to file
        try {
            PrintWriter file = new PrintWriter(new FileWriter(KILLFILE, true));

            file.println(floor + room + ";" + name + ";" + claim);

            file.close();
        } catch (IOException e) {
            System.out.println(" - Kunne ikke skrive til fil!");
        }
    }
}


//=================================================================================
// Class: Building
//=================================================================================
class Building {
    //  Variables
    // -------------------------------------------------------------------------
    private String name;
    private int floors;
    private int roomsPerFloor;
    private int roomExpense;
    private int floorExpense;
    private ArrayList<Room> rooms;
    private int deposit;
    private int fee;
    private int lastRunMonth;
    private int lastRunYear;
    private int totalProfit;
    private int uptime;
    
    //  Constructor
    // -------------------------------------------------------------------------
    Building() {

    }
    
    //  Methods
    // -------------------------------------------------------------------------
    
    // Building
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    
    // Structure
    public void setFloors(int floors) {
        this.floors = floors;
    }
    public int getFloors() {
        return this.floors;
    }
    public void setRoomsPerFloor(int rooms) {
        this.roomsPerFloor = rooms;
    }
    public int getRoomsPerFloor() {
        return this.roomsPerFloor;
    }
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }
    public ArrayList<Room> getRooms() {
        return this.rooms;
    }
    public ArrayList<Room> getUnoccupiedRooms() {
        ArrayList<Room> unoccupied = new ArrayList<Room>();
        
        for (Room room : this.getRooms())
            if (!room.isOccupied())
                unoccupied.add(room);
                
        return unoccupied;
    }
    public Room getRoomByName(String name) {
        for (Room room : this.getRooms())
            if(room.getName().equalsIgnoreCase(name.replace(" ", "")))
                return room;
                
        return null;
    }
    public Room getRoomByTenantName(String name) {
        for (Room room : this.getRooms())
            if (room.getTenant() != null)
                if (room.getTenant().getName().equalsIgnoreCase(name))
                    return room;
                
        return null;
    }
    
    // Operation
    public int getDeposit() {
        return this.deposit;
    }
    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }
    public int getFee() {
        return this.fee;
    }
    public void setFee(int fee) {
        this.fee = fee;
    }
    public void setRoomExpense(int expense) {
        this.roomExpense = expense;
    }
    public int getRoomExpense() {
        return this.roomExpense;
    }
    public void setFloorExpense(int expense) {
        this.floorExpense = expense;
    }
    public int getFloorExpense() {
        return this.floorExpense;
    }
    public void setLastRunMonth(int month) {
        if(month == 13)
            month = 1;
        this.lastRunMonth = month;
    }
    public int getLastRunMonth() {
        return this.lastRunMonth;
    }
    public void setLastRunYear(int year) {
        this.lastRunYear = year;
    }
    public int getLastRunYear() {
        return this.lastRunYear;
    }
    public int getNextRunMonth() {
        if(this.getLastRunMonth() == 12)
            return 1;
        return (this.getLastRunMonth() + 1);
    }
    public int getNextRunYear() {
        if(this.getNextRunMonth() == 1)
            return (this.getLastRunYear() + 1);
        return this.getLastRunYear();
    }
    public void setTotalProfit(int profit) {
        this.totalProfit = profit;
    }
    public int getTotalProfit() {
        return this.totalProfit;
    }
    public void setUptime(int months) {
        this.uptime = months;
    }
    public int getUptime() {
        return this.uptime;
    }
    public int[] getCurrentRentRates() {
        int[] rates = new int[this.getFloors()];
        
        for (Room r : this.getRooms())
            rates[(r.getFloor()-1)] = r.getRent();
        
        return rates;
    }
}


//=================================================================================
// Class: Room
//=================================================================================
class Room {
    //  Variables
    // -------------------------------------------------------------------------
    private int floor;
    private String id;
    private int rent;
    private Tenant tenant = null;

    //  Methods
    // -------------------------------------------------------------------------
    public int getFloor() {
        return floor;
    }
    public void setFloor(int floor) {
        this.floor = floor;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.floor + this.id;
    }
    public int getRent() {
        return rent;
    }
    public void setRent(int rent) {
        this.rent = rent;
    }
    public Tenant getTenant() {
        return tenant;
    }
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    public boolean isOccupied() {
        if(tenant == null)
            return false;
        return true;
    }
}


//=================================================================================
// Class: Tenant
//=================================================================================
class Tenant {
    //  Variables
    // -------------------------------------------------------------------------
    private String name;
    private int credit;

    //  Methods
    // -------------------------------------------------------------------------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCredit() {
        return credit;
    }
    public void setCredit(int credit) {
        this.credit = credit;
    }
}


//=================================================================================
// Class: Helpers
//=================================================================================
class InputHelper {
    Scanner input;

    InputHelper() {
        input = new Scanner(System.in);
    }

    // Get int input, then validate
    int getInt(String query, int min, int max) {
        int i;

        do {
            System.out.print("\n > " + query + " (" + min + "-" + max + "): ");
            while (!input.hasNextInt()) {
                System.out.print(" > Ikke gyldig tall, prøv igjen: ");
                input.next();
            }
            i = input.nextInt();
        } while (i < min || i > max);

        input.nextLine();

        return i;
    }

    // Get string input
    String getString(String queryText) {
        System.out.print(" > " + queryText + ": ");

        String choice = input.nextLine().trim();

        return choice;
    }
}

class OutputHelper {
    // Constants
    final String SEPBIG = "===============================================================";
    final String SEPSMALL = "---------------------------------------------------------------\n";
    
    void print(String string) {
        System.out.print(string);
    }
    void println(String string) {
        System.out.println(string);
    }
    void printBigSep() {
        System.out.println(SEPBIG);
    }
    void printSmallSep() {
        System.out.println(SEPSMALL);
    }
}
