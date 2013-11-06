/*
	Titteregister v1.0
*/

/* Imports */
import java.util.*;
import java.io.*;


class Titteregister {
	public static void main(String args[]) {
		// Header
        OutputHelper out = new OutputHelper();
        InputHelper in = new InputHelper();

        out.printSep();
        out.printText("Oles titteregister (v1.0)");
        out.printSep();

        // Register
        Register data = new Register();

        // Menu
        out.printMenu();

        int choice = 0;

        while (choice != 4) {
            choice = in.getMenuChoice();

            switch (choice) {
                case 1: 
                    System.out.println("* Legg til ny observasjon");
                    data.addObservation(in.getObservationData());
                    break;
                case 2:
                    System.out.println("* Hent observasjoner etter type");
                    data.getObservationsByType(in.getTextChoice("Type"));
                    break;
                case 3:
                    System.out.println("* Hent observasjoner etter sted");
                    data.getObservationsByPlace(in.getTextChoice("Sted"));
                    break;
                case 4:
                    System.out.println("* Avslutter...");
                    break;
            }
        }
	}
}


class Register {
    final String FILENAME = "fugler.txt";

    OutputHelper out;

    Register(){
        /* Output helper */
        out = new OutputHelper();
    }

    /* Print observations by type */
    void getObservationsByType(String type) {
        try {
            /* Generate table */
            out.printSep();
            out.printText("Observasjoner av " + type);
            out.printSep();

            BufferedReader data = new BufferedReader(new FileReader(FILENAME));

            String line;

            while ((line = data.readLine()) != null) {
                String[] ob = line.split(",");

                if(ob[0].equalsIgnoreCase(type)) {
                    out.printRow(ob[2], ob[1], ob[3]);
                }
            }

            data.close();

            out.printSep();

        } catch(IOException e) {
            System.out.println("! Kunne ikke lese filen...");
        }
    }

    /* Print observations by location */
    void getObservationsByPlace(String location) {
        try {
            /* Generate table */
            out.printSep();
            out.printText("Observasjoner i " + location);
            out.printSep();

            BufferedReader data = new BufferedReader(new FileReader(FILENAME));

            String line;

            while ((line = data.readLine()) != null) {
                String[] ob = line.split(",");

                if(ob[2].equalsIgnoreCase(location)) {
                    out.printRow(ob[0], ob[1], ob[3]);
                }
            }

            data.close();

            out.printSep();

        } catch(IOException e) {
            System.out.println("! Kunne ikke lese filen...");
        }
    }

    void addObservation(String[] observation) {
        /* Write observation to file */
        try {
            PrintWriter file = new PrintWriter(new FileWriter(FILENAME, true));

            file.println(observation[0] + "," + observation[1] + "," + observation[2] + "," + observation[3]);

            file.close();

            System.out.println("* Observasjon av " + observation[0] + " (" + observation[1] + ") i " + observation[2] + " (" + observation[3] + ") ble skrevet til fil!");
        } catch (IOException e) {
            System.out.println("! Kunne ikke skrive til fil");
        }
    }
}

class OutputHelper {
    /* Constants */
    final int WIDTH = 60;

    /* Output separator */
    void printSep() {
        System.out.print("+");
        for (int i = 0; i < WIDTH; i++)
            System.out.print("-");
        System.out.println("+");
    }

    /* Output text line */
    void printText(String text) {
        text = "| " + text;
        System.out.print(text);
        for(int i = 0; i < ((WIDTH + 1) - text.length()); i++) 
            System.out.print(" ");
        System.out.println("|");
    }

    /* Output table */
    void printRow(String col1, String col2, String col3) {
        String text1 = "| " + col1;
        String text2 = col2 + "      " + col3 + " |";
        System.out.print(text1);
        for(int i = 0; i < ((WIDTH + 2) - text1.length() - text2.length()); i++)
            System.out.print(" ");
        System.out.println(text2);
    }

    /* Output menu */
    void printMenu() {
        System.out.println("\n  Meny:");
        System.out.println("   [1] Registrer en fugleobservasjon");
        System.out.println("   [2] Skriv ut alle observasjoner av en fugletype");
        System.out.println("   [3] Skriv ut alle observasjonene på ett bestemt sted");
        System.out.println("   [4] Avslutt systemet");
    }
}

class InputHelper {
    Scanner input;

    InputHelper() {
        input = new Scanner(System.in);
    }

    /* Get int input, then validate */
    int getMenuChoice() {
        int choice;

        do {
            System.out.print("\n> Velg ønsket funksjon (1-4): ");
            while (!input.hasNextInt()) {
                System.out.print("> Ikke et tall, prøv igjen: ");
                input.next();
            }
            choice = input.nextInt();
        } while (choice < 0 || choice > 4);

        return choice;
    }

    /* Get string input */
    String getTextChoice(String queryText) {
        System.out.print("  > " + queryText + ": ");

        String choice = input.next();

        return choice;
    }

    /* Get observation data */
    String[] getObservationData() {
        String[] observation = new String[4];

        observation[0] = this.getTextChoice("Type");
        observation[1] = this.getTextChoice("Kjønn (H/M)");
        observation[2] = this.getTextChoice("Sted");
        observation[3] = this.getTextChoice("Dato");

        return observation;
    }
}
