package bftproject.ycsbapp.occ;

import bftsmart.tom.util.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CacheWrapper {

    private String tpid;
    private Map<String, String> cacheDatabase;
    private Set<String> readSet;
    private SerializeDatabase sdb;


    public CacheWrapper(String tpid, SerializeDatabase sdb){
        this.tpid = tpid;
        this.cacheDatabase = new HashMap<>();
        this.readSet = new HashSet<>();
        this.sdb = sdb;
    }

    public String read(String key){
        this.readSet.add(key);
        if (this.cacheDatabase.containsKey(key)) {
            return this.cacheDatabase.get(key);
        } else {
            return this.sdb.readLocalDatabase(key);
        }
    }

    public boolean write(String key, String val){
        this.cacheDatabase.put(key, val);
        return true;
    }

    public boolean commit(){
        for (String key : this.cacheDatabase.keySet()) {
            this.sdb.writeLocalDatabase(key, this.cacheDatabase.get(key));
            //this.sdb.writeLocalDatabase(key, this.cacheDatabase.get(key));
            //Logger.println("-----------COMMIT-------------" );
            //Logger.println(String.format("Key and Value: %s, %s", key, this.cacheDatabase.get(key)));
        }
        return true;
    }

    public void abort(){
        this.cacheDatabase.clear();
        this.readSet.clear();
        //System.out.println("Transaction Aborted!");
    }

    public Set<String> getReadSet() {
        return this.readSet;
    }

    public Set<String> getWriteSet(){
        return this.cacheDatabase.keySet();
    }

    protected void finalize(){
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //Logger.println(this.toString() + "now finalize:" + System.currentTimeMillis());
    }
}
