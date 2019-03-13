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


public class UserAppClient{


    private static ServiceProxy proxy = null;
    private static AtomicInteger counter = new AtomicInteger();
    private static String table = "Black_Friday_Sales";

    private static Logger logger = LoggerFactory.getLogger(UserAppClient.class);

    public UserAppClient(int clientId) {


//            JFrame frame = new JFrame("Client ID :" + clientId);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(500,500);
//            JButton button = new JButton("Press");
//            frame.getContentPane().add(button); // Adds Button to content pane of frame
//            frame.setVisible(true);
        printWelcomeBackground(clientId);
        logger.info("Hello World");
        logger.info("Hello World");logger.info("Hello World");


    }


    public static void printWelcomeBackground(int clientId) {

        System.out.println("---------------------------------------------");
        System.out.println("              BLACK FRIDAY SALES             ");
        System.out.println("---------------------------------------------");
        System.out.println("\tPRODUCT ID\tPRODUCT NUMBER");

        //print all data


        System.out.println("---------------------------------------------");
        System.out.println("\t\t YOUR ID: " + clientId);

    }



    public static void displayAllProducts(String table) {

        // ProductId, ProductNum
        // Map<String, byte[]>
    }

    public static boolean buyOneProduct(String productId){

        //

        //true success, false failed
        return true;
    }

    public static int buyOneProductNTimes(String productId, int n){

        int num = 0;

        //read left # of product productNum
        // update to productNum - 1;

        while(n-- > 0){

            if(buyOneProduct(productId)){
                num++;
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

    }
}


