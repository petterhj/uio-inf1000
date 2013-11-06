//=================================================================================
// Class: Building
//=================================================================================
class Building {
    // Constants
    final String DATAFILE       = "hybeldata.txt";
    final String EMPTY          = "TOM HYBEL";
    final int DEPOSIT           = 15000;
    final int EXPENSE_ROOM      = 1200;
    final int EXPENSE_FLOOR     = 1700;
    
    final String SEP            = "---------------------------------------------------------------\n";
    final String ERROR_NOROOM   = "\t[FEIL] Ugyldig romnummer. Prøv igjen!";

    // Variables
    ArrayList<Room> rooms = new ArrayList<Room>();

    String buildingName;
    int buildingFloors, buildingRooms;

    int lastRunMonth, lastRunYear;
    int totalEarnings, totalMonths;

    int rent, rentTop;
    
    InputHelper in;
    
    
    private String name;
    private int floors;
    private int roomsPerFloor;
    private int roomCosts;
    private int floorCosts;
    

    // Constructor
    Building() {
        // Building
        this.buildingName = buildingName;
        this.buildingFloors = buildingFloors;
        this.buildingRooms = buildingRooms;

        // Read building data
        try {
            BufferedReader data = new BufferedReader(new FileReader(DATAFILE));

            // Stats
            String[] stats = data.readLine().split(";");

            this.setLastRun(Integer.parseInt(stats[0].trim()), Integer.parseInt(stats[1].trim()));
            // TODO: GET/SET!!!!!!
            this.totalEarnings = Integer.parseInt(stats[2].trim());
            this.totalMonths = Integer.parseInt(stats[3].trim());
            
            int rent = Integer.parseInt(stats[4].trim());
            int rentTop = Integer.parseInt(stats[5].trim());

            // Rooms
            String line;

            while ((line = data.readLine()) != null) {
                String[] d = line.split(";");

                // Room
                Room room = new Room();
                room.setFloor(Integer.parseInt(d[0]));
                room.setId(d[1]);
                room.setDeposit(DEPOSIT);
                room.setRent(rent);

                if (room.getFloor() == this.buildingFloors) room.setRent(rentTop);

                // Tenant
                if(!d[3].equals(EMPTY)) {
                    Tenant tenant = new Tenant();
                    tenant.setName(d[3]);
                    tenant.setCredit(Integer.parseInt(d[2]));
                    room.setTenant(tenant);
                }

                rooms.add(room);
            }

            data.close();

        } catch(IOException e) {
            System.out.println("\t[FEIL] Kunne ikke lese datafilen!");
        }
        
        // Input helper
        in = new InputHelper();
    }
    
    
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
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
    public void setRoomCosts(int cost) {
        this.roomCosts = cost;
    }
    public int getRoomCosts() {
        return this.roomCosts;
    }
    public void setFloorCosts(int cost) {
        this.floorCosts = cost;
    }
    public int getFloorCosts() {
        return this.floorCosts;
    }
        

    // Function (1): Print overview
    void printOverview() {
        System.out.println("\n [1] Hybeloversikt for " + this.buildingName + ":");
        System.out.println(SEP);

        // Rooms
        int prevFloor = 0;

        for (Room room : this.rooms) {
            if (room.getFloor() > prevFloor) System.out.println();
            prevFloor = room.getFloor();

            // Tenant
            Tenant t = room.getTenant();
            String name = (room.isOccupied() ? t.getName() : "( LEDIG )");
            int credit = (room.isOccupied() ? t.getCredit() : 0);

            System.out.print(" " + room.getName());
            System.out.print("\t\t" + name);
            System.out.println("\t\t" + credit);
        }

        // Stats
        System.out.println("\n\n Dato:\t\t" + this.getLastRunMonth() + "/" + this.getLastRunYear());
        System.out.println(" Driftstid:\t" + this.totalMonths + " måneder");
        System.out.println(" Fortjeneste:\t" + this.totalEarnings + " kr.");
    }

    // Function (2): Add new tenant
    void newTenant() {
        // Header
        System.out.println("\n [2] Registrer ny leietager i " + this.buildingName + ":");
        System.out.println(SEP);

        // Unoccupied rooms
        System.out.println(" - Ledige rom:");
        int i = 0;
        for (Room room : this.rooms) {
            if(!room.isOccupied()) {
                System.out.println("\t" + room.getName() + " (" + room.getRent() + " kr.)");
                i++;
            }
        }
        if (i == 0)
            System.out.println("\t- Ingen ledige rom desverre!");
        
        // Select room
        System.out.println();    
        Room room = this.getRoomByName(in.getString("Velg ledig leilighet"));
        
        if (room != null) {
            // Check if room is unoccupied
            if (!room.isOccupied()) {
                // Register new tenant
                Tenant nt = new Tenant();
                nt.setName(in.getString("Navn på ny leietaker"));
                nt.setCredit(room.getDeposit() - room.getRent());
                room.setTenant(nt);
                
                System.out.println("\n\t- " + nt.getName() + " flyttet inn i leilighet " + room.getName() + ", med gjenværende saldo på kr. " + nt.getCredit() + "!");
            }
            else System.out.println("\t- Rommet er ikke ledig. Må enten først benytte menyvalg 4 for frivillig utflytting, eventuelt menyvalg 6 for \"frivillig\" utflytting!");
        }
        else System.out.println(ERROR_NOROOM);
    }

