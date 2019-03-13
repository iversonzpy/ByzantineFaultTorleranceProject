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
package bftsmart.demo.ycsbapp.client;

import bftsmart.demo.ycsbapp.database.YCSBMessage;
import bftsmart.tom.ServiceProxy;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @Author  Minghui
 * @Description  YCSB test client.
 * @Usage Run scripts in ./runscripts/ycsbClient.sh
 *
 */
public class YCSBClient extends DB {

	private static AtomicInteger counter = new AtomicInteger();
	private static AtomicInteger tcounter = new AtomicInteger();

	private ServiceProxy proxy = null;
	private int myId;
	private String ptid = "";
	
	public YCSBClient() {
	}
	
	@Override
	public void init() {
		Properties props = getProperties();
		int initId = Integer.valueOf((String)props.get("smart-initkey"));
		myId = initId + counter.addAndGet(1);
		proxy = new ServiceProxy(myId);
		//Logger.println("YCSBKVClient. Initiated client id: " + myId);

	}

	@Override
	public int delete(String arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

//	@Override
	public int insert(String table, String key,
			HashMap<String, ByteIterator> values)  {

		ptid = myId + "_" + tcounter;
//		System.out.println("-------INSERT---------PTID : " + ptid);

        return update(table, key, values);
	}

	@Override
	public int read(String table, String key,
			Set<String> fields, HashMap<String, ByteIterator> result) {
		ptid = myId + "_" + tcounter;
//		System.out.println("-------READ---------PTID : " + ptid);
        YCSBTransactions transR = new YCSBTransactions(proxy, ptid);

        HashMap<String, byte[]> results = new HashMap<String, byte[]>();

        byte[] reply = null;


        reply = transR.read(table, key, fields, results);
		tcounter.getAndAdd(1);


		YCSBMessage replyMsg = YCSBMessage.getObject(reply);
		return replyMsg.getResult();
	}

	@Override
	public int scan(String arg0, String arg1, int arg2, Set<String> arg3,
			Vector<HashMap<String, ByteIterator>> arg4) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(String table, String key,
			HashMap<String, ByteIterator> values) {
		ptid = myId + "_" + tcounter;
//		System.out.println("-------UPDATE---------PTID : " + ptid);
        YCSBTransactions transU = new YCSBTransactions(proxy, ptid);

        HashMap<String, byte[]> results = new HashMap<String, byte[]>();

//        String, byte[]
		Set<String> fields = new HashSet<>();
		for (String field : values.keySet()) {
			fields.add(field);
		}


        transU.read(table, key, fields, results);


		Iterator<String> keys = values.keySet().iterator();
		HashMap<String, byte[]> map = new HashMap<String, byte[]>();
		while(keys.hasNext()) {
			String field = keys.next();
			//Logger.println("---------update- write----------");
			//Logger.println("field :" + field);
			map.put(field, values.get(field).toArray());
		}


		transU.write(table, key, map);


		byte[] reply = transU.commit();
        tcounter.getAndAdd(1);

		if(reply == null){
			System.out.println("Commit failed! Transaction ID = " + ptid);
			return -1;
		}else {

			return 0;
		}

		//YCSBMessage replyMsg = YCSBMessage.getObject(reply);
		//return replyMsg.getResult();
	}

}
