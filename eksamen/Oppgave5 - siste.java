class Test {
    public static void main(String args[]) {
        int[] tallrekke = {2, 6, 6, 3, 8, 1, 3, 9, 14};
        int vanligst = flest(tallrekke);
        System.out.println(vanligst);
    }
    
    static int flest(int[] tallrekke) {
        int mostCommon = 0;
        int count = 0;
        
        for (int i : tallrekke) {
            int currentCount = 0;
            
            for (int k : tallrekke)
                if (k == i)
                    currentCount++;
            
            if (currentCount > count) {
                count = currentCount;
                mostCommon = i;
            }
        }
        
        return mostCommon;
    }
}
