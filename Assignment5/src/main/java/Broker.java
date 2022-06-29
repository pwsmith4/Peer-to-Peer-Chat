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

    public String getSocket(){
        return socketInfo;
    }

    public void addBank(BankInfo bank){
        banks.add(bank);
        System.out.println("Bank Added");
        b++;
    }

    public void addClient(ClientInfo client){
        clients.add(client);
        System.out.println("Client Added");
        c++;
    }

    public ArrayList<ClientInfo> getClients(){
        return clients;
    }

    public String getBanks(){
        String allBanks = "";
        for (int i = 0;i<banks.size();i++) {
            allBanks = allBanks + banks.get(i).getName() + ",";
        }
        return allBanks;
    }

   public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    public boolean loanRequest(ClientInfo client, int amount){
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

    public boolean acceptLoanRequest(ClientInfo client, int amount){
        System.out.println("Accepted Loan Request");
        System.out.println("Client: " + client.getName());
        int numBanks = 0;

        for (int i = 0;i<banks.size();i++) {
            if(banks.get(i).getMoney() > (amount * 1.5)){
                numBanks++;
                banks.get(i).addClient(client);
            }
        }
        for (int i = 0;i<banks.size();i++) {
            if(banks.get(i).getMoney() > (amount * 1.5)){
                banks.get(i).removeMoney(amount/numBanks);
                System.out.println("Bank: " + banks.get(i).getName() + ", now has " + banks.get(i).getMoney());
            }
        }
        return false;
    }

}
