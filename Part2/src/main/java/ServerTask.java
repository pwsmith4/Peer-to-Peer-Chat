import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.*;

/**
 * This is the class that handles communication with a peer/client that has connected to use
 * and wants something from us
 * 
 */

public class ServerTask extends Thread {
	private BufferedReader bufferedReader;
	private Peer peer = null; // so we have access to the peer that belongs to that thread
	private PrintWriter out = null;
	private Socket socket = null;
	
	// Init with socket that is opened and the peer
	public ServerTask(Socket socket, Peer peer) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		this.peer = peer;
		this.socket = socket;
	}
	
	// basically wait for an input, right now we can only handle a join request
	// and a message
	// More requests will be needed to make everything work
	// You can enhance this or totally change it, up to you. 
	// I used simple JSON here, you can use your own protocol, use protobuf, anything you want
	// in here this is not done especially pretty, I just use a PrintWriter and BufferedReader for simplicity
	public synchronized void run() {
		while (true) {
			try {
			    JSONObject json = new JSONObject(bufferedReader.readLine());
				if(json.get("type").equals("loanAccept")){

					System.out.println("Your Loan Request was Accepted");
				}
				if(json.get("type").equals("loanDenied")){
					System.out.println("Your Loan Request was Denied");
				}
					if(json.get("type").equals("bank")) {
						BankInfo bankInfo = new BankInfo((json.get("username").toString()), (int)json.get("amount"));
						peer.getBroker().addBank(bankInfo);
							}
					if(json.get("type").equals("id")){
					ClientInfo clientInfo = new ClientInfo((String) json.get("username"));
					peer.getBroker().addClient(clientInfo);
				}

				if(json.getString("type").equals("credit")){
					System.out.println("Credit Amount: " + json.get("amount"));
					System.out.println("Banks: " + peer.getBroker().getBanks());
					ArrayList<ClientInfo> clients = peer.getBroker().getClients();
					ClientInfo client = null;
					for (int i = 0;i<clients.size();i++) {
						if(((String) json.get("username")).equals(clients.get(i).getName())){
							client = clients.get(i);
						}
					}
					Boolean request = peer.getBroker().loanRequest(client, (int)json.get("amount"));
					if(request == true){
						if(peer.getBroker().acceptLoanRequest(client, (int)json.get("amount"), peer) == true) {
							peer.pushMessage("{'type': 'loanAccept', 'username': "+ (String)json.get("username") + "}");
						}
					}else {
						peer.pushMessage("{'type': 'loanDenied', 'username': "+ (String)json.get("username") + "}");
					}
				}
				if(json.getString("type").equals("error")){
					System.out.println("Cannot payback more money than is owed.");
				}
				if(json.getString("type").equals("oneCredit")){
					System.out.println("Each user can only have one credit at a time.");
				}
				if(json.getString("type").equals("payingThisBank")){
					System.out.println("Bank: "+json.get("bankName")+", now has $"+json.get("bankAmount"));
				}
				if(json.getString("type").equals("payingBack")){
					ArrayList<ClientInfo> clients = peer.getBroker().getClients();
					ClientInfo client = null;
					for (int i = 0;i<clients.size();i++) {
						if(((String) json.get("username")).equals(clients.get(i).getName())){
							client = clients.get(i);
							client.amountOwed((String)json.get("amountOwed"));
						}
					}

					System.out.println("You now owe: $" + json.get("amountOwed"));
				}
				if(json.getString("type").equals("payback")){
					System.out.println("Payback Amount: " + json.get("amount"));
					ArrayList<ClientInfo> clients = peer.getBroker().getClients();
					ClientInfo client = null;
					for (int i = 0;i<clients.size();i++) {
						if(((String) json.get("username")).equals(clients.get(i).getName())){
							client = clients.get(i);
						}
					}
					peer.getBroker().payback(client, (int)json.get("amount"), peer);
				}

			    if (json.getString("type").equals("join")){
			    	//System.out.println("     " + json); // just to show the json

			    	//System.out.println("     " + json.getString("username") + " wants to join the network");
			    	peer.updateListenToPeers(json.getString("ip") + ":" + json.getInt("port"));
			    	out.println(("{'type': 'join', 'list': '"+ peer.getPeers() +"'}"));

			    	if (peer.isLeader()){
			    		peer.pushMessage(json.toString());
			    	}
			    	// TODO: should make sure that all peers that the leader knows about also get the info about the new peer joining
			    	// so they can add that peer to the list
			    } else {
			    	System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
			    }
			    
			    
			} catch (Exception e) {
				interrupt();
				break;
			}
		}
	}

}
