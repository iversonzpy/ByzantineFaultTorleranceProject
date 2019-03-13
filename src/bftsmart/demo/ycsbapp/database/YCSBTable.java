package bftsmart.demo.ycsbapp.database;

import bftsmart.tom.util.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */



/**
 *
 * @author Marcel Santos
 *
 */
public class YCSBTable extends TreeMap<String, HashMap<String, byte[]>> implements Serializable {
    private static final long	serialVersionUID	= 3786544460082473686L;

    private Map<String, Map<String,byte[]>> tableMap = null;

    public YCSBTable() {
        tableMap = new TreeMap<String, Map<String,byte[]>>();
    }

    public Map<String,byte[]> addTable(String key, Map<String, byte[]> table) {
        return tableMap.put(key, table);
    }

    public byte[] addData(String tableName, String key, byte[] value) {

        Map<String, byte[]> table = tableMap.get(tableName);
        if (table == null) {
            //Logger.println("Error in addData(): Table does not exist! " + tableName);
            return null;
        }
        byte[] ret = table.put(key, value);
//        //Logger.println("--------------------------------");
//        //Logger.println("-------------ADD DATA-----------");
//        //Logger.println("Key = " + key + ", Value = " + new String(value));
//        //Logger.println("-------------AFTER EXECUTE------");
//        //Logger.println("Table name: " + tableName);
//        //Logger.println("Key = " + key + ", Value = " + new String(table.get(key)));
//        //Logger.println("--------------------------------");
        return ret;
    }

    public Map<String, byte[]> getTable(String tableName) {

        return tableMap.get(tableName);

    }

    public byte[] getEntry(String tableName, String key) {
        //Logger.println("Table name: "+ tableName);
        //Logger.println("Entry key: "+ key);
        Map<String,byte[]> info= tableMap.get(tableName);
        if (info == null) {
            //Logger.println("Error in getEntry(): Table does not exist! " + tableName);
            return null;
        }
        return info.get(key);
    }



}

