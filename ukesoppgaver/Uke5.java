class Uke5 {
    public static void main(String[] args) {
        Fibonacci fci = new Fibonacci();

        fci.printNumbers();
    }
}

class Fibonacci {
    int[] numbers = new int[15];

    void printNumbers() {
        numbers[0] = 0;
        numbers[1] = 1;

        for(int i = 0; i < 15; i++) {
            if(i > 1) {
                numbers[i] = (numbers[i-2] + numbers[i-1]);
            }

            System.out.print(numbers[i] + " - ");
        }

        System.out.print("\n");
    }
}
