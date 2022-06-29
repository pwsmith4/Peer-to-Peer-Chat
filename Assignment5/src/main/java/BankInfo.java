import java.util.ArrayList;

public class BankInfo {
    public String name;
    public int money;
    ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();


    BankInfo(String name, int money){
        this.name = name;
        this.money = money;
    }

    public String getName(){
        return name;
    }

    public int getMoney(){
        return money;
    }

    public void addClient(ClientInfo client){
        clients.add(client);
    }

    public ArrayList<ClientInfo> getClients(){
        return clients;
    }

    public void removeMoney(int remove){
        this.money = this.money - remove;
    }
}
