## BFT PROJECT

In this project, we implemented a distributed Key-Value Database System.  
It includes three layers, application layer,transaction layer and replication layer.

##### System Architecture




##### YCSB Test Usage

###### Start Replicas

To run YCSB (TEST_TYPE = 1), run below command
```bash
java -cp bin/:lib/* bftproject.ycsbapp.occ.SerializeDatabase $REPLICA_INDEX $TEST_TYPE

```
or run scripts
```bash
./runscripts/startReplicaYCSB.sh 0 1
./runscripts/startReplicaYCSB.sh 1 1
./runscripts/startReplicaYCSB.sh 2 1
./runscripts/startReplicaYCSB.sh 3 1
```

##### Start YCSB Client

./runscripts/ycsbClient.sh


##### Online Shopping User Application Usage / Operation Interactive OCC Test Usage

###### Start Replicas

set TEST_TYPE = 2, run below command
```bash
java -cp bin/:lib/* bftproject.ycsbapp.occ.SerializeDatabase $REPLICA_INDEX $TEST_TYPE

```
or run scripts
```bash
./runscripts/startReplicaYCSB.sh 0 2
./runscripts/startReplicaYCSB.sh 1 2
./runscripts/startReplicaYCSB.sh 2 2
./runscripts/startReplicaYCSB.sh 3 2
```
###### Start Online Shopping User Application Client 
Set Client_ID = 0,1,2,...
```bash
./runscripts/userclient_start.sh 0
./runscripts/userclient_start.sh 1
```
or 
###### Start Operation Interactive OCC Test Client
Set Client_ID = 0,1,2,...
```bash
./runscripts/testclient.sh 0
./runscripts/testclient.sh 1
```

Reference: BFT-Smart and Optimal Concurrency Control.



