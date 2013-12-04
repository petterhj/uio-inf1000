import java.io.*;   // For BufferedReader og FileReader
import java.util.*; // For HashMap

class Test {
    public static void main(String args[]) {
        //  Open file
        try {
            // Les fil
            BufferedReader data = new BufferedReader(new FileReader("Ord.txt"));
            
            HashMap<String, Integer> ord = new HashMap<String, Integer>();
            
            String linje;
            
            while((linje = data.readLine()) != null)
                for (String o : linje.split(" "))
                    ord.put(o, o.trim().length());      // Bare unike, erstatter
            
            // Tell opp
            HashMap<Integer, Integer> stats  = new HashMap<Integer, Integer>();
            
            for (int i : ord.values()) {                // i = ordlengde
                if (i < 5) {
                    if (stats.get(i) == null)
                        stats.put(i, 1);                    // Nytt antall
                    else
                        stats.put(i, (stats.get(i) + 1));   // Legg til antall
                }
            }
            
            // Skriv ut
            for (int i : stats.keySet())
                System.out.println("Ord av lengde " + i + ": " + stats.get(i));
            
            
            
        } catch (IOException e) {
            System.out.println("Kunne ikke lese fil!");
        }
    }
}
