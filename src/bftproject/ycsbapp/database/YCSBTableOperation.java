package bftproject.ycsbapp.database;

import bftproject.ycsbapp.client.YCSBTransactions;

import java.util.HashMap;
import java.util.Map;

public class YCSBTableOperation {

    private YCSBTable localDB;

    public YCSBTableOperation(YCSBTable localDB){
        this.localDB = localDB;
    }

    public void insertNewTable(String tableName) {
        this.localDB.addTable(tableName, new HashMap<String, byte[]>());
    }

    public String readLocalDatabase(String tableName, String key){

        //Logger.println("------------------ readLocalDatabase ------------------");
        //Logger.println("Para KEY:" + key);

        byte[] reply = this.localDB.getEntry(tableName, key);
        return reply == null ? "" : new String(reply);
    }

    public void writeLocalDatabase(String tableName, String key, String value){
        //Logger.println("------------------ Write to Local Database ------------------");
        //Logger.println("key:" + key + ", Value:" + value);
        this.localDB.addData(tableName,key, value.getBytes());
    }


    public void printTableData(String tableName){
        Map<String,byte[]> table = this.localDB.getTable(tableName);
        for(String key : table.keySet())
        {
            //System.out.println("------------------ Table data: ------------------");
            //System.out.println("Key, value; " +  key + ", " + new String(table.get(key)));
        }
    }

    public void printDBData(){

        if(this.localDB == null || this.localDB.size() == 0){
            // System.out.println("------------------ DB is not initialized! ------------------");
            this.localDB = new YCSBTable();
        }

        for(String tableName : localDB.keySet()){
            printTableData(tableName);
        }

    }
}
