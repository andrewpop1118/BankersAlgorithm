import java.util.ArrayList;


/**
 * 
 * @author Justin Cotner
 *
 */

public class Driver {
	
	final private static int bankersResources = 4;
	final private static int numClients = 3;
	final private static int clientNUnits = 3;
	final private static int clientNRequests = 4;
	final private static long minSleepMillis = 1000;
	final private static long maxSleepMillis = 5000;
	
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
