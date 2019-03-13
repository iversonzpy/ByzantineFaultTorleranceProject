package bftsmart.demo.ycsbapp.client;


import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YCSBTransactions {
    String ptid;
    ByteArrayOutputStream output;
    private ServiceProxy proxy;

    public YCSBTransactions(ServiceProxy proxy, String ptid) {
        this.ptid = ptid;
        this.proxy = proxy;
    }

    public byte[] readall(String table) throws IOException {
        //default read all
        YCSBMessage request = YCSBMessage.newReadAllRequest(table);

        byte[] reply = proxy.invokeUnordered(request.getBytes());

        return reply;
        //return read(table, "", new HashSet<>(), new HashMap<>());
    }

    public byte[] read(String table, String key, Set<String> fields, HashMap<String, byte[]> results) {

        YCSBMessage request = YCSBMessage.newReadRequest(table, key, fields, results, ptid);

        //Logger.println(request.toString());


        byte[] reply;
        try {
            reply = proxy.invokeUnordered(request.getBytes());
        } catch (RuntimeException ex) {
            reply = "".getBytes();
            //System.out.println("Error occurred in read(). " + ex.getMessage());
        }
        return reply;
    }

    public byte[] write( String table, String key, HashMap<String, byte[]> map) {

        YCSBMessage msg = YCSBMessage.newUpdateRequest(table, key, map, ptid);
        byte[] reply;
        try {
            reply = proxy.invokeOrdered(msg.getBytes());
        } catch (RuntimeException ex) {
            reply = "0".getBytes();
            //System.out.println("Error occurred in write(). " + ex.getMessage());
        }
        return reply;
    }

    public byte[] commit() {
        int result = 0;
        HashMap<String, byte[]> results = new HashMap<>();
        YCSBMessage msg = YCSBMessage.newCommitRequest("", "", null, results, ptid);
        byte[] reply = null;
        try {
            reply = proxy.invokeOrdered(msg.getBytes());
        } catch (RuntimeException ex) {
            //System.out.println("Error occurred in commit(). " + ex.getMessage());
        }
        return reply;
    }
}


