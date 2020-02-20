import java.util.Arrays;
public class ImplBank implements Bank {
    private int numberOfRequests; // the number of resource requests
    private int numberOfCustomers; // The number of Customers
    private int[] available;
    private int[][] maximum;
    private int[][] allocation;

    public ImplBank(int[][] max,int[] avail,int numberOfResources,int numberOfCustomers) {
        this.available = avail;
        this.maximum = max;
        this.numberOfCustomers = numberOfCustomers;
        allocation = new int[numberOfCustomers][numberOfResources];
        for(int i=0;i<numberOfCustomers;i++){
            for(int j=0;j<numberOfResources;j++){
                allocation[i][j] = 0;   //allocation starts with zero

            }
        }
    }

    @Override
    public void initial(){           //display initial values
        System.out.print("\nBANK - Initial Resources Available:\n[ ");
        for (int i = 0; i < available.length; i++) {
            System.out.print(available[i] + ", ");
        }
        System.out.print("]\nBANK - Max:\n");
        for (int i = 0; i < maximum.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < maximum[i].length; j++) {
                System.out.print(maximum[i][j]+ ", ");
            }
            System.out.print("]\n");
        }
    }


    @Override
    public void getAllocState() {    //display current values
        System.out.print("BANK - Allocation Matrix:\n");
        for (int i = 0; i < allocation.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < allocation[i].length; j++) {
                System.out.print(allocation[i][j]+ ", ");
            }
            System.out.print("]\n");
        }

    }

    public void finalState(){          //display final values
        System.out.print("Final Available Vector:\n[");
        for (int i = 0; i < available.length; i++) {
            System.out.print(available[i] + ", ");
        }
        System.out.print("]\nFinal Allocation Matrix:\n");
        for (int i = 0; i < allocation.length; i++) {
            System.out.print("[ ");
            for (int j = 0; j < allocation[i].length; j++) {
                System.out.print(allocation[i][j]+ ", ");
            }
            System.out.print("]\n");
        }
    }


    @Override
    public synchronized boolean requestResources(int custNum, int[] request) {
        System.out.print("Customer "+ custNum +" making request:\n[");
        for (int i = 0; i < request.length; i++) {
            System.out.print(request[i]+" ,");//display customer's request
        }
        System.out.println("]");

        for (int i = 0; i < request.length; i++) {
            //is request greater than available?
            if (request[i] > available[i])
                return false;//not enough resources
            //is request greater than need?
            if (request[i] > maximum[custNum][i]-allocation[custNum][i])
                return false;//should not request more than needed
        }
        //is request safe and can safe sequence be found?
        if (!safeState(custNum, request))
            return false;
        for (int i = 0; i < request.length; i++) {
            available[i] -= request[i];          //take resources
            allocation[custNum][i] += request[i];//put request in allocation
        }

        return true;
    }

    @Override
    public synchronized void holdResources(int custNum, int r){
        System.out.println("Customer " + custNum +" request " + r +" granted");
        getAllocState();//display current allocation matrix

    }

    @Override
    public synchronized void releaseResources(int custNum, int[] release) {


        for (int i = 0; i < release.length; i++) {
            available[i] += release[i];             //return resources
            allocation[custNum][i] -= release[i];   //remove allocation
        }
        notifyAll();//notify waiting threads that more resources are available
        System.out.print("Customer "+ custNum +" releasing resources:\n[");
        for (int i = 0; i < release.length; i++) {
            System.out.print(release[i]+" ,");//display returned resources
        }
        System.out.println("]");
    }
    /****************************SAFESTATE***************************
     *checks to see if the current request is safe and if a safe
     * sequence is found, if so it returns true and displays the
     *safe sequence, if the current request is not safe returns false,
     *and if the request is safe but no seafety sequence is fount, it
     *will delay the request unitl there is a safe sequence.
     *
     *parameters: customer id number and the array of resources
     * the customer thread is returning
     */
    private synchronized boolean safeState(int custNum, int[] request) {
        String safetySequence = new String();
        int[] clonedResources = available.clone();
        int[][] clonedAllocation = allocation.clone();
        // First check if any part of the request requires more resources than
        // are available (unsafe state)
        for (int i = 0; i < clonedResources.length; i++) {
            if (request[i] > clonedResources[i]) {
                return false;
            }
        }
        // If we reach this point, the first request was valid so we execute it
        // on the simulated resources
        for (int i = 0; i < clonedResources.length; i++) {
            clonedResources[i] -= request[i];
            clonedAllocation[custNum][i] += request[i];
        }
        // Create new boolean array and set all to false
        boolean[] canFinish = new boolean[numberOfCustomers];
        for (int i = 0; i < canFinish.length; i++) {
            canFinish[i] = false;    //assume not safe
        }
        // Now check if there is an order wherein other customers can still
        // finish after this one
        for (int i = 0; i < numberOfCustomers; i++) {

            // Find a customer that can finish a request. Loop through all
            // resources per customer
            for (int j = 0; j < numberOfCustomers; j++) {
                if (!canFinish[j]) {
                    for (int k = 0; k < clonedResources.length; k++) {
                        //if need <= available
                        if (!((maximum[j][k] - clonedAllocation[j][k]) > clonedResources[k])) {
                            canFinish[j] = true;    //coustomer j is safe
                            for (int l = 0; l < clonedResources.length; l++) {
                                clonedResources[l] += clonedAllocation[j][l];
                            }
                        }
                        else{           //safe sequence is not found
                            System.out.print("Bank - Safe Sequence not found\nBank:  Customer "+custNum+" must wait\n");
                            //return resources since it cannot finish
                            delayRequest(custNum, request,true);
                            notifyAll();   //notify other threads that rescources were returned
                            try{
                                wait();    //wait for more resources
                            }catch(InterruptedException e){}
                            if(requestResources(custNum, request)){
                                delayRequest(custNum, request, false);
                                return true;
                            }
                        }
                    }
                    if (canFinish[j])            //if the request is safe
                        safetySequence += (j+", ");  //add to safety sequence
                }
            }
        }                                        //display safety sequence
        System.out.print("Bank - Safe Sequence: \n[" + safetySequence+"]\n");
        // Restore the value of allocation for this thread
        for (int i = 0; i < available.length; i++) {
            clonedAllocation[custNum][i] -= request[i];
        }
        for (boolean aCanFinish : canFinish) {
            if (!aCanFinish) {
                return false;
            }
        }
        return true;
    }
    /*********************DELAYREQUEST********************
     *returns the held resources if the process doesn't have
     *enough resources to complete
     *
     *parameters: customer id number and the array of resources
     * the customer thread is returning
     */
    private void delayRequest(int custNum,int[] request, boolean key){
        for (int i = 0; i < request.length; i++) {
            if(key)
                available[i] += request[i];       //return resource
            allocation[custNum][i] -= request[i]; //remove alloction
        }
    }

}