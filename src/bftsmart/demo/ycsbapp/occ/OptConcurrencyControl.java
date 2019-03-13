package bftsmart.demo.ycsbapp.occ;


import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.tom.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptConcurrencyControl {


    // key: tnc, value: CacheWrapper
    private Map<Integer, CacheWrapper> operationsWrapperMap;
    // key: tpid value: SerializeExecutor
    private Map<String, SerializeExecutor> operationExecutorMap;
    private SerializeDatabase sdb;
    private int tnc;
    //private SerializeExecutor serializeExecutor;

    public OptConcurrencyControl(SerializeDatabase sdb){
        this.tnc = 0;
        this.operationsWrapperMap = new HashMap<>();
        this.operationExecutorMap = new HashMap<>();
        this.sdb = sdb;
    }


    public int getTnc() {
        return this.tnc;
    }

    public void increaseTnc(){
        this.tnc++;
    }


    public String executeReadOperation(YCSBMessage message){

        if (!operationExecutorMap.containsKey(message.getPtid())){
            CacheWrapper cacheWrapper = new CacheWrapper(message.getPtid(), sdb);
            SerializeExecutor serializeExecutor =  new SerializeExecutor(cacheWrapper, this);
            operationExecutorMap.put(message.getPtid(), serializeExecutor);
        }
        SerializeExecutor existedSerializeExecutor = operationExecutorMap.get(message.getPtid());
        List<String> result = new ArrayList<>();

        for(String field: message.getFields()){
            result.add(existedSerializeExecutor.cache.read(field));
        }

        return String.join("," , result);
    }

    public boolean executeWriteOperation(YCSBMessage message){

        //Logger.println("---------------executeWriteOperation");
        //Logger.println("----- YCSBMessage: " + message.toString());

        if (!operationExecutorMap.containsKey(message.getPtid())){
            CacheWrapper cacheWrapper = new CacheWrapper(message.getPtid(), this.sdb);
            SerializeExecutor serializeExecutor = new SerializeExecutor(cacheWrapper, this);
            operationExecutorMap.put(message.getPtid(), serializeExecutor);
        }
        SerializeExecutor existedSerializeExecutor = operationExecutorMap.get(message.getPtid());

        for(Map.Entry<String, byte[]> entry: message.getValues().entrySet()){
            existedSerializeExecutor.cache.write(entry.getKey(), new String(entry.getValue()));
        }
        return true;
    }

    public boolean executeCommitOperation(YCSBMessage message){

        //Logger.println("---------------executeCommitOperation---------------");
        //Logger.println("----- YCSBMessage: " + message.toString());

        if (!this.operationExecutorMap.containsKey(message.getPtid())){
            CacheWrapper cacheWrapper = new CacheWrapper(message.getPtid(), this.sdb);
            SerializeExecutor serializeExecutor = new SerializeExecutor(cacheWrapper, this);
            operationExecutorMap.put(message.getPtid(), serializeExecutor);
        }
        SerializeExecutor existedSerializeExecutor = operationExecutorMap.get(message.getPtid());
        try {
            return existedSerializeExecutor.validate();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    public boolean commitAfterValidate(CacheWrapper cache) {
        //Logger.println("---------------commitAfterValidate---------------");
        if(cache.commit()){
            //Logger.println("---------------Commit succeeded!---------------");
            //cache.finalize();
            increaseTnc();
            operationsWrapperMap.put(tnc, cache);
            return true;
        }
        ////Logger.println("---------------Error: Can not commit.---------------");
        return false;
    }



    public CacheWrapper getCacheWrapperByTn(int tn){
        //Logger.println("---------------getCacheWrapperByTn---------------");
        //Logger.println("tn = " + tn);
        //Logger.println(operationsWrapperMap.keySet().toString());

        if(!operationsWrapperMap.containsKey(tn)){
            //System.out.println("---------------Error: no cache wrapper found!");
            return null;
        }
        return operationsWrapperMap.get(tn);
    }



}
