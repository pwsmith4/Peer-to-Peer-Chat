## Purpose:
A peer-2-peer for a chat. All peers can communicate with each other without a leader Server. 

###Screencast:
https://youtu.be/1gyDlFl9HDM 

Each peer is client and server at the same time. 
When started the peer has a ServerThread in which the peer listens for potential other peers to connect.

If you want to change the leader settings
	gradle runPeer -PpeerName=Hans -Ppeer="localhost:8080" Pleader="localhost:8000" -q --console=plain

You can of course replace localhost with the IP of your AWS, Pi etc. 


### Running a Peer

Peer that is minimal with the "default" leader from above
	gradle runPeer -PpeerName=Anna -Ppeer="localhost:9000" -Pleader="localhost:8080" -q --console=plain

If you want to change settings
	gradle runPeer -PpeerName=Elsa -Ppeer="localhost:9002" -Pleader="localhost:8080" -q --console=plain

- leader: Needs to be set to a running Peer Server.

You can start as many Peers as you like they should all connect. 

I was unable to get the third Server (and beyond) to connect with all the Servers that werent passed into it. 
I needed to give it the peers list from the peer that it is connecting to, but I couldn't get it working. 



