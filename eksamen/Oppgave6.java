import java.util.Scanner;

class Test {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        
        int num;
        
        do {
            System.out.print("Skriv inn et positivt heltall: ");
            
            while(!in.hasNextInt()) {
                System.out.print("Ikke gyldig, prøv igjen: ");
                
                in.next();
            }
            
            num = in.nextInt();
        } while(num < 0);
        
        String ds = String.valueOf(num);
        
        for(int i = 0; i < (ds.length()); i++) {
            String[] tr = {"Null", "En", "To", "Tre", "Fire", "Fem", "Seks", "Sju", "Åtte", "Ni"};

            char x = ds.charAt(i);
            int y = Character.getNumericValue(x);
            String z = tr[y];
            
            // tr[Character.getNumericValue(ds.charAt(i))]
            
            System.out.print(" " + z);
        }
    }
}
