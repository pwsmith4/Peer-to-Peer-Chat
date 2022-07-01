import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Broker{
    String socketInfo;
    int port;
    String host;
    ArrayList<BankInfo> banks = new ArrayList<BankInfo>();
    ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
    int c = 0;
    int b = 0;

    Broker(){
    }

    public synchronized String getSocket(){
        return socketInfo;
    }

    public synchronized void addBank(BankInfo bank){
        banks.add(bank);
        System.out.println("Bank Added");
        b++;
    }

    public synchronized void addClient(ClientInfo client){
        clients.add(client);
        System.out.println("Client Added");
        c++;
    }

    public synchronized ArrayList<ClientInfo> getClients(){
        return clients;
    }

    public synchronized String getBanks(){
        String allBanks = "";
        for (int i = 0;i<banks.size();i++) {
            allBanks = allBanks + banks.get(i).getName() + ",";
        }
        return allBanks;
    }

   public synchronized String getHost(){
        return host;
    }

    public synchronized int getPort(){
        return port;
    }

    public synchronized boolean loanRequest(ClientInfo client, int amount){
        System.out.println("Loan Request");
        System.out.println("Client: " + client.getName());
        int yes = 0;
        int no = 0;

        for (int i = 0;i<banks.size();i++) {
            if(banks.get(i).getMoney() > (amount * 1.5)){
                System.out.println("Bank: " + banks.get(i).getName() + ", Said yes");
                yes++;
            }else{
                System.out.println("Bank: " + banks.get(i).getName() + ", Said no");
                no++;
            }
        }
        if(yes > no){
            return true;
        }
        return false;
    }

    public synchronized boolean acceptLoanRequest(ClientInfo client, int amount, Peer peer){
        System.out.println("Accepted Loan Request");
        System.out.println("Client: " + client.getName());
        int numBanks = 0;

        for (int i = 0;i<banks.size();i++) {
            if(banks.get(i).getMoney() > (amount * 1.5)){
                numBanks++;
            }
        }
        for (int i = 0;i<banks.size();i++) {
            if(banks.get(i).getMoney() > (amount * 1.5)) {
                if (banks.get(i).addClient(client.getName(), amount / numBanks) == false) {
                    peer.pushMessage("{'type': 'oneCredit'}");
                    return false;
                } else {
                    banks.get(i).removeMoney(amount / numBanks);
                    System.out.println("Bank: " + banks.get(i).getName() + ", now has " + banks.get(i).getMoney());
                    peer.pushMessage("{'type': 'payingThisBank', 'bankName': "+banks.get(i).getName()+", 'bankAmount': " + banks.get(i).getMoney() + "}");
                }
            }
        }
        peer.pushMessage("{'type': 'payingBack', 'amountOwed': " + amount + "}");

        return true;
    }

    public synchronized void payback(ClientInfo client, int amount, Peer peer){
        boolean error = false;

        String[] choice = new String[2];
        String amountOwed ="";
        amount = amount/banks.size();
        for (int i = 0;i<banks.size();i++) {
            ArrayList<String> clients = banks.get(i).getClients();
            for(int j = 0;j<clients.size();j++) {
                choice = clients.get(0).split(" ");

            }
            String clientName = choice[0];
            amountOwed = choice[1];
            System.out.println("ClientName:" + clientName+". Amount Owed: "+amountOwed);
            if((Integer.parseInt(amountOwed) == 0) && (client.getName().equals(clientName))){
                banks.get(i).addMoney(amount);
                banks.get(i).removeClient(clientName);
                peer.pushMessage("{'type': 'payingThisBank', 'bankName': "+banks.get(i).getName()+", 'bankAmount': " + banks.get(i).getMoney() + "}");
            }else if(Integer.parseInt(amountOwed)<amount){
                peer.pushMessage("{'type': 'error'}");
                error = true;
            } else if(client.getName().equals(clientName)){
                System.out.println("Client: "+clientName+", is paying: "+amount+ ", to "+banks.get(i).getName());
                banks.get(i).addMoney(amount);

                System.out.println("Bank: "+banks.get(i).getName()+", now has $"+banks.get(i).getMoney());
                System.out.println("Client now owes: "+(Integer.parseInt(amountOwed) - amount));
                if((Integer.parseInt(amountOwed) - amount) == 0){
                    banks.get(i).removeClient(clientName);
                }
                banks.get(i).setClientAmount(clientName, (Integer.parseInt(amountOwed) - amount));
                peer.pushMessage("{'type': 'payingThisBank', 'bankName': "+banks.get(i).getName()+", 'bankAmount': " + banks.get(i).getMoney() + "}");
            }
        }
        if(banks.size() > 0 && error == false) {
            peer.pushMessage("{'type': 'payingBack', 'amountOwed': " + (Integer.parseInt(amountOwed) - amount)*2 + ", 'username' " + peer.getUsername() + "}");
        }
    }

}
