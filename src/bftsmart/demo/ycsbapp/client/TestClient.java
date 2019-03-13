package bftsmart.demo.ycsbapp.client;

import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.tom.ServiceProxy;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import bftsmart.tom.util.Logger;
import com.yahoo.ycsb.ByteIterator;


public class TestClient {


    private static ServiceProxy proxy = null;
    private static AtomicInteger counter = new AtomicInteger();
    private static String table = "Black_Friday_Sales";

    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            System.out.println("Usage: java bftsmart.demo.client.TestClient <Client Id>");
            System.exit(-1);
        }
        String ptid, keys, values;
        int args_id = Integer.parseInt(args[0]);
        proxy = new ServiceProxy(args_id);

        Console console = System.console();
       // Scanner scanner = new Scanner(System.in);
        while(true) {

            //print all key - values in database
            ptid = args_id + "_" + counter.get(); // a specific ptid, couter will +1 after commit

            System.out.println();
            System.out.println("----------------------------");
            System.out.println("Transaction ID: " + ptid);
            System.out.println("Choose an operation to send:");
            String usercmd = console.readLine("0. READALL  1. READ  2. WRITE  3. COMMIT  4. EXIT :  ");

           // int userCmd = scanner.nextInt();

            switch(usercmd) {
                case "0":
                    readAll();
                    break;
                case "1":
                    // READ
                    keys = console.readLine("Enter the keys to read, delimited with comma(','):  ");
                    readTest(table, keys, ptid);
                    break;

                case "2":
                    keys = console.readLine("Enter the keys to write, delimited with comma(','):  ");
                    values = console.readLine("Enter the values to write, delimited with comma(','):  ");
                    writeTest(table, keys, values, ptid);
                    break;

                case "3":
                    if(commitTest(table, ptid)) {
                        counter.getAndAdd(1);
                    }
                    break;
                case "4":
                    System.exit(-1);
                default:
                    System.out.println("Please type in 1,2,3,4");
            }
        }
    }

    public static void readAll() throws IOException{
        YCSBTransactions transactions = new YCSBTransactions(proxy, "");

        transactions.readall(table);

    }

    public static void readTest(String table, String keys, String pid) throws IOException {

        //Logger.println("------------------ readTest: ------------------");
        //Logger.println("Table: " + table);
        //Logger.println("keys: " + keys); // a,b,c a
        //Logger.println("------------------ readTest End ------------------");

        String key = "";
        Set<String> keyset = new HashSet<>();
        for(String keyitem : keys.split(",")){
            if(key == ""){
                key = keyitem;
            }
            keyset.add(keyitem.trim());
        }

        HashMap<String, byte[]> results = new HashMap<String, byte[]>();


        YCSBTransactions transactions = new YCSBTransactions(proxy, pid);

        byte[] res = transactions.read(table, key, keyset, results);

        //YCSBMessage request = YCSBMessage.newReadRequest(table, key, keyset, results, pid);
        //byte[] res = proxy.invokeUnordered(request.getBytes());
        //Logger.println("Response outside: " + new String(res));
        System.out.println("Response: " + new String(res));

    }

    public static void writeTest(String table, String keys, String values, String pid) throws IOException {
        YCSBTransactions transactions = new YCSBTransactions(proxy, pid);

        String key = "";
//        Set<String> keyset = new HashSet<>();
        String[] keyitems = keys.split(",");
        String[] valueitems = values.split(",");
        HashMap<String, byte[]> map = new HashMap<>();
        for(int i = 0; i < keyitems.length; i++){
            if(key == ""){
                key = keyitems[i];
            }
//            keyset.add(keyitems[i]);
            map.put(keyitems[i],valueitems[i].getBytes());

        }


        byte[] res = transactions.write(table, key, map);

        YCSBMessage replyMsg = YCSBMessage.getObject(res);


        if(replyMsg.getResult() == 0){
            System.out.println("Response: Error occurred! Write failed!");
        }else{
            System.out.println("Write to cache successfully!");
        }
        //System.out.println("Response: " + replyMsg.getResult());
        //System.out.println("Write to cache successfully!");
    }

    public static void increaseByOneTest(String table, Set<String> keys, String pid){
        //TO-DO a+1
    }

    public static void increaseByTwoTest(String table, Set<String> keys,  String pid){
        //TO-DO b+2
    }



    public static boolean commitTest(String table, String pid) throws IOException {
        //TO_DO
        YCSBTransactions transactions = new YCSBTransactions(proxy, pid);
        byte[] res = transactions.commit();
        YCSBMessage replyMsg = YCSBMessage.getObject(res);
        if(replyMsg.getResult() == 0){
            System.out.println("Response: Error occurred! Commit failed!");
        }else{
            System.out.println("Commit to DB successfully!");
        }
        //System.out.println("Commit to DB successfully!");
        return true;
    }

    public static void readTable(String table) {
        // TO-DO
        // read all data from table
    }
}

