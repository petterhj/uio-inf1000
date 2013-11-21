import java.util.*;
import easyIO.*;

class Oblig1 {
    public static void main(String[] argv) {
        // Define precipitation values
        LinkedHashMap<String, DataSet> data = new LinkedHashMap<String, DataSet>();

        data.put("Mai", new DataSet(22, 89, 0));
        data.put("Juni", new DataSet(18, 127, 0));
        data.put("Juli", new DataSet(8, 19, 81));

        Yr stats = new Yr(data);
               
        // Output formatting
        final int COL1 = 40;
        final int COL2 = 15;
         
        Out screen = new Out();

        // Print header
        screen.outln("\n+-------------------------------------------------------+");
        screen.outln("|  Nedbørstall for sommeren 2013\t\t\t|");
        screen.outln("+-------------------------------------------------------+");
        screen.outln("|\t\t\t\t\t\t\t|");
            
            // Statistics
        screen.out("| Dager med nedbør:", COL1);
        screen.outln(stats.getRainyDays() + " dager\t|", COL2, OutExp.LEFT);
        screen.out("|   > Totalt:", COL1);
        screen.outln(stats.getTotal() + " mm.\t\t|", COL2, OutExp.LEFT);
        screen.out("|   > Snitt:", COL1);
        screen.outln(Format.alignLeft(stats.getAverage(), 3, 1) + " mm.\t\t|", COL2, OutExp.LEFT);
        screen.outln("|\t\t\t\t\t\t\t|");
        screen.outln("| Mot normalt:\t\t\t\t\t\t|", COL1);
        
        for (Map.Entry<String, DataSet> d : data.entrySet()) {
            screen.out("|   > " + d.getKey(), COL1);
            
            if(d.getValue().getDeviation() != 0.0) {
                screen.outln(Format.alignLeft(d.getValue().getDeviation(), 4, 1) + " %\t\t|", COL2, OutExp.LEFT);
            }
            else {
                screen.outln("-\t\t|", COL2, OutExp.LEFT);
            }
        }
        
        screen.outln("|\t\t\t\t\t\t\t|");

        // Print footer
        screen.outln("+-------------------------------------------------------+\n");
    }
}

class Yr {
    LinkedHashMap<String, DataSet> data;

    Yr(LinkedHashMap<String, DataSet> data) {
        this.data = data;
    }

    // Return total precipitation
    int getTotal() {
        int total = 0;

        for (DataSet d : data.values()) {
            total += d.getPrecipitation();
        }

        return total;
    }
    
    // Return days with rain
    int getRainyDays() {
        int rainy_days = 0;

        for (DataSet d : data.values()) {
            rainy_days += d.getDays();
        }

        return rainy_days;
    }
    
    // Return average precipitation
    double getAverage() {
        return (this.getTotal() / (double) this.getRainyDays());
    }
}

class DataSet {
    private int days;
    private int precipitation;
    private int average;

    DataSet(int days, int precipitation, int average){
        this.days = days;
        this.precipitation = precipitation;
        this.average = average;
    }
    
    // Return days with precipitation
    int getDays() {
        return this.days;
    }
    
    // Return precipitation value
    int getPrecipitation() {
        return this.precipitation;
    }
    
    // Return average precipitation
    int getAverage() {
        return this.average;
    }
    
    // Return precipitation deviation
    double getDeviation() {
        if(this.getAverage() > 0) {
            return (((double)this.getPrecipitation() / (double)this.getAverage()) * 100);
        }
        else {
            return 0;
        }
    }
}