    // Function (3): Register payment
    void registerPayment() {
        // Header
        System.out.println("\n [3] Registrer betaling fra leietager:");
        System.out.println(SEP);
        
        // Select room
        Room room = this.getRoomByName(in.getString("Velg leilighet"));
        
        if (room != null) {
            // Check if room is occupied
            if (room.isOccupied()) {
                // Register new payment
                System.out.println("\t- Beboer: " + room.getTenant().getName());
                System.out.println("\t- Husleie: " + room.getRent() + " kr.");
                
                int payment = in.getInt("Innbetalt beløp", 0, 999999);
                
                System.out.println("\n\t- Betaling registrert (" + payment + " kr.)!");
            }
            else System.out.println("\t- Rommet er ubebodd!");
        }
        else System.out.println(ERROR_NOROOM);
    }

    // Function (4): Register tenant moving out
    void removeTenant() {
        // Header
        System.out.println("\n [4] Registrer frivillig utflytting:");
        System.out.println(SEP);
        
        // Check if tenant exists
        Room room = this.getRoomByTenantName(in.getString("Navn på leietager"));
        
        if (room != null) {
            // Tenant
            Tenant ot = room.getTenant();
        
            // Remove tenant
            System.out.println("\n\t- " + ot.getName() + " flyttet ut!");
            
            room.setTenant(null);            
        }
        else System.out.println("\t- Leietager ikke funnet!");
    }

    // Function (5): Run payments
    void runPayments() {
        // Header
        System.out.println("\n [5] Månedskjøring av husleie:");
        System.out.println(SEP);
        
        System.out.println(" - Forrige månedskjøring: " + this.getLastRunMonth() + "/" + this.getLastRunYear() + "\n");
        
        // User confirmation
        while (true) {
            String c = in.getString("Ønsker du å utføre månedskjøring for " + this.getNextRunMonth() + "/" + this.getNextRunYear() + " (j/n)?");

            if (c.equalsIgnoreCase("j")) {
                // Execute
                this.setLastRun(this.getNextRunMonth(), this.getNextRunYear());
                
                for (Room room : this.rooms) {
                    // Occupied room
                    if (room.isOccupied()) {
                        // Rent
                        int rent = room.getRent();
                        int credit = room.getTenant().getCredit();
                        int newcredit = (room.getTenant().getCredit() - room.getRent());
                        int profit = ((credit < rent) ? credit : rent);
                        
                        System.out.print("ROM: " + room.getName() + " -- ");
                        System.out.print("LEIE: " + rent + " -- ");
                        System.out.print("SALDO: " + credit + " -- ");
                        System.out.print("NYSALDO: " + newcredit + " -- ");
                        System.out.print("FORTJENESTE: " + profit + " -- ");
                        System.out.println();
                    }
                    // Unoccupied room
                    else {
                        
                    }
                }
                
                // Expenses
                System.out.println("ANTALL ROM: " + this.rooms.size() + " á " + 1200);
                System.out.println("FELLESAREAL: " + this.buildingFloors + " á " + 1700);
                
                // Return
                return;
            }
            if (c.equalsIgnoreCase("n"))
                return;
        }

    }

    // Function (6): Evict tenant
    void evictTenant() {
        System.out.println("eviction");
    }

    // Function (7): Increase rental fee
    void getRicher() {
        System.out.println("increase fee");
    }

    // Function (8): Quit
    void quit() {
        // Save to datafile
        System.out.println("\tLagrer data...");

        // Quit
        System.out.println("\tTakk for nå!");
        System.exit(0);
    }
    
    // Return room by name identifier
    Room getRoomByName(String name) {
        for (Room room : this.rooms)
            if(room.getName().equalsIgnoreCase(name.replace(" ", "")))
                return room;
                
        return null;
    }
    
    // Return room by tenant name
    Room getRoomByTenantName(String name) {
        for (Room room : this.rooms)
            if (room.getTenant() != null)
                if (room.getTenant().getName().equalsIgnoreCase(name))
                    return room;
                
        return null;
    }
    
    public void setLastRun(int month, int year) {
        this.lastRunMonth = month;
        this.lastRunYear = year;
    }
    public int getLastRunMonth() {
        return this.lastRunMonth;
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
        
        return this.getLastRunYear();
    }
}


//=================================================================================
// Class: Room
//=================================================================================
class Room {
    private int floor;
    private String id;
    private int rent;
    private int deposit;
    private Tenant tenant = null;

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
    public int getDeposit() {
        return this.deposit;
    }
    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }
}


//=================================================================================
// Class: Tenant
//=================================================================================
class Tenant {
    private String name;
    private int credit;

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

    /* Get int input, then validate */
    int getInt(String query, int min, int max) {
        int i;

        do {
            System.out.print("\n > " + query + " (" + min + "-" + max + "): ");
            while (!input.hasNextInt()) {
                System.out.print(" > Ikke et tall, prøv igjen: ");
                input.next();
            }
            i = input.nextInt();
        } while (i < min || i > max);

        input.nextLine();

        return i;
    }

    /* Get string input */
    String getString(String queryText) {
        System.out.print(" > " + queryText + ": ");

        String choice = input.nextLine().trim();

        return choice;
    }
}
