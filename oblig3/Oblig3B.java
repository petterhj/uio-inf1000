import java.io.*;
import java.util.*;

class Oblig3B {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Bruk: > java Oblig3A <tekstfil>");
        } else {
            System.out.println("[*] Starter tekstanalyse");
            OrdAnalyse oa = new OrdAnalyse(args[0]);
            oa.analyze();
            oa.printCommon();
        }
    }
}

class OrdAnalyse {
    // Variables
    String file;
    String[] words = new String[5000];
    int[] count = new int[5000];
    
    // Init
    OrdAnalyse(String file) {
        this.file = file;
    }
    
    // Analyze
    void analyze() {
        System.out.println("[*] Åpner " + this.file + "...");
        
        try {
            // Open file
            BufferedReader data = new BufferedReader(new FileReader(this.file));
            
            // Get all words in file
            System.out.println("[*] Analyserer...");
            
            String line;
            int wc = 0;
            
            while ((line = data.readLine()) != null) {
                String[] lw = line.split(" ");
                
                for (int i = 0; i < lw.length; i++) {
                    String new_word = lw[i].trim().toLowerCase();
                    
                    if (!this.inArray(new_word) && !new_word.equals("")) {
                        // New word
                        this.words[wc] = new_word;
                        this.count[wc] = 1;
                    
                        wc++;
                    }
                }
            }

            // Close file
            data.close();
            
            // Generate report
            this.generateReport();
            
        } catch (IOException e) {
            System.out.println("[!] Kunne ikke åpne fil. Avslutter.");
            System.exit(1);
        }
    }
    
    // Report
    void generateReport() {
        int[] wordcount = countWords();
        
        /* Write observation to file */
        try {
            PrintWriter file = new PrintWriter(new FileWriter("oppsummering.txt"));

            file.println("Antall ord lest: " + wordcount[0] + " og antall unike ord: " + wordcount[1] + "\n");
            
            for (int i = 0; i < this.words.length; i++)
                if (this.words[i] != null)
                    file.println(this.words[i] + "\t\t\t" + this.count[i]);

            file.close();

            System.out.println("[*] Resultater skrevet til \"oppsummering.txt\"!");
        } catch (IOException e) {
            System.out.println("[!] Kunne ikke skrive til fil!");
        }
    }
    
    
    // Print common words
    void printCommon() {
        System.out.println("[*] Skriver ut liste over vanlige ord...");
        // Find most common word
        int max = 0;
        
        for (int i = 0; i < this.count.length; i++)
            if (this.count[i] > this.count[max])
                max = i;
        
        System.out.println("\tVanligste ord: \"" + this.words[max] + "\" (" + this.count[max] + " forekomster)\n");
        
        // List words within 10 % of max
        double min = this.count[max] * 0.1;
        
        System.out.println("\tOrd som forekommer minst 10 % av " + this.count[max] + ":");
        
        for (int i = 0; i < this.count.length; i++)
            if (this.count[i] > min)
                System.out.println("\t > Vanlig ord: " + this.words[i] + " (" + this.count[i] + " forekomster)");
    }
    
    // Check if string in array, add to counter
    boolean inArray(String word) {
        for (int i = 0; i < this.words.length; i++) {
            if (this.words[i] != null) {
                if (this.words[i].equalsIgnoreCase(word)) {
                    // Add to counter
                    this.count[i] = this.count[i] + 1;
                    // break; nodvendig her?
                    return true;
                }
            }
        }

        return false;
    }
    
    // Count words in array
    int[] countWords() {
        int[] stats = {0, 1};
        
        for (int i = 0; i < this.count.length; i++) {
            stats[0] = stats[0] + count[i];
            
            if(this.count[i] > 0)
                stats[1] = stats[1] + 1;
        }
                
        return stats;
    }
}
