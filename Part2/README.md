## Purpose:
A peer-2-peer for a chat. All peers can communicate with each other. 
##Screencast:
https://youtu.be/ZnPc9Mo7Oxo 

Each peer is client and server at the same time. 
When started the peer has a ServerThread in which the peer listens for potential other peers to connect.

Run the leader Server first:
	gradle runLeader

### Running a Peer

node1 is the first bank to be added to the leader
	gradle node1 

If you want to change the amount of money a bank has (Default is 1000)
	gradle node1 -Pmoney=500

For more banks to be added:
	gradle node2
	gradle node3

Runs the Client Server 
	gradle client

Attempts to run more clients:
	gradle client2
	gradle client3

Run leader first, then a bank, and the client. After that you can change 
the number of banks at any time, but changing the number of clients is
not fully functional. 




