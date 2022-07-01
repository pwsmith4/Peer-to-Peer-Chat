public class ClientInfo {
    public String name ="";
    public String[] banksOwed = new String[3];
    public int[] amountOwed = new int[3];
    public int j = 0;
    public int amount = 0;

    ClientInfo(String name){
        this.name = name;
      //  this.money = money;
    }

    public synchronized String getName(){
        return name;
    }

    public synchronized void setAmountOwed(int amount){
        this.amount = amount;
    }
    public synchronized int getAmount(){
        return amount;
    }
    public void amountOwed(String amount){
        System.out.println("You now owe: $" + amount);
    }

    public synchronized int owesBank(String bankName){
        for(int i = 0; i< banksOwed.length;i++) {
            banksOwed[i].equals(bankName);
            return amountOwed[i];
        }
        return 0;
    }
}
