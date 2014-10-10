import java.util.Random;

/**
 * Client class which represents a thread requesting and releasing
 * resources form the bank.
 * 
 * @author Justin Cotner
 *
 */

public class Client extends Thread{

	/** Banker object to request resources from */
	private Banker banker;
	
	/** Number of units to claim */
	private int nUnits;
	
	/** Number of requests to make */
	private int nRequests;
	
	/** Minimum number of milliseconds to sleep */
	private long minSleepMillis;
	
	/** Maximum number of milliseconds to sleep */
	private long maxSleepMillis;
	
	/**
	 * Constructor for a client thread.
	 * 
	 * @param name - Name for the thread
	 * @param banker - Banker obj to request resources from
	 * @param nUnits - Number of units to claim
	 * @param nRequests - Number of requests to make
	 * @param minSleepMillis - Minimum number of milliseconds to sleep
	 * @param maxSleepMillis - Maximum number of milliseconds to sleep
	 */
	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis){
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}
	
	/**
	 * The Client's run method that will make the specified number of requests
	 * to the banker.
	 */
	public void run(){
		Random rand = new Random();	
		banker.setClaim(nUnits);
		
		for(int i = 0; i < nRequests; i++){
			if(banker.remaining() == 0){
				banker.release(banker.allocated());
			}
			
			else{
				int temp = 1;//rand.nextInt(banker.remaining()) + 1;//+1 Prevents request for 0 units
				
				banker.request(temp);
			}
			
			int t = rand.nextInt((int)((maxSleepMillis/1000 - minSleepMillis/1000) + 1)) + (int)(minSleepMillis/1000);
			
			try{
				Thread.sleep(t * 1000);
			}
			catch(InterruptedException e){}
		}
		banker.release(banker.allocated());
		
		return;
	}
	
}
