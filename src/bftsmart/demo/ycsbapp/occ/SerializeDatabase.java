package bftsmart.demo.ycsbapp.occ;

import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.demo.ycsbapp.database.YCSBTable;
import bftsmart.demo.ycsbapp.database.YCSBTableOperation;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.tom.util.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SerializeDatabase extends DefaultSingleRecoverable {

    private YCSBTable localDB;
    public YCSBTableOperation dbOperator;
    private OptConcurrencyControl optConcurrencyControl;

    private String tableName = "";

    public SerializeDatabase(int id) {

        this.localDB = new YCSBTable();
        dbOperator = new YCSBTableOperation(this.localDB);
        //initialTestDB();
        initialYCSBTestDB();
        new ServiceReplica(id, this, this);

        optConcurrencyControl = new OptConcurrencyControl(this);
    }

    public void initialTestDB() {

        tableName = "Black_Friday_Sales";

        if(tableName != null || !tableName.isEmpty()){
            localDB.addTable(tableName, new HashMap<String, byte[]>());
        }

        dbOperator.writeLocalDatabase(tableName, "A", "1000");
        dbOperator.writeLocalDatabase(tableName, "B", "500");
        dbOperator.writeLocalDatabase(tableName, "C", "300");

    }

    public void initialYCSBTestDB(){
        this.tableName = "usertable";
        String field = "field0";
        String value = "1";
        HashMap<String, byte[]> values = new HashMap<>();
        values.put(field, value.getBytes());
        this.localDB.addTable(tableName, values);
        printDBData();
    }

    public static void main(String[] args){
        if(args.length < 1) {
            //Logger.println("Use: java SerializeDatabase <processId>");
            System.exit(-1);
        }
        new SerializeDatabase(Integer.parseInt(args[0]));
    }


    @Override
    public void installSnapshot(byte[] state) {
        try {
            // serialize to byte array and return
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            localDB = (YCSBTable) in.readObject();
            in.close();
            bis.close();

        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream mybos = new ByteArrayOutputStream();
            ObjectOutput ycsbout = new ObjectOutputStream(mybos);
            ycsbout.writeObject(localDB);
            ycsbout.flush();
            mybos.flush();
            ycsbout.close();
            mybos.close();
            return mybos.toByteArray();
        } catch (IOException ex) {
            //Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {

        YCSBMessage aRequest = YCSBMessage.getObject(command);
        YCSBMessage reply = YCSBMessage.newUpdateResponse(0);
        switch (aRequest.getType()) {
            case WRITE: {
                boolean executeResult = optConcurrencyControl.executeWriteOperation(aRequest);
                if(executeResult){
                    // 1 - success 0 - failed
                    reply = YCSBMessage.newUpdateResponse(1);
                }
                break;
            }
            case COMMIT: {
                boolean executeResult = optConcurrencyControl.executeCommitOperation(aRequest);
                if(executeResult){
                    reply = YCSBMessage.newUpdateResponse(1);
                    //System.out.println("Transaction submitted");
                }

                break;
            }
            default:
                //System.out.println("No transaction submitted");
        }

        return reply.getBytes();
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        //Logger.println("###################################");
        //Logger.println("Enter appExecuteUnordered");
        //Logger.println("command ==== " + new String(command));

        YCSBMessage aRequest = YCSBMessage.getObject(command);
        if(aRequest == null){
            //System.out.println("YCSBMessage is NULL");
            return new byte[1];

        }else{
            //System.out.println("YCSBMessage in Server:  " + aRequest.toString());
        }


        String replyStr = "";
        switch (aRequest.getType()) {
            case READ: { //
                replyStr = optConcurrencyControl.executeReadOperation(aRequest);
                break;

            }
            case READALL:{
                printDBData();
                break;
            }

        }

        return replyStr.getBytes();
    }

    public String readLocalDatabase(String key){

        //Logger.println("------------------readLocalDatabase");
        //Logger.println("Para KEY:" + key);

        byte[] reply = this.localDB.getEntry(tableName, key);
        return reply == null ? "" : new String(reply);
    }

    public void writeLocalDatabase(String key, String value){
        //Logger.println("------------------ Write Local Database ------------------");
        //Logger.println("key:" + key + ", Value:" + value);
        this.localDB.addData(tableName,key, value.getBytes());
    }

    public Map<String, byte[]> getTableData(){
        return this.localDB.getTable(tableName);
    }

    public void printDBData(){
        System.out.println("-----------------");

        Map<String,byte[]> table = this.localDB.getTable(tableName);
        for(String key : table.keySet())
        {
            //System.out.println("------------------ Map data: ------------------");
            //System.out.println("Key, value; " +  key + ", " + new String(table.get(key)));
            //System.out.println("\tPRODUCT ID\t INVENTORY");

            //print all data


            System.out.println("\t"+ key + "\t" + new String(table.get(key)));
        }

    }




}
