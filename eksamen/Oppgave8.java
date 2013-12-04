class Test {
    public static void main(String args[]) {
    
        int[] antallPizzaer = new int[365];
        
        lagStatistikk(antallPizzaer);
    }
    
    static void lagStatistikk(int antall[]) {
        // Header
        System.out.println("Salgsstaistikk for PizzaKing");
        System.out.println("-----------------------------------");
        
        String[] days = {"Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag"};
        
        // Each weekday
        for (int d = 0; d < days.length; d++) {
            double total = 0.0;
            int count = 0;
            
            // Each week
            for (int w = d; w < antall.length; w+=7) {
                total += antall[w];
                count++;
            }
            
            double snitt = (total / count);
            
            System.out.print(days[d] + ": ");
            System.out.printf("%.2f", snitt);
            System.out.println(" pizzaer solgt i gjennomsnitt (totalt = " + total + ")");
        }
    }
}
