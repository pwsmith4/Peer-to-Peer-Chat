import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

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

				if(json.get("type").equals("notify")){
					System.out.println("     " + json); // just to show the json
					String s = (String)json.get("ipLead");
					int h = Integer.valueOf((String) json.get("portLead"));
					SocketInfo sock = new SocketInfo(s, h);
					peer.addPeer(sock);
					peer.updateListenToPeers(json.getString("ipLead") + ":" + json.getInt("portLead"));
					peer.pushMessage(json.toString());
				}else if (json.getString("type").equals("join")){
			    	System.out.println("     " + json); // just to show the json

			    	System.out.println("     " + json.getString("username") + " wants to join the network");
			    	peer.updateListenToPeers(json.getString("ipLead") + ":" + json.getInt("portLead"));
				//	json.put("notify", "yes");
					System.out.println("Pushing: {'type': 'notify', 'new0': '" + json.get("new0") +"', 'new1': '" + json.get("new1") + "', 'list': "+ peer.getPeers() +", 'ipLead:" + json.getString("ipLead") + ", 'portLead':" + json.getInt("portLead") +")");
				//	Peer per = new Peer(json.getString("ipLead"), (int)json.getInt("portLead"));
				//	peer.addPeer(per);
					peer.pushMessage("{'type': 'notify', 'new0': '" + json.get("new0") +"', 'new1': '" + json.get("new1") + "', 'list': "+ peer.getPeers() +", 'ipLead:" + json.getString("ipLead") + ", 'portLead':" + json.getInt("portLead") +")");

			    	/*if (peer.isLeader()){
			    		peer.pushMessage(json.toString());
			    	}*/

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
