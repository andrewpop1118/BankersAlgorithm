/**
 * The Banker class handles and simulates resource allocation for each thread
 * that requests a claim.  Deadlock is avoided in this class through the use
 * of the Banker's Algorithm.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Banker {
	private int nUnitsOnHand;
	private int nUnitsTotal;
	private HashMap<Thread,int[]> allocation; //May need to change- int[] = [currentAlloc, remainingAlloc]
	
	private class AllocCompare implements Comparator<int []> {

		@Override
		public int compare(int[] o1, int[] o2) {
			if(o1.length < 2 || o2.length < 2) {
				System.err.println("Error - Comparing Invalid Allocation Arrays");
				System.exit(1);
			}
			
			if(o1[1] == o2[1]) {
				return 0;
			} else if (o1[1] > o2[1]) {
				return 1;
			} else {
				return -1;
			}
		}
		
		
	}
	
	public Banker(int nUnits) {
		this.nUnitsOnHand = nUnits;
		this.nUnitsTotal = nUnits;
		this.allocation = new HashMap<Thread, int[]>();
	}
	
	public void setClaim(int nUnits) {
		
		Thread claimingThread = Thread.currentThread();
		
		//Prevent race conditions for threads attempting to access
		//the allocation map at the same time
		synchronized(this) {
			if(allocation.get(claimingThread) != null ||
			   nUnits < 0 ||
			   nUnits > this.nUnitsTotal) {
				System.exit(1); //Terminate abnormally in these cases
			}
			
			int[] allocationArray = {0, nUnits};
			allocation.put(claimingThread, allocationArray);
			
			System.out.println("Thread "+claimingThread.getName()+
					" sets a claim for "+nUnits+" units.");
		}
	}
	
	public synchronized boolean request(int nUnits) {
		Thread requestingThread = Thread.currentThread();
		
		if(allocation.get(requestingThread) == null ||
           nUnits < 0 ||
           nUnits > allocation.get(requestingThread)[1]) {
			System.exit(1);
		}
		
		System.out.println("Thread " + requestingThread.getName() + " requests "+nUnits+" units.");
		
		boolean safe = isSafeState();
		
		if(safe && nUnits <= this.nUnitsOnHand){
			allocation.get(requestingThread)[0] += nUnits;
			allocation.get(requestingThread)[1] -= nUnits;
			this.nUnitsOnHand -= nUnits;
			System.out.println("Thread "+ requestingThread.getName() +" has "+ allocation.get(requestingThread)[0] +" units allocated.");
		} else {
			while(!(isSafeState()) || nUnits > this.nUnitsOnHand){
				try {
					System.out.println("Thread "+ requestingThread.getName() +" waits.");
					this.wait();
					System.out.println("Thread "+ requestingThread.getName() +" awakened.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			//The Allocation results in a safe state here
			
			allocation.get(requestingThread)[0] += nUnits;
			allocation.get(requestingThread)[1] -= nUnits;
			this.nUnitsOnHand -= nUnits;
			System.out.println("Thread "+ requestingThread.getName() +" has "+ allocation.get(requestingThread)[0] +" units allocated.");
			
		}
		return true;
	}
	
	public void release(int nUnits) {
		Thread requestingThread = Thread.currentThread();
		
		if(allocation.get(requestingThread) == null ||
           nUnits < 0 ||
           nUnits > allocation.get(requestingThread)[0]) {
			System.exit(1);
		}
		synchronized(this){
			System.out.println("Thread "+ requestingThread.getName() +" releases "+ nUnits +" units.");
			allocation.get(requestingThread)[0] -= nUnits;
			allocation.get(requestingThread)[1] += nUnits;
			this.nUnitsOnHand+=nUnits;
			this.notifyAll();
		}
		
	}
	
	public int allocated() {
		return allocation.get(Thread.currentThread())[0];
	}
	
	public int remaining() {
		return allocation.get(Thread.currentThread())[1];
	}
	
	private boolean isSafeState(){
		//We're reading the allocation map here so we want to lock
		//the banker so that no race conditions occur
		synchronized(this){
			int nUnitsOnHand = this.nUnitsOnHand;
			
			ArrayList<int[]> allocationPairs = new ArrayList<int[]>(allocation.values());
		
			Collections.sort(allocationPairs, new AllocCompare());
			
			for(int i = 0; i < allocationPairs.size(); i++) {
				if(allocationPairs.get(i)[1] > nUnitsOnHand) {
					return false;
				}
				nUnitsOnHand += allocationPairs.get(i)[0];
			}
		}
		return true;
	}
}
