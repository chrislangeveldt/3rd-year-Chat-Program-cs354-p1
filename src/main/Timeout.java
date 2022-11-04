/** 
 * Used for thread that checks for a timeout when connection to server
 */
public class Timeout implements Runnable {

    /** 
     * Empty constructor
     */
    public Timeout() {}

    /** 
     * Execution of the thread that check for timeout
     */
    @Override
    public void run() {

        try {
            Thread.sleep(1000);
            System.out.println("Waiting to connect...");
            Thread.sleep(4000);
            System.out.println("Failed to connect");
            System.exit(0);
        } catch (InterruptedException e) {}
        
    }
    
}
