/*
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientSock implements Runnable{
    SocketInfo socketInfo;
    String name;
    Broker broker;


    ClientSock(Broker broke){
        broker = broke;
        broker.addClient(this);
    }
    public void setSocketInfo(String socket) throws IOException {
        */
/*ServerThread serverThread = new ServerThread(socket);
        serverThread.setBroker(broker);
        serverThread.start();*//*

    }
    public void run(){
        try {

            while(true) {
                System.out.println("Enter 'credit' or 'payback' (exit to exit)");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                String message = bufferedReader.readLine();
                if (message.equals("exit")) {
                    System.out.println("bye, see you next time");
                    break;
                } else {
                    String[] choice = message.split(" ");
                    if(choice[0].equals("credit")) {
                        JSONObject json = new JSONObject();
                        json.put("choice", "credit");
                        json.put("amount", Integer.parseInt(choice[1]));
                        Socket sock = new Socket("localhost", 8000);
                        OutputStream out = sock.getOutputStream();
                        InputStream in = sock.getInputStream();
                        NetworkUtils.send(out, JsonUtils.toByteArray(json));
                        byte[] responseBytes = NetworkUtils.receive(in);
                        JSONObject response = JsonUtils.fromByteArray(responseBytes);
                    //    broker.pushMessage("credit");
                        //broker.commLeader("credit");
                        //broker.loanRequest(this, Integer.parseInt(choice[1]));
                    }
                    if(choice[0].equals("payback")){
                        JSONObject json = new JSONObject();

                    }
                }
            }
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
*/
