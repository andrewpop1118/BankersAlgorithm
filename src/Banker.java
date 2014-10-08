/**
 * The Banker class handles and simulates resource allocation for each thread
 * that requests a claim.  Deadlock is avoided in this class through the use
 * of the Banker's Algorithm.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 */

import java.util.LinkedHashMap;

public class Banker {
	private int nUnits;
	private LinkedHashMap<Thread,int[]> allocation; //May need to change
	
	public Banker(int nUnits) {
		this.nUnits = nUnits;
	}
	
	public void setClaim(int nUnits) {
		
		Thread claimingThread = Thread.currentThread();
		
		//Prevent race conditions for threads attempting to make requests
		//at the same time
		synchronized(this) {
			if(allocation.get(claimingThread) != null ||
			   nUnits < 0 ||
			   nUnits < this.nUnits) {
				System.exit(1); //Terminate abnormally in these cases
			}
			
			int[] allocationArray = {0, nUnits};
			allocation.put(claimingThread, allocationArray);
			
			System.out.println("Thread "+claimingThread.getName()+
					" sets a claim for "+nUnits+" units.");
		}
	}
	
	public boolean request(int nUnits) {
		return false;
	}
	
	public void release(int nUnits) {
		
	}
	
	public int allocated() {
		return 0;
	}
	
	public int remaining() {
		return 0;
	}
}
