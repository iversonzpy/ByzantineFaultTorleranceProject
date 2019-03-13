package bftsmart.demo.ycsbapp.client;


import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.tom.ServiceProxy;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.awt.Frame;
import javax.swing.*;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.yahoo.ycsb.ByteIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Author  Pengyang
 * @Description  User Application Client for Online Shopping.
 * @Usage UserAppClient $ClientId or run scripts in ./runscripts/userclient_start.sh $ClientId
 *
 */


public class UserAppClient{


    private static ServiceProxy proxy = null;
    //private static AtomicInteger counter = new AtomicInteger();
    private static String table = "Black_Friday_Sales";
    private static AtomicInteger tcounter = new AtomicInteger();
    private static String ptid;
    private static int cliendId;
    private static boolean isSoldOut = false;

    private static Logger logger = LoggerFactory.getLogger(UserAppClient.class);

    public UserAppClient(int clientId) {
        this.cliendId = clientId;
        proxy = new ServiceProxy(clientId);
        //printWelcomeBackground(clientId);
    }


    public static void printWelcomeBackground(int clientId) {


        // Print the list objects in tabular format.
        System.out.println("----------------------------------------------------------------------");
        System.out.println("                          BLACK FRIDAY SALES                          ");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%25s %25s", "PRODUCT_ID", "INVENTORY");
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        displayAllProducts(table);
        System.out.println("----------------------------------------------------------------------");


        //print all data

        System.out.println("\t\t\t YOUR CUSTOMER ID: " + clientId);

    }

    public static void displayAllProducts(String table) {


        YCSBTransactions transactions = new YCSBTransactions(proxy, "");
        HashMap<String, String> results = transactions.readall(table);

        for (String product : results.keySet()) {
            System.out.printf("%25s %25s", product, new String(results.get(product)));
            System.out.println();
           // System.out.println("\t"+ product + "\t" + new String(results.get(product)));
        }


    }

    public static boolean buyOneProduct(String productId){

        ptid = cliendId + "_" + tcounter.getAndAdd(1);

        //System.out.println("PTID " + ptid);
        //true success, false failed
        // one read, get number
        // num - 1

        YCSBTransactions transU = new YCSBTransactions(proxy, ptid);

        int productNum = 0;

        Set<String> fields = new HashSet<>();
        fields.add(productId);
        HashMap<String, byte[]> results = new HashMap<>();
        byte[] reply = transU.read(table, productId, fields, results);


        if(reply == null){
            return false;
        }
        try {
            productNum = Integer.parseInt(new String(reply));
        } catch (NumberFormatException ex) {
            //System.out.println("Error " + ex.getMessage());
            return false;
        }

        if(productNum <= 0){
            isSoldOut = true;
            System.out.println("\t\t" + productId + " Sold Out!!!");
            return false;
        }

        // send a write and commit

        HashMap<String, byte[]> writeValues = new HashMap<>();
        writeValues.put(productId, Integer.toString(productNum - 1).getBytes());

        byte[] writeReply = transU.write(table, productId, writeValues);

        int writeres = 0;

        try {
            writeres = YCSBMessage.getObject(writeReply).getResult();
        } catch (NullPointerException ex) {
        }

        if(writeres == 0){
            return false;
        }

        //System.out.println("-----------" + commitres);
        byte[] commitReply = transU.commit();

        int commitres = 0;

         try{
             commitres = YCSBMessage.getObject(commitReply).getResult();
         }catch (NullPointerException ex){

         }

        if(commitres == 0){
            System.out.println("\u00D7 One purchase is aborted! Trying next purchase!");
            return false;
        }

        return true;
    }

    public static int buyOneProductNTimes(String productId, int n){

        int num = 0;

        //read left # of product productNum
        // update to productNum - 1;
        int maxTime = 3 * n;

        while(maxTime-- > 0 && n > 0 && !isSoldOut){

            if(buyOneProduct(productId)){
                num++;
                n--;
                System.out.println("\u2713 Successfully purchased " + num + " product!");

            }
        }
        //success num times
        return num;
    }



    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java bftsmart.demo.client.TestClient <Client Id>");
            System.exit(-1);
        }

        new UserAppClient(Integer.parseInt(args[0]));


        String keys, values;
        Console console = System.console();
        String userCmd = "1";

        while(!userCmd.equals("0")) {
            printWelcomeBackground(cliendId);
            isSoldOut = false;

            String productId = console.readLine("Type the productId you want to purchase:  ");
            String numString = console.readLine("Type the total number you want to purchase:  ");
            int num = Integer.parseInt(numString);

            int purchasedNum = buyOneProductNTimes(productId, num);

            if(purchasedNum > 0){

                System.out.println("Congratz! You got " + purchasedNum + " product " + productId);
            }else{
                System.out.println("Sorry, Products are sold out.");
            }

            System.out.println();

            userCmd = console.readLine("Enter 0 to exit, or enter any key to continue.  ");
        }

        System.exit(-1);
    }
}


