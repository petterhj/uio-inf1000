class Test {
    public static void main(String args[]) {
        String[] ordliste = {"Karoline", "Anders", "Camilla"};
        String[] resultat = konkatener(ordliste, " er flink");
        
        for (String t : resultat) {
            System.out.println(t);
        }
    }
    
    static String[] konkatener(String[] ordliste, String hale) {
        String[] resultat = new String[ordliste.length];
        
        for (int i = 0; i < ordliste.length; i++) {
            resultat[i] = ordliste[i] + hale;
        }
        
        return resultat;
    }
    
    static String[] konkatener2(String[] ordliste, String hale) {
        for (int i = 0; i < ordliste.length; i++)
            ordliste[i] += hale;
        
        return ordliste;
    }
}
