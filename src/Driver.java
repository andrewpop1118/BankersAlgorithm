import java.util.ArrayList;


/**
 * Driver program for the Banker's Algorithm Activity.
 * 
 * @author Justin Cotner
 */

public class Driver {
	
	/** Run parameter for number of resources that the banker has */
	final private static int bankersResources = 4;
	
	/** Run parameter for the number of clients to run */
	final private static int numClients = 3;
	
	/** Run parameter representing the total claim for each client */
	final private static int clientNUnits = 3;
	
	/** 
	 * Run parameter for the number of requests that each client will make 
	 *  to the bank.
	 */
	final private static int clientNRequests = 4;
	
	/**
	 * Run parameter for the minimum number of time each client thread should 
	 * sleep after making a request.
	 */
	final private static long minSleepMillis = 1000;
	
	/**
	 * Run parameter for the maximum number of time each client should sleep
	 * after making a request.
	 */
	final private static long maxSleepMillis = 5000;
	
	/**
	 * Main method for the program.  Runs the Banker's algorithm with the 
	 * specified number of clients, resources, claims, and requests.
	 * 
	 * @param args - not used
	 */
	public static void main(String[] args){
		Banker banker = new Banker(bankersResources);
		ArrayList<Client> clients = new ArrayList<Client>();
		
		for(int i = 1; i <= numClients; i++){
			clients.add(new Client(("Client " + i), banker, clientNUnits,
					clientNRequests, minSleepMillis, maxSleepMillis));
		}
		
		for(Client client: clients){
			client.start();
		}
		
		for(Client client: clients){
			try{
				client.join();
			}
			
			catch(InterruptedException e){
				System.err.println(e);
			}
		}
	}
	
}
