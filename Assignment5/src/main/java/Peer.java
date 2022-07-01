import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.json.*;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and host:port for the peer and host:port of the initial leader
 * This Peer is basically the client of the application, while the server (the one listening and waiting for requests)
 * is in a separate thread ServerThread
 * In here you should handle the user input and then send it to the server of annother peer or anything that needs to be done on the client side
 * YOU CAN MAKE ANY CHANGES YOU LIKE: this is a very basic implementation you can use to get started
 * 
 */

public class Peer {
	private boolean firstTime = true;
	private String username;
	public static int clientMoney = 0;
	public static int bankMoney = 0;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	private static Broker leader;
	private static int id;
	public static Broker broker;

	private Set<SocketInfo> peers = new HashSet<SocketInfo>();
	private Array peersTest;
	private boolean isLeader = false;
	private static boolean isClient = false;
	private static boolean isBank = false;
	private static SocketInfo leaderSocket;

	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}

	public synchronized void setLeader(boolean isLeader, SocketInfo leaderSocket){
		this.isLeader = isLeader;
		this.leaderSocket = leaderSocket;
	}
	public synchronized boolean isLeader(){
		return isLeader;
	}
	public synchronized void setIsBank(){
		isBank = true;
	}

	public synchronized void setID(int givenId){
		id = givenId;
	}

	public synchronized int getId(){
		return id;
	}

	public synchronized Broker getBroker(){
		return broker;
	}

	public synchronized void setBroker(Broker broker){
		this.broker=broker;
	}

	public synchronized void addPeer(SocketInfo si){
		peers.add(si);
	}
	
	// get a string of all peers that this peer knows
	public synchronized String getPeers(){
		String s = "";
		for (SocketInfo p: peers){
			s = s +  p.getHost() + ":" + p.getPort() + " ";
		}
		return s; 
	}

	/**
	 * Adds all the peers in the list to the peers list
	 * Only adds it if it is not the currect peer (self)
	 *
	 * @param list String of peers in the format "host1:port1 host2:port2"
	 */
	public synchronized void updateListenToPeers(String list) throws Exception {
		String[] peerList = list.split(" ");
		for (String p: peerList){
			String[] hostPort = p.split(":");

			// basic check to not add ourself, since then we would send every message to ourself as well (but maybe you want that, then you can remove this)
			if ((hostPort[0].equals("localhost") || hostPort[0].equals(serverThread.getHost())) && Integer.valueOf(hostPort[1]) == serverThread.getPort()){
				continue;
			}
			SocketInfo s = new SocketInfo(hostPort[0], Integer.valueOf(hostPort[1]));
			peers.add(s);
		}
	}
	
	/**
	 * Client waits for user to input can either exit or send a message
	 */
	public void askForInput() throws Exception {
		try {

			while(true) {


				if (!isLeader && !isBank) {
					if (firstTime) {
						System.out.println("Enter Client Name: ");
					} else {
						System.out.println("Enter 'credit' or 'payback' with amount. Ex. 'credit 500'(exit to exit)");
					}
				}

					if (firstTime) {
						String username = bufferedReader.readLine();
						this.username = username;
					pushMessage("{'type': 'id', 'username': '" + username + "', 'amount': '" + clientMoney + "'}");
					firstTime = false;
				}else {
						String message = bufferedReader.readLine();
					if (message.equals("exit")) {
						System.out.println("bye, see you next time");
						break;
					} else {
						String[] choice = message.split(" ");
						if (choice[0].equals("credit")) {

							pushMessage("{'type': 'credit', 'username': '" + username + "','message':'" + choice[0] + " " + choice[1] + "', 'amount': " + choice[1] + "}");
						}
						if (choice[0].equals("payback")) {

							pushMessage("{'type': 'payback', 'username': '" + username + "','message':'" + choice[0] + " " + choice[1] + "', 'amount': " + choice[1] + "}");
						}
					}

			}
			}
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

// ####### You can consider moving the two methods below into a separate class to handle communication
	// if you like (they would need to be adapted some of course)


	/**
	 * Send a message only to the leader 
	 *
	 * @param message String that peer wants to send to the leader node
	 * this might be an interesting point to check if one cannot connect that a leader election is needed
	 */

	public synchronized void commLeader(String message) {
		try {
			BufferedReader reader = null; 
				Socket socket = null;
				try {
					socket = new Socket("localhost", 8000);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
				} catch (Exception c) {
					if (socket != null) {
						socket.close();
					} else {
						System.out.println("Could not connect to " + leader.getHost() + ":" + leader.getPort());
					}
					return; // returning since we cannot connect or something goes wrong the rest will not work. 
				}

				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(message);

				JSONObject json = new JSONObject(reader.readLine());
			//	System.out.println("     Received from server " + json);
				String list = json.getString("list");
				updateListenToPeers(list); // when we get a list of all other peers that the leader knows we update them

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

/**
	 * Send a message to every peer in the peers list, if a peer cannot be reached remove it from list
	 *
	 * @param message String that peer wants to send to other peers
	 */
	public synchronized void pushMessage(String message) {
		try {
			//System.out.println("     Trying to send to peers: " + peers.size());

			Set<SocketInfo> toRemove = new HashSet<SocketInfo>();
			BufferedReader reader = null; 
			int counter = 0;
			for (SocketInfo s : peers) {
			//	System.out.println("Peers: " + getPeers());
				Socket socket = null;
				try {
					socket = new Socket(s.getHost(), s.getPort());
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (Exception c) {
					if (socket != null) {
						socket.close();
					} else {
						System.out.println("  Could not connect to " + s.getHost() + ":" + s.getPort());
						System.out.println("  Removing that socketInfo from list");
						toRemove.add(s);
						continue;
					}
					System.out.println("     Issue: " + c);
				}

				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			//	System.out.println("Sending: " + message);

				out.println(message);
				counter++;
				socket.close();
		     }
		    for (SocketInfo s: toRemove){
		    	peers.remove(s);
		    }

		//    System.out.println("     Message was sent to " + counter + " peers");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getUsername(){
		return username;
	}

	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		String[] hostPort = args[2].split(":");
		SocketInfo s = new SocketInfo(hostPort[0], Integer.valueOf(hostPort[1]));
		//Broker broker = new Broker(args[2]);
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);
		ServerThread serverThread = new ServerThread(args[1]);
		Peer peer = new Peer(bufferedReader, username, serverThread);

		if(args[3].equals("true")) {
			System.out.println("Broker Run");
			peer.setLeader(true, s);
			Broker broker = new Broker();
			peer.setBroker(broker);
		}
		if(args[5].equals("true")) {
			System.out.println("Client Run");
			peer.addPeer(s);
			peer.setLeader(false, s);
			System.out.println("Client Money: " + Integer.parseInt(args[4]));
			clientMoney = Integer.parseInt(args[4]);
			peer.commLeader("{'type': 'join', 'username': '" + username + "','ip':'" + serverThread.getHost() + "','port':'" + serverThread.getPort() + "'}");
		}
		if(args[6].equals("true")){
			System.out.println("Bank Run");
			peer.setIsBank();
			peer.addPeer(s);
			peer.setLeader(false, s);
			System.out.println("Bank Money: " + Integer.parseInt(args[4]));
			bankMoney = Integer.parseInt(args[4]);
			peer.commLeader("{'type': 'join', 'username': '" + username + "','ip':'" + serverThread.getHost() + "','port':'" + serverThread.getPort() + "'}");
			peer.pushMessage("{'type': 'bank', 'username': " + args[0] + ",'amount':" + bankMoney + "}");
		}
		serverThread.setPeer(peer);
		serverThread.start();
		if(!isBank) {
			peer.askForInput();
		}
		//ServerThread serverThread = new ServerThread(args[1]);
		//Peer peer = new Peer(bufferedReader, username, serverThread);


		/*if (size == 4 || size == 6) {
			System.out.println("Started peer");
        } else {
            System.out.println("Expected: <name(String)> <peer(String)> <leader(String)> <isLeader(bool-String)>");
            System.exit(0);
        }

		if(size == 5){
			System.out.println("You have $" + args[4] + ".");
		}*/
     /*   System.out.println("Arguments: " + args[0] + " " + args[1]);
			if (args[5].equals("true")){
				isClient = true;
			}else{
				isClient = false;
			}
*/



      /*  String[] hostPort = args[2].split(":");
        SocketInfo s = new SocketInfo(hostPort[0], Integer.valueOf(hostPort[1]));
        System.out.println(args[3]);*/
       /* if (args[3].equals("true")){
			System.out.println("Is leader");
			leader = new Leader(s);
			peer.setLeader(true);*/
		//	peer.setLeader(true, s);

		/*} else {
			System.out.println("Pawn: " + args[4]);

			// add leader to list
			peer.addPeer(s, Integer.parseInt(args[4]));
			peer.setLeader(false);


			// send message to leader that we want to join
			peer.commLeader("{'type': 'join', 'username': '"+ username +"','ip':'" + serverThread.getHost() + "','port':'" + serverThread.getPort() + "'}");

		}*/

		//peer.askForInput();

	}
	
}
