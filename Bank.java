public interface Bank {
   /************************INITIAL************************
    *Displays the initial availble resources vetor as well
    *as the maximum request matrix before any customer threads
    *begin
    *
    *no parameters
    */
   void initial();

   /********************GETALLOCSTATE***********************
    *Displays the current contents of the allocation matrix.
    *
    *no parameters
    */

   void getAllocState();
   /************************FINALSTATE**********************
    *Displays the contents of available resources vector and
    *the allocation matrix after all the customers have left.
    *
    *no parameters
    */

   void finalState();
   /*********************REQUESTRESOURCES********************
    *the customer makes a request from the available matrix
    *and checks for a safe sequence
    *
    *parameters:customer id number and the array of resources
    * the customer thread needs
    */
   boolean requestResources(int custNum, int[] request);


   /*********************HOLDRESOURCES********************
    *shows that the customers request was granted if they are
    *holding resources
    *
    *parameters: customer id number and the array of request number
    */
   void holdResources(int custNum, int rNum);

   /*********************RELEASERESOURCES********************
    *The coustomer thread returns the resources it was using to
    *the available resources vector.
    *
    *parameters: customer id number and the array of resources
    * the customer thread is returning
    */

   void releaseResources(int custNum, int[] release);
}
               
