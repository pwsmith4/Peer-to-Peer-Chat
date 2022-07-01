import java.util.ArrayList;

public class BankInfo {
    public String name;
    public int money;
    ArrayList<String> clients = new ArrayList<String>();


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

    public boolean addClient(String client, int amount){
        System.out.println("Bank: "+this.getName()+ " is ADDING CLIENT: "+client+" with: "+amount);
        for(int i = 0;i<clients.size();i++) {
            String[] choice = clients.get(i).split(" ");
            String clientName = choice[0];
            String amountOwed = choice[1];

            if(client.equals(clientName)){
                System.out.println("YOU CAN ONLY HAVE ONE CREDIT PER USER");
                return false;
            }
        }

        String temp = client +" "+ amount;
    //    client.setAmountOwed(amount);
        clients.add(temp);
        return true;
    }

    public void removeClient(String client){
        for(int i = 0;i<clients.size();i++) {
            String[] choice = clients.get(i).split(" ");
            String clientName = choice[0];
            if(client.equals(clientName)){
                System.out.println("Client removed from bank: "+getName());
                clients.remove(i);
            }
        }
    }

    public void setClientAmount(String client, int amount){
        System.out.println("Setting Client Amount");
        for(int i = 0;i<clients.size();i++) {
            String[] choice = clients.get(i).split(" ");
            String clientName = choice[0];
            String amountOwed = choice[1];

            if(client.equals(clientName)){
               // amount = Integer.parseInt(amountOwed) - amount;
                System.out.println("Setting to: " + (clientName + " " + amount));
                clients.set(i,(clientName + " " + amount));
               // clients.get(i)
            }
        }
    }

    public ArrayList<String> getClients(){
        return clients;
    }

    public void addMoney(int add){
        this.money = this.money + add;
    }

    public void removeMoney(int remove){
        this.money = this.money - remove;
    }
}
