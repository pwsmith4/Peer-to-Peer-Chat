import org.json.JSONObject;

public class BankSock implements Runnable {
    SocketInfo socketInfo;

    JSONObject clients = new JSONObject();

    BankSock(SocketInfo s){
        socketInfo = s;
    }
    public void run(){

    }
    public boolean sendRequest(int amount, String name){

        /*if(!bank.has(name)){
            if(Integer.parseInt((String) bank1.get("money")) > (amount * 1.5)){
                System.out.println("Bank 1 says yes");
            }
        }*/
        return false;
    }
}
