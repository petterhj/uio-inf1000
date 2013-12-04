class Test {
    public static void main(String args[]) {
        int[] tallrekke = {2, 6, 6, 3, 8, 1, 3, 9, 14};
        int vanligst = flest(tallrekke);
        System.out.println(vanligst);
    }
    
    static int flest(int[] a) {
        int verdi, antall;
        int maxVerdi = 0, maxAnt = 0;

        for (int i = 1; i < a.length; i++) {
            verdi = a[i-1];
            antall = 1;

            // The rest of the array
            for (int j = i; j < a.length; j++) {
                if (a[j] == verdi) antall++;
            }
            if (antall > maxAnt) {
                maxAnt = antall;
                maxVerdi = verdi;
            }
        }

        return maxVerdi;
    }
}

