public class Customer implements Runnable {
    private int numOfResources;// the number of different resources
    private int[] maxDemand;   // the maximum this Customer will demand
    private int custNum;       // this Customer's id number
    private int[] request;     // this Customer's resource request
    private Bank bank;
    public Customer(int custNum, int[][] max, Bank bank) { //Customer constructor
        this.custNum = custNum;
        this.maxDemand = max[custNum];
        this.bank = bank;
        numOfResources = maxDemand.length;
        request = new int[numOfResources];
    }
    public synchronized void run() {
        int numOfRequests = (int)((Math.random()*2)+3); //make between 3 and 5 requests
        for (int r = 0; r<numOfRequests;r++){
            try{
                for (int i = 0; i < request.length; i++)
                    request[i] =(int) (Math.random() * maxDemand[i]);//generate request 0 to max

                if (bank.requestResources(custNum, request)) {  //make request
                    bank.holdResources(custNum,r);
                    Thread.sleep((int)(Math.random() * 5000-1000)+1000);//sleep for 1 to 5 seconds
                    bank.releaseResources(custNum, request); //return resources to bank
                }

            }catch (InterruptedException e){
            }
        }
        System.out.print("Customer "+custNum+" has left the bank\n");
    }     //cutomer is done with all requests, thread can terminate

}