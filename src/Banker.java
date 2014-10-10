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
	/** Total number of units the bank has on hand */
	private int nUnitsOnHand;
	
	/** Total number of units the bank has total */
	private int nUnitsTotal;
	
	/** HashMap of the Thread allocation: key - Thread obj, value: 2 element array*/
	private HashMap<Thread,int[]> allocation; //int[] = [currentAlloc, remainingAlloc]
	
	/**
	 * Private comparator class to compare allocations stored in the allocation HashMap based
	 * on the remaining value.
	 * 
	 * @author Andrew Popovich (ajp7560@rot.edu)
	 *
	 */
	private class AllocCompare implements Comparator<int []> {

		/**
		 * Compares the remaining allocation between two allocations.
		 * Abnormally terminates the program if either array does not contain 2 elements.
		 * 
		 * @return comparasion int - 0 if equal, 
		 *             1 if remainAlloc of 1 > remainAlloc of 2, -1 otherwise
		 */
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
	
	/**
	 * Bank's constructor that initializes the number of units and the allocation map.
	 * 
	 * @param nUnits - total number of units
	 */
	public Banker(int nUnits) {
		this.nUnitsOnHand = nUnits;
		this.nUnitsTotal = nUnits;
		this.allocation = new HashMap<Thread, int[]>();
	}
	
	/**
	 * Allows a thread to be registered in the bank with a set amount of 
	 * resources to claim.
	 * 
	 * @param nUnits - total number of units the thread will claim
	 */
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
	
	/**
	 * Method for a thread to request some resources from the bank. The method
	 * is synchronized to prevent two clients from requesting resources at the 
	 * same time.
	 * 
	 * @param nUnits - number of units requested
	 * @return true - not used...
	 */
	public synchronized boolean request(int nUnits) {
		Thread requestingThread = Thread.currentThread();
		
		if(allocation.get(requestingThread) == null ||
           nUnits < 0 ||
           nUnits > allocation.get(requestingThread)[1]) {
			System.exit(1);
		}
		
		System.out.println("Thread " + requestingThread.getName() + " requests "+nUnits+" units.");
		
		boolean safe = isSafeState(nUnits);
		
		//If the allocation is safe and can be made with the resources on hand
		if(safe && nUnits <= this.nUnitsOnHand){
			
			allocation.get(requestingThread)[0] += nUnits;
			allocation.get(requestingThread)[1] -= nUnits;
			this.nUnitsOnHand -= nUnits;
			System.out.println("Thread "+ requestingThread.getName() +" has "+ allocation.get(requestingThread)[0] +" units allocated.");
			
		} else {
			//Wait while the allocation results in an unsafe
			while(!(isSafeState(nUnits)) || nUnits > this.nUnitsOnHand){
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
	
	/**
	 * Method to allow a thread to release some resources back to the bank. It
	 * will also wake up any threads waiting for the bank to get more resources.
	 * 
	 * @param nUnits - number of units to give back to the bank
	 */
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
	
	/**
	 * Getter for a threads current allocation.
	 */
	public int allocated() {
		return allocation.get(Thread.currentThread())[0];
	}
	
	/**
	 * Getter for a threads current remaining.
	 */
	public int remaining() {
		return allocation.get(Thread.currentThread())[1];
	}
	
	/**
	 * isSafeState performs the banker's algorithm by simulating the next
	 * allocation and determines if it will lead to an unsafe state.  In
	 * either case it will reset the bank's state.
	 * 
	 * @param request - number of units requested
	 * @return boolean - indicates that the request will result in a safe state
	 */
	private boolean isSafeState(int request){
		//We're reading the allocation map here so we want to lock
		//the banker so that no race conditions occur
		synchronized(this){
			int currentAlloc = this.allocation.get(Thread.currentThread())[0];
			int remainAlloc = this.allocation.get(Thread.currentThread())[1];
			
			
			ArrayList<int[]> allocationPairs = new ArrayList<int[]>(allocation.values());
			
			//Simulate the allocation
			int nUnitsOnHand = this.nUnitsOnHand - request;
			this.allocation.get(Thread.currentThread())[0] = currentAlloc + request;
			this.allocation.get(Thread.currentThread())[1] = remainAlloc - request;
			
			Collections.sort(allocationPairs, new AllocCompare());
	
			
			
			for(int i = 0; i < allocationPairs.size(); i++) {
				if(allocationPairs.get(i)[1] > nUnitsOnHand) {
					//Reset Allocation
					this.allocation.get(Thread.currentThread())[0] = currentAlloc;
					this.allocation.get(Thread.currentThread())[1] = remainAlloc;
					return false;
				}
				nUnitsOnHand += allocationPairs.get(i)[0];
			}
			//Reset Allocation
			this.allocation.get(Thread.currentThread())[0] = currentAlloc;
			this.allocation.get(Thread.currentThread())[1] = remainAlloc;

		}
		return true;
	}
}
