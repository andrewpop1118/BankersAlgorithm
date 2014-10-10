import java.util.ArrayList;


/**
 * 
 * @author Justin Cotner
 *
 */

public class Driver {
	
	final private static int totalUnits = 5;
	final private static int numClients = 2;
	final private static int clientNUnits = 2;
	final private static int clientNRequests = 3;
	
	public static void main(String[] args){
		Banker banker = new Banker(totalUnits);
		ArrayList<Client> clients = new ArrayList<Client>();
		
		for(int i = 1; i < numClients; i++){
			clients.add(new Client(("Client " + i), banker, clientNUnits, clientNRequests, (long)1000, (long)3000));
		}
		
		for(Client client: clients){
			client.start();
		}
	}
	
}
