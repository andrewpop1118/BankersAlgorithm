import java.util.Random;

/**
 * 
 * @author Justin Cotner
 *
 */

public class Client extends Thread{

	Banker banker;
	int nUnits;
	int nRequests;
	long minSleepMillis;
	long maxSleepMillis;
	
	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis){
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}
	
	public void run(){
		Random rand = new Random();	
		banker.setClaim(nUnits);
		
		for(int i = 0; i < nRequests; i++){
			if(banker.remaining() == 0){
				banker.release(banker.allocated());
			}
			
			else{
				int temp = rand.nextInt(banker.remaining());
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
