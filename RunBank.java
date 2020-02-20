import java.util.Scanner;
public class RunBank{
    private static Scanner s = new Scanner(System.in);
    public static void main(String[] args) {
        int numberOfResources = s.nextInt();   //read input for number of
        int numberOfCustomers = s.nextInt();   //resouces and customers
        int[] availableM;
        int[][] maximumM;
        if(numberOfResources < 1||numberOfCustomers<1||numberOfResources>10||numberOfCustomers>10){
            System.out.println("Error, invalid input, values must be 1 - 10");//input validation
        }
        else{
            availableM = new int[numberOfResources];
            for(int i = 0; i < numberOfResources; i++){
                availableM[i]= (int)((Math.random()*8)+1); //random from 1 through 9
            }
            maximumM = new int[numberOfCustomers][numberOfResources];

            for(int i=0;i<numberOfCustomers;i++){
                for(int j=0;j<numberOfResources;j++){
                    maximumM[i][j]= (int)(Math.random()* availableM[j]);//random from 0 through available

                }
            }
            //construct bank
            Bank bank = new ImplBank(maximumM, availableM,numberOfResources,numberOfCustomers);
            bank.initial();   //show initial resouces and max
            Thread[] customers = new Thread[numberOfCustomers];
            for(int i =0; i< numberOfCustomers;i++){   //construct customers
                customers[i] = new Thread(new Customer(i, maximumM, bank));
            }
            for (Thread thread : customers) {
                thread.start();       //start all customer threads
            }
            try {
                for (Thread thread : customers) {
                    thread.join();        //return customer threads
                }
            }catch (InterruptedException e) {
                return;
            }
            bank.finalState();//show ending resorces and allocation
        }
    }
}